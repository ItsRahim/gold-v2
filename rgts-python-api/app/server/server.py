import logging
from concurrent import futures
from datetime import datetime, timezone

import grpc

from ..proto import price_pb2
from ..proto import price_pb2_grpc

logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S'
)
logger = logging.getLogger(__name__)

class PriceServicer(price_pb2_grpc.GoldPriceServiceServicer):
    def GetGoldPrice(self, request, context):
        response = price_pb2.GoldPriceResponse()
        response.price = 1850.75
        response.source = "RGTS Gold Price API"
        response.datetime = datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M:%S")
        return response


def serve(host='localhost', port=50051):
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    price_pb2_grpc.add_GoldPriceServiceServicer_to_server(PriceServicer(), server)

    server_address = f'{host}:{port}'
    server.add_insecure_port(server_address)
    server.start()
    logger.info(f'Server started on {server_address}')

    try:
        server.wait_for_termination()
    except KeyboardInterrupt:
        logger.info("Shutting down server...")
        server.stop(0)