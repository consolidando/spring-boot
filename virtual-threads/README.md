# Spring Boot - Tomcat with Virtual Threads vs. Reactor Netty

## Overview
This Spring Boot backend has been developed to test virtual threads. 
The server offers a REST API that provides access to a Postgres database. 
Depending on the profile chosen in Maven, a reactive version is generated 
based on event loop in Netty, or a servlet version based on the thread-per-request model with a Tomcat server.

## Maven Profiles

There are two Maven profiles defined in the project:

1. **tomcat**: This profile is activated by default and configures the application to run with Tomcat server in the thread-per-request model. It sets the active Spring profile to "servlet".

2. **netty**: This profile configures the application to run with Netty server based on event loop. It sets the active Spring profile to "flux".

## Properties

- `spring.threads.virtual.enabled`: `true` or `false` - enables virtual threads.
- `app.tomcat.virtual-threads.concurrency-limit`: `-1` for no limit, a number for maximum concurrency of virtual threads.

## JARs Generated According to Maven Profile

1. **vt-tomcat-{version}.jar**
2. **vt-netty-{version}.jar**

## Images Generated According to Maven Profile

These images are pushed to Docker.

1. **vt-tomcat:{version}**
2. **vt-netty:{version}**

## Docker Compose Files

You can find the Docker Compose files that allow you to generate the different scenarios at [GitHub - spring-boot/docker-compose/virtual-threads](https://github.com/consolidando/spring-boot/tree/main/docker-compose/virtual-threads)

## Reference Documentation

- [Spring Boot - Tomcat with virtual threads versus Reactor Netty](https://diy.elmolidelanoguera.com/2024/02/spring-boot-virtual-threads-versus.html)

## License

This project is licensed under the [CC BY-NC-ND 4.0](https://creativecommons.org/licenses/by-nc-nd/4.0/) License. See the [LICENSE](LICENSE.md) file for details.
