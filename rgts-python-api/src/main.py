import argparse
import crython
import time

@crython.job(expr='@minutely')
def do_job():
    print("Hello World")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Start the gRPC server')
    parser.add_argument('--bootstrap-server', default='localhost', help='The Kafka server host')
    parser.add_argument('--kafka-port', type=int, default=9093, help='The Kafka server port')

    args = parser.parse_args()

    crython.start()

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("\nShutting down...")
        crython.stop()
