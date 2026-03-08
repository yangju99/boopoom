#!/usr/bin/env bash
set -euo pipefail

./gradlew bootRun --args='--boopoom.seed.enabled=true --boopoom.seed.force=true --boopoom.seed.exit=true --spring.main.web-application-type=none'
