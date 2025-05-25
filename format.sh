#!/bin/bash
set -e

current_branch=$(git rev-parse --abbrev-ref HEAD)

if [ "$current_branch" == "main" ]; then
  echo "Cannot format and push to main branch"
  exit 0
fi

# Skip uncommitted check in CI
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

for file in $modified_files; do
  git add "$file"
done

# Commit and push
if [ -n "$CI" ]; then
  git fetch origin

  ref="$GITHUB_REF"
  branch_name="${ref#refs/heads/}"
  branch_name="${branch_name#refs/pull/}"
  branch_name="${branch_name%/merge}"
  branch_name="${branch_name%/head}"

  echo "Detected branch: $branch_name"

  git checkout -B "$branch_name"

  git config user.name "github-actions[bot]"
  git config user.email "github-actions[bot]@users.noreply.github.com"

  git commit -m "Pre-commit java formatting" || echo "No changes to commit"

  git push origin HEAD:"refs/heads/$branch_name"
else
  git push origin "$current_branch"
fi

echo "✅ Code formatted, committed, and pushed to $current_branch"