# iot_slackbotc

## Example of docker-compose.yml
```yaml
version: '2'

services:
  slackbot:
    container_name: slackbot
    image: carrotlicious/iot_slackbotc-armv32v7:0.0.1
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - /etc/timezone:/etc/timezone:ro
    restart: always
    ports:
      - "8080:8080"
    environment:
      - "JAVA_OPTS=-Xmx512m -Xms128m"
      - "SLACK_TOKEN=xxxxxx"
      - "spring_profiles_active=staging"
```
