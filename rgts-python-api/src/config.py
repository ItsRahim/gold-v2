import os
from dotenv import load_dotenv

load_dotenv()


class Config:
    @staticmethod
    def get(key, default=None):
        return os.getenv(key, default)
