import logging
from datetime import datetime, UTC

import crython

from backend.util.config import Config
from backend.infrastructure.kafka_handler import KafkaHandler
from backend.core.scraper.scraper import get_gold_price
from backend.proto.price_pb2 import GoldPriceInfo

logger = logging.getLogger(__name__)

kafka_handler = KafkaHandler()
gold_price_update_topic = Config.get('GOLD_PRICE_UPDATE_TOPIC', 'gold.price.update')


@crython.job(expr='@minutely')
def fetch_gold_price():
    """Fetch gold price and send it to Kafka."""
    source_name, price_data = get_gold_price()

    if source_name is not None and price_data is not None:
        request_time = datetime.now(UTC).isoformat()

        gold_price_info = GoldPriceInfo(
            source=source_name,
            price=price_data,
            datetime=request_time
        )

        message_data = gold_price_info.SerializeToString()

        kafka_handler.send_message(topic=gold_price_update_topic, message=message_data, delete_cached=True)
        logger.info(f"Sent gold price to Kafka: {price_data} at {request_time}")
    else:
        logger.warning("Failed to fetch gold price.")
