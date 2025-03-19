import json
import logging
from typing import Optional
import requests
from bs4 import BeautifulSoup
from fake_useragent import UserAgent
from sqlalchemy.exc import SQLAlchemyError

from src.config import Config
from src.database.database_manager import DatabaseManager

logger = logging.getLogger(__name__)

ua = UserAgent().random
user_agent = {'User-Agent': ua}
FALLBACK_SOURCE_NAME = Config.get('FALLBACK_GOLD_SRC', 'uk-investing')


def get_gold_price() -> Optional[float]:
    """Fetch and parse the gold price from the source."""
    source = get_source()
    if not source:
        return None

    source_name, source_url, source_element = source

    source_element_parsed = json.loads(source_element)
    tag_name, attributes = source_element_parsed

    try:
        result = requests.get(source_url, headers=user_agent)
        result.raise_for_status()
        logger.info(f"Successfully fetched data from {source_name}")

        gold_data = BeautifulSoup(result.text, 'html.parser')
        price_element = gold_data.find(tag_name, attributes)

        if price_element:
            price = price_element.text.strip().replace(',', '')
            return round(float(price), 2)
        else:
            logger.warning(f"No price found for {source_name} using {source_element}")
            return None
    except requests.exceptions.RequestException as e:
        logger.error(f"Failed to fetch data from {source_name}: {e}")
    except Exception as e:
        logger.error(f"An unexpected error occurred while processing data from {source_name}: {e}")

    return None


def get_source() -> Optional[dict]:
    """
    Retrieve a source from the database.
    Returns:
        dict or None: A dictionary containing the source details if found, None otherwise.
    """
    query = (
        "SELECT source_name, source_url, source_element_data "
        "FROM price_sources "
        "WHERE source_is_active = :active"
    )

    fallback_query = (
        "SELECT source_name, source_url, source_element_data "
        "FROM price_sources "
        "WHERE source_endpoint = :fallback_source"
    )

    connection = DatabaseManager()

    try:
        source_data = get_active_source(connection, query)
        if source_data:
            return source_data
        else:
            logger.warning(
                f"Multiple active sources found or none available. Using fallback source: '{FALLBACK_SOURCE_NAME}'")
            return get_fallback_source(connection, fallback_query)
    except SQLAlchemyError as e:
        logger.error(f"Database query failed while retrieving source: {e}")
    return None


def get_active_source(connection: DatabaseManager, query: str) -> Optional[dict]:
    """Fetch the active source from the database."""
    params = {'active': 'true'}
    source = connection.execute_query(query, params)
    if source and len(source) == 1:
        return source[0]
    return None


def get_fallback_source(connection: DatabaseManager, query: str) -> Optional[dict]:
    """Fetch the fallback source if no active source is found."""
    params = {'fallback_source': FALLBACK_SOURCE_NAME}
    fallback_source = connection.execute_query(query, params)
    if fallback_source:
        return fallback_source[0]
    logger.warning(f"Fallback source '{FALLBACK_SOURCE_NAME}' not found.")
    return None
