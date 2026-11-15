#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")"

PAPER_REF=$(cat gradle.properties | perl -ne 'if (m/paperRef=([0-9a-f]+)/) { print $1 }')
PAPER_SHORT_REF=${PAPER_REF:0:7}

git add gradle.properties qanvas-api qanvas-server

if git diff --cached --quiet; then
    echo "Nothing to commit" >&2
    exit 1
fi

echo "Files that will be committed:"
git diff --cached --stat
echo

git commit -m "Updated Upstream (Paper / $PAPER_SHORT_REF)"
