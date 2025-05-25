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

# Move report from known location (scheduler service)
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

# Configure Git for CI
if [ -n "$CI" ]; then
  git config user.name "github-actions[bot]"
  git config user.email "github-actions[bot]@users.noreply.github.com"
fi

# Determine current or target branch
branch_name="${GITHUB_HEAD_REF:-${GITHUB_REF_NAME:-$(git rev-parse --abbrev-ref HEAD)}}"
echo "📌 Using branch: $branch_name"

# Handle detached HEAD
if [ "$(git rev-parse --abbrev-ref HEAD)" = "HEAD" ]; then
  echo "🔧 Detached HEAD detected, checking out branch $branch_name"
  git checkout -b "$branch_name"
fi

# Rebase in case of remote changes
git pull origin "$branch_name" --rebase || echo "⚠️ Rebase failed, continuing..."

# Add, commit and push report
git add "$REPORT_DST"

if git diff --staged --quiet; then
  echo "ℹ️ No changes to commit"
else
  git commit -m "Add OWASP Dependency-Check report [ci skip]"
  git push origin "$branch_name"
  echo "✅ Report committed and pushed"
fi