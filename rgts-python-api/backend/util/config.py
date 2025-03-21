import os
from dotenv import load_dotenv

load_dotenv()


class Config:
    @staticmethod
    def get(key, default=None):
        return os.getenv(key, default)

    @staticmethod
    def get_db_credentials():
        db_name = Config.get('DATABASE_NAME', 'gold')
        host = Config.get('DATABASE_HOST', 'localhost')
        port = Config.get('DATABASE_PORT', '5432')
        user = Config.get('DATABASE_USER', 'postgres')
        password = Config.get('DATABASE_PASSWORD', 'password')

        return f'postgresql+asyncpg://{user}:{password}@{host}:{port}/{db_name}'

    @staticmethod
    def load_sql_from_file(file_path: str) -> str:
        """Read SQL query from a file."""
        path = os.path.abspath(os.path.join(os.path.dirname( __file__ ), '..', file_path))
        try:
            with open(path, 'r') as file:
                return file.read()
        except FileNotFoundError:
            raise FileNotFoundError(f"The file at {file_path} was not found.")
        except Exception as e:
            raise Exception(f"An error occurred while reading the file: {str(e)}")
