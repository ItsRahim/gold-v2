import json
import logging
import redis.asyncio as redis
from backend.core.service.source import get_source
from backend.util.config import Config

logger = logging.getLogger(__name__)

redis_url = Config.get_redis_credentials()
redis_client = redis.from_url(redis_url, db=0)

CACHE_KEY = "scraper_settings"
INIT_FLAG_KEY = "isSourcesInitialised"


async def initialise_cache():
    try:
        is_initialised = await redis_client.get(INIT_FLAG_KEY)

        if not is_initialised:
            logger.info("Cache not initialised. Fetching data and storing to Redis cache")

            active_settings = await get_active_scraper_settings_from_db()
            if active_settings:
                await store_to_cache(active_settings)

                logger.info(f"Cache initialized with {len(active_settings)} active sources.")
            else:
                logger.warning("No active sources found. Cache not initialized.")
        else:
            logger.info("Cache already initialised.")

    except Exception as e:
        logger.error(f"Error initializing cache: {e}")


async def update_cache():
    try:
        active_settings = await get_active_scraper_settings_from_db()

        if active_settings:
            await store_to_cache(active_settings)

        else:
            logger.info("No active sources found to update cache.")

    except Exception as e:
        logger.error(f"Error updating cache: {e}")


async def get_scraper_settings():
    try:
        cached_settings = await redis_client.get(CACHE_KEY)

        if cached_settings:
            logger.info("Loaded from Redis cache")
            return json.loads(cached_settings)

        logger.info("Cache empty, fetching from database")
        active_settings = await get_active_scraper_settings_from_db()

        if active_settings:
            await store_to_cache(active_settings)

        return active_settings

    except Exception as e:
        logger.error(f"Error retrieving scraper settings: {e}")
        return None


async def get_active_scraper_settings_from_db():
    try:
        active_sources = await get_source()

        if not active_sources:
            logger.warning("No active sources found in database.")
            return None

        active_sources_dict = {
            "name": active_sources[0],
            "url": active_sources[1],
            "element": json.loads(active_sources[2])
        }

        return active_sources_dict

    except Exception as e:
        logger.error(f"Error fetching active scraper settings from database: {e}")
        return None


async def store_to_cache(active_settings):
    try:
        await redis_client.set(CACHE_KEY, json.dumps(active_settings))
        await redis_client.set(INIT_FLAG_KEY, "True")
        logger.info(f"Stored {len(active_settings)} active sources to Redis cache.")

    except Exception as e:
        logger.error(f"Error storing data to cache: {e}")
