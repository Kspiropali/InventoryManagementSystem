# Decentralised Inventory Management System
A complete web application featuring all sorts of authorization & authentication. Implements websockets
as well as RESTful API. The application is built using Springboot, pure JS, postgres and Redis.
[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)]()



### Deploying natively
### Prerequisites
* Java version 17, preferably amazon-corretto-17
* Gradle from: https://gradle.org/
* Postgres from: https://www.postgresql.org/
* Redis from: https://redis.io/
* Nginx from: https://www.nginx.com/
* (Optional) RabbitMQ from: https://www.rabbitmq.com/
```bash 
gradle build
gradle bootRun 
```

### Deploying with Docker

#### Prerequisites
* Java version 17, preferably amazon-corretto-17
* Gradle from: https://gradle.org/
* Docker from: https://www.docker.com/
* Docker-compose from: https://docs.docker.com/compose/install/

```docker
gradle build
docker-compose up
```
### Project Structure
* base local url: http://localhost
* base default port: 80
* Endpoints Documentation
* * Consult http://localhost/admin/swagger-ui.html for the endpoints documentation
* * Consult Decentralised Inventory Management System.postman_collection.json for the postman endpoints collection