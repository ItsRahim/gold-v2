#!/bin/bash
set -e

# Print current working directory for debugging
echo "Current working directory: $(pwd)"
echo "Listing directory contents:"
ls -la

# Run database migrations with alembic
echo "Running database migrations..."
cd /app
alembic upgrade head

# Start both applications in parallel
echo "Starting API and Scheduler services..."
python -m backend.core.api.api_main &
API_PID=$!
echo "API service started with PID: $API_PID"

python -m backend.core.scheduler.scheduler_main &
SCHEDULER_PID=$!
echo "Scheduler service started with PID: $SCHEDULER_PID"

# Handle termination
trap 'echo "Caught signal, terminating processes..."; kill $API_PID $SCHEDULER_PID; exit' SIGTERM SIGINT

# Keep the container running
echo "Container is now running. Press Ctrl+C to stop."
wait $API_PID $SCHEDULER_PID