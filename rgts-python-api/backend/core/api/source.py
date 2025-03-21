from fastapi import APIRouter, Depends, HTTPException
from backend.infrastructure.database.database_manager import DatabaseManager
from backend.schemas import PriceSource, GETPriceSourcesResponse
import logging
import json

from backend.util.config import Config
from backend.util.exceptions import raise_not_found_exception

router = APIRouter(prefix="/api/v1/price-sources", tags=["price-sources"])

logger = logging.getLogger(__name__)


def get_db_manager() -> DatabaseManager:
    return DatabaseManager()


@router.get("", response_model=GETPriceSourcesResponse)
def get_price_sources(db_manager: DatabaseManager = Depends(get_db_manager)):
    try:
        query = Config.load_sql_from_file("queries/get_all_price_sources.sql")
        price_sources = db_manager.execute_query(query)

        if not price_sources:
            raise_not_found_exception(detail="No price sources found")

        structured_sources = [
            PriceSource(
                name=source[1],
                endpoint=source[2],
                url=source[3],
                element=str(json.loads(source[4])),
                isActive=source[5]
            )
            for source in price_sources
        ]

        return GETPriceSourcesResponse(price_sources=structured_sources)

    except Exception as e:
        logger.error(f"Failed to retrieve price sources: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="Internal Server Error")
