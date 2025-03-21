import json
import logging
from typing import Any, Coroutine

from fastapi import APIRouter, Depends, HTTPException

from backend.infrastructure.database.database_manager import DatabaseManager
from backend.schemas import PriceSource, GETPriceSourcesResponse, POSTPriceSourceRequest
from backend.util.config import Config
from backend.util.exceptions import raise_not_found_exception, raise_internal_server_exception, \
    raise_bad_request_exception

router = APIRouter(prefix="/api/v2/price-sources", tags=["price-sources"])

logger = logging.getLogger(__name__)


def get_db_manager() -> DatabaseManager:
    return DatabaseManager()


@router.get("", response_model=GETPriceSourcesResponse)
async def get_price_sources(db_manager: DatabaseManager = Depends(get_db_manager)):
    try:
        query = Config.load_sql_from_file("queries/get_all_price_sources.sql")
        price_sources = await db_manager.execute_query(query)

        if not price_sources:
            raise_not_found_exception(detail="No price sources found")

        structured_sources = [
            PriceSource(
                id=source[0],
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
        raise_internal_server_exception("Failed to retrieve price sources")


@router.post("", response_model=None)
async def add_price_source(request: POSTPriceSourceRequest, db_manager: DatabaseManager = Depends(get_db_manager)):
    try:
        name = request.name
        endpoint = request.endpoint
        url = str(request.url)
        element = request.element.replace("'", '"')

        if not await check_if_price_source_exists(name, db_manager):
            await add_new_price_source(name, endpoint, url, element, db_manager)
            return {"message": "Price source added successfully."}
        else:
            raise_bad_request_exception(detail=f"Price source with name '{name}' already exists.")

    except HTTPException as e:
        raise e
    except Exception as e:
        logger.error(f"Failed to add price source: {str(e)}", exc_info=True)
        raise_internal_server_exception("Failed to add price source")


async def check_if_price_source_exists(name: str, db_manager: DatabaseManager) -> bool | None:
    try:
        query = Config.load_sql_from_file("queries/price_source_exists_by_name.sql")
        params = {'name': name}
        result = db_manager.execute_query(query, params)
        return bool(result)
    except Exception as e:
        logger.error(f"Error checking if price source exists: {str(e)}", exc_info=True)
        raise_internal_server_exception("Error checking if price source exists")


async def add_new_price_source(name: str, endpoint: str, url: str, element: str, db_manager: DatabaseManager) -> None:
    try:
        query = Config.load_sql_from_file("queries/add_price_source_query.sql")
        params = {
            'name': name,
            'endpoint': endpoint,
            'url': url,
            'element': element,
            'is_active': False
        }
        affected_rows = db_manager.execute_query(query, params)
        if not affected_rows:
            raise_bad_request_exception(detail="Failed to add price source")
        logger.info(f"Number of rows affected by the insert: {affected_rows}")
    except Exception as e:
        logger.error(f"Error adding price source: {str(e)}", exc_info=True)
        raise_internal_server_exception("Failed to add price source")


@router.post("/{source_id}/activate", response_model=None)
async def activate_source(source_id: str, db_manager: DatabaseManager = Depends(get_db_manager)):
    try:
        async with db_manager.session_scope() as session:
            if await deactivate_sources(db_manager, session):
                try:
                    int_id = int(source_id)
                except ValueError:
                    raise_bad_request_exception(detail="Invalid source ID. Must be an integer.")

                await activate_new_source(int_id, db_manager, session)
                return {"message": "Activated source successfully."}
            else:
                raise_bad_request_exception(detail="Failed to activate source")
    except HTTPException as e:
        raise e
    except Exception as e:
        logger.error(f"Failed to activate price source: {str(e)}", exc_info=True)
        raise_internal_server_exception("Failed to activate price source")



async def deactivate_sources(db_manager: DatabaseManager, session) -> bool | None:
    try:
        query = Config.load_sql_from_file("queries/deactivate_all_price_sources.sql")
        affected_rows = await db_manager.execute_query(query, session=session)
        if affected_rows == 0:
            raise_internal_server_exception("Failed to deactivate price source")
        return True
    except Exception as e:
        logger.error(f"Failed to deactivate price source: {str(e)}", exc_info=True)
        raise_internal_server_exception("Failed to deactivate price source")


async def activate_new_source(int_id: int, db_manager: DatabaseManager, session) -> None:
    try:
        query = Config.load_sql_from_file("queries/activate_price_source.sql")
        params = {'id': int_id}
        affected_rows = await db_manager.execute_query(query, params=params,session=session)
        if affected_rows == 0:
            raise_internal_server_exception("Failed to activate the price source")
    except Exception as e:
        logger.error(f"Failed to activate new price source: {str(e)}", exc_info=True)
        raise_internal_server_exception("Failed to activate price source")
