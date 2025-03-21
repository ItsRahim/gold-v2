import logging
from contextlib import asynccontextmanager

from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.ext.asyncio import create_async_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text

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
        logger.info("Initializing database")
        self.connection_url = Config.get_db_credentials()

        self.engine = create_async_engine(self.connection_url, pool_size=10, max_overflow=0, pool_pre_ping=True,pool_recycle=1800)

        self.Session = sessionmaker(self.engine, class_=AsyncSession, expire_on_commit=False)
        logger.info("Database connection initialized")

    @asynccontextmanager
    async def session_scope(self):
        """Provides a transactional scope around a series of operations."""
        session = self.Session()
        try:
            yield session
            await session.commit()
        except SQLAlchemyError as e:
            await session.rollback()
            logger.error(f"Database transaction error: {e}")
            raise
        except Exception as e:
            await session.rollback()
            logger.error(f"Unexpected session error: {e}")
            raise
        finally:
            await session.close()

    async def execute_query(self, query: str, params: dict[str, object] | None = None, session=None):
        if not hasattr(self, "engine"):
            self._initialize_db()
        try:
            if session:
                result = await session.execute(text(query), params or {})
            else:
                async with self.engine.connect() as conn:
                    result = await conn.execute(text(query), params or {})
            if result.returns_rows:
                return result.fetchall()
            else:
                return result.rowcount
        except SQLAlchemyError as e:
            logger.error(f"Database query execution failed: {e}")
            raise
