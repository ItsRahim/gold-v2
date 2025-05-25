#!/bin/bash
set -e

current_branch=$(git rev-parse --abbrev-ref HEAD)

if [ "$current_branch" == "main" ]; then
  echo "Cannot format and push to main branch"
  exit 0
fi

# Check if there are any uncommitted changes
if [ -n "$CI" ]; then
  echo "Running in CI, skipping uncommitted changes check"
else
  if ! git diff-index --quiet HEAD --; then
    echo "❌ Error: There are uncommitted changes. Please commit or stash them first."
    git status --porcelain
    exit 1
  fi
fi

# Format the code
echo "Formatting code..."
mvn com.spotify.fmt:fmt-maven-plugin:format

# Get modified files and stage them
modified_files=$(git ls-files --modified)
if [ -z "$modified_files" ]; then
  echo "No formatting changes needed."
  exit 0
fi

# Stage files one by one
for file in $modified_files; do
  git add "$file"
done

# Commit and push
git commit -m "Pre-commit java formatting"
git push origin "$current_branch"

echo "✅ Code formatted, committed, and pushed to $current_branch"