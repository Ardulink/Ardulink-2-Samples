#!/bin/bash

if [ -f /data/package.json ]; then
    echo "Installing Node-RED dependencies from package.json..."
    cd /data
    npm install --no-fund --no-update-notifier --only=production
fi

echo "Starting Node-RED..."
node-red -u /data
