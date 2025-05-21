#!/bin/bash

# Format the code
echo "Formatting code..."
mvn com.spotify.fmt:fmt-maven-plugin:format

# Stage all changes
git add .

# Check if there are staged changes
if git diff --cached --quiet; then
  echo "No changes to commit."
  exit 0
fi


# Commit and push
git commit -m "Pre-commit java formatting"

current_branch=$(git rev-parse --abbrev-ref HEAD)
git push origin "$current_branch"

echo "✅ Code formatted, committed, and pushed to $current_branch"
