import logging
import time

import crython
import uvicorn
from fastapi import FastAPI

from backend.core.routes.source import router as price_source_router

logger = logging.getLogger(__name__)

app = FastAPI()

app.include_router(price_source_router)

if __name__ == '__main__':
    logger.info("Starting gold price scraper...")
    crython.start()

    uvicorn.run(app, host="0.0.0.0", port=8000)

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        logger.info("Stopping scheduler...")
        crython.stop()
