FROM python:3.13-slim

WORKDIR /app

RUN apt-get update \
    && apt-get install -y libpq-dev build-essential \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \

RUN pip install --no-cache-dir pipenv

COPY rgts-python-api/Pipfile rgts-python-api/Pipfile.lock ./

RUN python -m pipenv install --deploy --system

COPY rgts-python-api/startup.sh /app/startup.sh

COPY . .

RUN chmod +x /app/startup.sh

CMD ["/app/startup.sh"]

EXPOSE 8000
LABEL authors="Rahim Ahmed"