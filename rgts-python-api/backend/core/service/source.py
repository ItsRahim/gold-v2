import logging
from typing import Any, Coroutine

from sqlalchemy.exc import SQLAlchemyError

from backend.infrastructure.database.database_manager import DatabaseManager
from backend.util.config import Config

logger = logging.getLogger(__name__)

FALLBACK_SOURCE_NAME = Config.get('FALLBACK_GOLD_SRC', 'uk-investing')


async def get_source() -> tuple[str, str, str] | Coroutine[Any, Any, tuple[str, str, str] | None] | None:
    """
    Retrieve a source from the database.
    Returns:
        dict or None: A dictionary containing the source details if found, None otherwise.
    """
    query = Config.load_sql_from_file("queries/get_scraping_data_source.sql")

    fallback_query = Config.load_sql_from_file("queries/get_fallback_scraping_data_source.sql")

    connection = DatabaseManager()

    try:
        source_data = await get_active_source(connection, query)
        if source_data:
            return source_data
        else:
            logger.warning(
                f"Multiple active sources found or none available. Using fallback source: '{FALLBACK_SOURCE_NAME}'")
            return await get_fallback_source(connection, fallback_query)
    except SQLAlchemyError as e:
        logger.error(f"Database query failed while retrieving source: {e}")
    return None


async def get_active_source(connection: DatabaseManager, query: str) -> tuple[str, str, str] | None:
    """Fetch the active source from the database."""
    params = {'active': 'true'}
    source = await connection.execute_select_query(query, params)
    if source:
        return source[0]
    return None


async def get_fallback_source(connection: DatabaseManager, query: str) -> tuple[str, str, str] | None:
    """Fetch the fallback source if no active source is found."""
    params = {'fallback_source': FALLBACK_SOURCE_NAME}
    fallback_source = await connection.execute_select_query(query, params)
    if fallback_source:
        return fallback_source[0]
    logger.warning(f"Fallback source '{FALLBACK_SOURCE_NAME}' not found.")
    return None
