#!/bin/bash
set -e

echo "Running OWASP Dependency-Check..."

# Create security reports directory
mkdir -p security-reports

# Run OWASP Dependency-Check
mvn org.owasp:dependency-check-maven:check \
  -DnvdApiKey="${NVD_API_KEY}" \
  -Dformat=HTML \
  -DoutputDirectory=security-reports

echo "Moving report to security-reports directory..."

# Move the generated report to the expected location
if [ -f "rgts-scheduler-service/target/dependency-check-report.html" ]; then
  mv rgts-scheduler-service/target/dependency-check-report.html security-reports/dependency-check.html
  echo "Report moved successfully"
else
  echo "❌ Error: dependency-check-report.html not found in expected location"
  echo "Looking for report files..."
  find . -name "dependency-check-report.html" -type f 2>/dev/null || echo "No dependency-check-report.html files found"
  exit 1
fi

echo "Committing OWASP report..."

# Configure git
git config --global user.name 'github-actions[bot]'
git config --global user.email 'github-actions[bot]@users.noreply.github.com'

# Fetch latest changes and checkout the correct branch
git fetch origin

# Determine branch name based on environment
if [ -n "$GITHUB_HEAD_REF" ]; then
  branch_name="$GITHUB_HEAD_REF"
elif [ -n "$GITHUB_REF_NAME" ]; then
  branch_name="$GITHUB_REF_NAME"
else
  branch_name=$(git rev-parse --abbrev-ref HEAD)
fi

echo "Checking out branch: $branch_name"
git checkout "$branch_name"

# Pull latest changes to avoid conflicts
git pull origin "$branch_name" --rebase || echo "Rebase failed, continuing..."

# Add and commit the report
git add security-reports/dependency-check.html

if git diff --staged --quiet; then
  echo "No changes to commit - report unchanged"
else
  git commit -m "Add OWASP Dependency-Check report [ci skip]"
  git push origin "$branch_name"
  echo "✅ OWASP report committed and pushed successfully"
fi