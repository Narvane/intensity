# Cron helper — install on the VPS (UTC 03:00):
#   0 3 * * * /opt/intensity/deploy/cron-reset-demo.sh >> /var/log/intensity-demo-reset.log 2>&1
set -euo pipefail
cd "$(dirname "$0")"
./reset-demo.sh
