#!/bin/bash
set -e

# Don't run on main
current_branch=$(git rev-parse --abbrev-ref HEAD)
if [ "$current_branch" == "main" ]; then
  echo "Cannot format and push to main branch"
  exit 0
fi

# Check for uncommitted changes (only locally)
if [ -z "$CI" ]; then
  if ! git diff-index --quiet HEAD --; then
    echo "❌ Error: There are uncommitted changes. Please commit or stash them first."
    git status --porcelain
    exit 1
  fi
else
  echo "Running in CI, skipping uncommitted check"
fi

# Format the code
echo "Formatting code..."
mvn com.spotify.fmt:fmt-maven-plugin:format

# Stage modified files
modified_files=$(git ls-files --modified)
if [ -z "$modified_files" ]; then
  echo "No formatting changes needed."
  exit 0
fi

# Stage files one by one
for file in $modified_files; do
  git add "$file"
done

# Detect branch name in CI or local
branch_name="${GITHUB_HEAD_REF:-${GITHUB_REF_NAME:-$(git rev-parse --abbrev-ref HEAD)}}"
echo "Using branch: $branch_name"

# Handle detached HEAD in CI
if [ "$(git rev-parse --abbrev-ref HEAD)" = "HEAD" ]; then
  echo "Detached HEAD, creating branch $branch_name from current HEAD"
  git checkout -b "$branch_name"
fi

# Set author for CI
if [ -n "$CI" ]; then
  git config user.name "github-actions[bot]"
  git config user.email "github-actions[bot]@users.noreply.github.com"
fi

# Commit and push
git commit -m "Pre-commit java formatting" || echo "No changes to commit"
git push origin "$branch_name"

echo "✅ Code formatted, committed, and pushed to $branch_name"