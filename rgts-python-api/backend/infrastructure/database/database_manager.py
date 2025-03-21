import logging
from contextlib import contextmanager

from sqlalchemy import create_engine, text
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.orm import sessionmaker

from backend.util.config import Config

logger = logging.getLogger(__name__)

class DatabaseManager:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(DatabaseManager, cls).__new__(cls)
            cls._instance._initialize_db()
        return cls._instance

    def _initialize_db(self):
        logger.info("Initialising database")
        self.connection_url = Config.get_db_credentials()
        self.engine = create_engine(self.connection_url, pool_size=10, max_overflow=0, pool_pre_ping=True,
                                    pool_recycle=1800)
        self.Session = sessionmaker(bind=self.engine)
        logger.info("Database connection initialized")

    def execute_query(self, query: str, params: dict[str, object] | None = None):
        if not hasattr(self, "engine"):
            self._initialize_db()
        try:
            with self.engine.connect() as conn:
                result = conn.execute(text(query), params or {})
                return result.fetchall()
        except SQLAlchemyError as e:
            logger.error(f"Database query execution failed: {e}")
            raise

    @contextmanager
    def session_scope(self):
        """Provides a transactional scope around a series of operations."""
        session = self.Session()
        try:
            yield session
            session.commit()
        except SQLAlchemyError as e:
            session.rollback()
            logger.error(f"Database transaction error: {e}")
            raise
        except Exception as e:
            session.rollback()
            logger.error(f"Unexpected session error: {e}")
            raise
        finally:
            session.close()

    def close_connection(self):
        """Closes the database connection and disposes of the engine."""
        try:
            self.engine.dispose()
            logger.info("Database connection closed successfully.")
        except Exception as e:
            logger.error(f"Error closing database connection: {e}")
            raise
