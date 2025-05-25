#!/bin/bash
set -e

# Detect current or target branch
branch_name="${GITHUB_HEAD_REF:-${GITHUB_REF_NAME:-$(git rev-parse --abbrev-ref HEAD)}}"
echo "🔎 Using branch: $branch_name"

# Abort if on main branch
if [ "$branch_name" == "main" ]; then
  echo "🚫 Cannot format and push to main branch"
  exit 0
fi

# Skip uncommitted check in CI
if [ -n "$CI" ]; then
  echo "✅ Running in CI, skipping uncommitted changes check"
else
  if ! git diff-index --quiet HEAD --; then
    echo "❌ Error: There are uncommitted changes. Please commit or stash them first."
    git status --porcelain
    exit 1
  fi
fi

# Format code
echo "🎨 Formatting code with Maven formatter..."
mvn com.spotify.fmt:fmt-maven-plugin:format

# Get modified files
modified_files=$(git ls-files --modified)
if [ -z "$modified_files" ]; then
  echo "✅ No formatting changes needed."
  exit 0
fi

# Stage all modified files safely
echo "$modified_files" | xargs -d '\n' git add

# Handle detached HEAD in CI
if [ "$(git rev-parse --abbrev-ref HEAD)" = "HEAD" ]; then
  echo "🔧 Detached HEAD detected, creating and switching to branch: $branch_name"
  git checkout -b "$branch_name"
fi

# Configure author in CI
if [ -n "$CI" ]; then
  git config user.name "github-actions[bot]"
  git config user.email "github-actions[bot]@users.noreply.github.com"
fi

# Commit changes
git commit -m "Pre-commit java formatting" || echo "ℹ️ No changes to commit"

# Rebase before pushing
echo "🔄 Pulling remote changes with rebase (if any)..."
git pull origin "$branch_name" --rebase || echo "⚠️ Rebase failed or not needed, continuing..."

# Push changes
echo "📤 Pushing to origin/$branch_name..."
git push origin "$branch_name"

echo "✅ Code formatted, committed, and pushed to $branch_name"