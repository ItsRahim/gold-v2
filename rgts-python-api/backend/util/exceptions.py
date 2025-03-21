from fastapi import HTTPException, status


def raise_bad_request_exception(detail: str = "Invalid request"):
    raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=detail)

def raise_forbidden_exception(detail: str = "Not authorised to perform this action"):
    raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail=detail)

def raise_not_found_exception(detail: str = "Resource not found"):
    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=detail)

def raise_internal_server_exception(detail: str = "Internal server error"):
    raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=detail)