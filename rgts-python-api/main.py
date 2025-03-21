import asyncio
import logging

import uvicorn
from apscheduler.schedulers.asyncio import AsyncIOScheduler
from apscheduler.triggers.date import DateTrigger
from apscheduler.triggers.interval import IntervalTrigger
from fastapi import FastAPI

from backend.core.api.source import router as price_source_router
from backend.core.cron.jobs import fetch_and_send

logger = logging.getLogger(__name__)

app = FastAPI()

app.include_router(price_source_router)


async def initialise_cron_jobs():
    """Start the price job at an interval (e.g., every minute)."""
    scheduler = AsyncIOScheduler()
    scheduler.add_job(fetch_and_send, DateTrigger())
    scheduler.add_job(fetch_and_send, IntervalTrigger(seconds=60))
    scheduler.start()

    try:
        while True:
            await asyncio.sleep(1)
    except (KeyboardInterrupt, SystemExit):
        logger.info("Stopping scheduler...")
        scheduler.shutdown()


async def run_app():
    """Run FastAPI app along with the price job."""
    asyncio.create_task(initialise_cron_jobs())

    # Run the FastAPI application using uvicorn
    config = uvicorn.Config(app, host="0.0.0.0", port=8000)
    server = uvicorn.Server(config)
    await server.serve()


if __name__ == "__main__":
    logger.info("Starting gold price scraper...")
    asyncio.run(run_app())
