﻿## Core - RabbitMQ Server


Basic structure and architecture of a RabbitMQ Application

## Requirements
  - Java 19
  - RabbitMQ Server

## Getting started

### Configuration of Rabbit
If you want to use a RabbitMQ custom configuration, you will need change the values of enviroment variables that RabbitMQ use to establish the connection. These values will be founded in the file "application.yml", the path to file is: 

  src/main/resources/application.yml 

The variables for connection with default values are:
  - host: localhost
  - password: guest
  - port: 5672
  - username: guest

Remember that port 15672 is used for HTTP, you can get more info about port used in RabbitMQ in the following links:

  - [Networking and RabbitMQ](https://www.rabbitmq.com/networking.html)
  - [Troubleshooting Networking](https://www.rabbitmq.com/troubleshooting-networking.html)

### Build Steps
In order to build and run the JAR file you can use any of the following commands


- Compile the files, generate the JAR executable and run the Spring Application
  ```shell
  $ .\mvnw spring-boot:run
  ```

- Just compile the files without run the Spring Application
  ```shell
  $ mvnw clean package
  ```

- Run the JAR executable
  ```shell
  $ java -jar .\target\transport-0.0.1-SNAPSHOT.jar
  ```
