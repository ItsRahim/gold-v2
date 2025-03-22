import logging

import requests
from bs4 import BeautifulSoup
from fake_useragent import UserAgent

from backend.infrastructure.database.redis_manager import get_scraper_settings

logger = logging.getLogger(__name__)

ua = UserAgent().random
user_agent = {'User-Agent': ua}


async def get_gold_price() -> tuple[str, float] | None:
    """Fetch and parse the gold price from the source."""
    source = await get_scraper_settings()
    if not source:
        return None

    source_name = source['name']
    source_url = source['url']
    source_element = source['element']

    tag_name, attributes = source_element[0], source_element[1]

    try:
        result = requests.get(source_url, headers=user_agent)
        result.raise_for_status()
        logger.info(f"Successfully fetched data from {source_name}")

        gold_data = BeautifulSoup(result.text, 'html.parser')
        price_element = gold_data.find(tag_name, attributes)

        if price_element:
            price = price_element.text.strip().replace(',', '')
            return source_name, round(float(price), 2)
        else:
            logger.warning(f"No price found for {source_name} using {source_element}")
            return None
    except requests.exceptions.RequestException as e:
        logger.error(f"Failed to fetch data from {source_name}: {e}")
    except Exception as e:
        logger.error(f"An unexpected error occurred while processing data from {source_name}: {e}")

    return None
