#!/bin/bash
set -e

echo "🔍 Running OWASP Dependency-Check..."

# Create output directory
mkdir -p security-reports

# Run dependency check
mvn org.owasp:dependency-check-maven:check \
  -DnvdApiKey="${NVD_API_KEY}" \
  -Dformat=HTML \
  -DoutputDirectory=security-reports

# Move report
REPORT_SRC="rgts-scheduler-service/target/dependency-check-report.html"
REPORT_DST="security-reports/dependency-check.html"

if [ -f "$REPORT_SRC" ]; then
  mv "$REPORT_SRC" "$REPORT_DST"
  echo "✅ Report moved to $REPORT_DST"
else
  echo "❌ Report not found at $REPORT_SRC"
  find . -name "dependency-check-report.html" -type f || echo "No report found"
  exit 1
fi

# Git setup (only for CI)
if [ -n "$CI" ]; then
  git config user.name "github-actions[bot]"
  git config user.email "github-actions[bot]@users.noreply.github.com"
fi

# Determine the current branch
branch_name="${GITHUB_HEAD_REF:-${GITHUB_REF_NAME:-$(git rev-parse --abbrev-ref HEAD)}}"
echo "📌 Using branch: $branch_name"

# Handle detached HEAD
if [ "$(git rev-parse --abbrev-ref HEAD)" = "HEAD" ]; then
  echo "🔧 Detached HEAD detected, checking out branch: $branch_name"
  git checkout -B "$branch_name"
fi

# Stage the report
git add "$REPORT_DST"

# Commit if changes exist
if git diff --staged --quiet; then
  echo "ℹ️ No changes to commit"
else
  git commit -m "Add OWASP Dependency-Check report [ci skip]"
fi

# Pull latest with rebase — force clean working tree first
git reset --hard
git clean -fd
git pull origin "$branch_name" --rebase || echo "⚠️ Rebase failed or unnecessary, continuing..."

# Push safely
git push origin "$branch_name" || {
  echo "❗ Push failed. Attempting force-with-lease..."
  git push --force-with-lease origin "$branch_name"
}

echo "✅ Report processed and pushed to $branch_name"