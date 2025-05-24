FROM python:3.13-slim

WORKDIR /app

COPY Pipfile ./
COPY Pipfile.lock ./

RUN apt-get update \
    && apt-get install -y libpq-dev build-essential \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN pip install pipenv && pipenv --version

COPY . .

RUN python -m pipenv install --deploy --system

COPY startup.sh /app/startup.sh
RUN chmod +x /app/startup.sh
CMD ["/app/startup.sh"]

EXPOSE 8000
LABEL authors="Rahim Ahmed"