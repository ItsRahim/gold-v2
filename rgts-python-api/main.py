import argparse
from app.server import server

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Start the gRPC server')
    parser.add_argument('--host', default='localhost', help='The server host')
    parser.add_argument('--port', type=int, default=50051, help='The server port')

    args = parser.parse_args()
    server.serve(host=args.host, port=args.port)