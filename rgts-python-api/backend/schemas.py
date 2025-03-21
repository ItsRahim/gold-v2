from typing import Optional, List

from pydantic import BaseModel, HttpUrl, Field


class PriceSource(BaseModel):
    name: str
    endpoint: str
    url: HttpUrl
    element: str
    is_active: Optional[bool] = Field(False, alias="isActive")

    class Config:
        populate_by_name = True
        from_attributes = True


class GETPriceSourcesResponse(BaseModel):
    price_sources: List[PriceSource]


class POSTPriceSourceRequest(PriceSource):
    pass


class POSTPriceSourceUpdateRequest(BaseModel):
    endpoint: Optional[str] = None
    url: Optional[HttpUrl] = None
    element: Optional[List[str]] = None


class PriceSourceActivate(BaseModel):
    name: str
