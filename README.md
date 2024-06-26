# Spring Boot Examples

This repository includes examples from the blog [Consolidando](http://diy.elmolidelanoguera.com/) with the label [Spring Boot](https://diy.elmolidelanoguera.com/search/label/Spring%20Boot)

## data-rest
A previously authenticated user using the `Google Identity authorization server` will be allowed to sign up for our application by entering relevant information, including an image. Part of the user information will be accessible publicly through an API.

To create the application, we will use Spring Boot. With `Spring Data REST`, we will create the API, use `Google Cloud Storage` to save the image, and `Google Datastore` to store user information. We will also document our API with `springdoc-openapi` and `Swagger UI`. Additionally, we discuss how to implement caching on the server and in the browser.

You can find this project in the subfolder [data-rest](https://github.com/consolidando/spring-boot/tree/main/data-rest).

## google-sign-in
We use `Google Sign-In` to generate an `ID token` in the browser application, which will accompany requests to our server in the form of `Bearer Authentication`. Our server can use this token to identify the user once its validity has been verified with the authorization server.

You can find this project in the subfolder [google-sign-in](https://github.com/consolidando/spring-boot/tree/main/google-sign-in).

## virtual-threads

Testing the performance of Spring Boot with `Virtual Threads` in `Tomcat` versus `Reactor Netty`.

You can find this project in the subfolder [virtual-threads](https://github.com/consolidando/spring-boot/tree/main/virtual-threads).

## License

This project is licensed under the [CC BY-NC-ND 4.0](https://creativecommons.org/licenses/by-nc-nd/4.0/) License. See the [LICENSE](LICENSE.md) file for details.