import logging
from contextlib import contextmanager

from sqlalchemy import create_engine, text
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.orm import sessionmaker, Query

from src.config import Config

logger = logging.getLogger(__name__)


class DatabaseManager:
    _instance = None

    def __init__(self):
        if not hasattr(self, "engine"):
            self._initialize_db()

    def _initialize_db(self):
        logger.info("Initialising database")
        db_name = Config.get('DATABASE_NAME', 'gold')
        host = Config.get('DATABASE_HOST', 'localhost')
        port = Config.get('DATABASE_PORT', '5432')
        user = Config.get('DATABASE_USER', 'postgres')
        password = Config.get('DATABASE_PASSWORD', 'password')

        self.connection_url = f'postgresql://{user}:{password}@{host}:{port}/{db_name}'
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
