import logging
from collections import deque

import crython
from kafka import KafkaProducer

from src.config import Config

logger = logging.getLogger(__name__)


class KafkaHandler:
    _message_cache = deque(maxlen=1000)
    _producer = None
    _bootstrap_server = Config.get("KAFKA_BOOTSTRAP_SERVERS", "localhost:9093")

    @classmethod
    def get_producer(cls):
        """Initializes Kafka Producer if not already initialized."""
        if cls._producer is None:
            try:
                cls._producer = KafkaProducer(bootstrap_servers=[cls._bootstrap_server])
                logger.info("Kafka producer initialized successfully")
            except Exception as e:
                logger.error(f"Failed to initialize Kafka producer: {e}")
                cls._producer = None
        return cls._producer

    @classmethod
    def retry_failed_messages(cls):
        """Attempts to resend cached messages."""
        producer = cls.get_producer()

        if not producer or not cls._message_cache:
            return

        while cls._message_cache:
            topic, message = cls._message_cache.popleft()
            try:
                future = producer.send(topic, message)
                future.get(timeout=10)
                logger.info(f"Retried message sent to {topic} successfully")
            except Exception as e:
                logger.error(f"Retry failed for {topic}: {e}. Adding back to cache.")
                cls._message_cache.appendleft((topic, message))
                break

    def send_message(self, topic: str, message: bytes, delete_cached: bool = False) -> None:
        """Attempts to send a message to Kafka. If it fails, caches the message for retry."""
        producer = self.get_producer()
        if producer:
            try:
                future = producer.send(topic, message)
                future.get(timeout=10)
                logger.info(f"Message sent to {topic} successfully")
                if delete_cached:
                    # Remove all caches messages with a specific topic (usually outdated live prices)
                    logger.info(f"Deleting cached message for {topic}")
                    KafkaHandler._message_cache = deque(
                        ((t, _) for t, _ in KafkaHandler._message_cache if t != topic),
                        maxlen=KafkaHandler._message_cache.maxlen
                    )
            except Exception as e:
                logger.error(f"Failed to send message to {topic}: {e}")
                KafkaHandler._message_cache.append((topic, message))
        else:
            logger.error("Kafka producer is not initialized. Message not sent.")
            KafkaHandler._message_cache.append((topic, message))


@crython.job(expr='@minutely')
def schedule_retry():
    KafkaHandler.retry_failed_messages()


crython.start()
