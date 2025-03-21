import asyncio
import logging

from apscheduler.schedulers.asyncio import AsyncIOScheduler
from apscheduler.triggers.date import DateTrigger
from apscheduler.triggers.interval import IntervalTrigger

from backend.core.scheduler.jobs import fetch_and_send
from backend.infrastructure.kafka_handler.KafkaHandler import schedule_retry

logger = logging.getLogger(__name__)

async def initialise_cron_jobs():
    scheduler = AsyncIOScheduler()
    scheduler.add_job(schedule_retry, IntervalTrigger(seconds=60))
    scheduler.add_job(fetch_and_send, DateTrigger())
    scheduler.add_job(fetch_and_send, IntervalTrigger(seconds=60))
    scheduler.start()

    try:
        while True:
            await asyncio.sleep(1)
    except (KeyboardInterrupt, SystemExit):
        logger.info("Stopping scheduler...")
        scheduler.shutdown()

if __name__ == "__main__":
    logger.info("Starting the scheduler...")
    asyncio.run(initialise_cron_jobs())