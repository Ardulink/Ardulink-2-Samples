Start [node-red](https://nodered.org/) and [mosquitto](https://mosquitto.org/) on your local machine (prerequisites is that you have [docker](https://www.docker.com/) installed)
```bash
docker-compose up -d
```

Run ardulink-mqtt with the connection (Arduino) you want to control (e.g. the virtual console for testing)
```bash
java -jar ardulink-mqtt-2.2.0.jar -connection ardulink://virtual-console
```

navigate your browser to http://localhost:1880/ui to play around with the widget. Changes made should be seen at the connected Arduino (in this case in the console because we did use virtual-console for testing). 

navigate your browser to http://localhost:1880/ if you want to see or change the node-red flow. 

