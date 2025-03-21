import logging
import uvicorn
from fastapi import FastAPI
from backend.core.api.source import router as price_source_router

logger = logging.getLogger(__name__)

app = FastAPI()
app.include_router(price_source_router)

if __name__ == "__main__":
    logger.info("Starting FastAPI server...")
    uvicorn.run(app, host="0.0.0.0", port=8000)
