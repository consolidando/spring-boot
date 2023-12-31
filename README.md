# Spring Boot Examples

This repository includes examples from the blog [Consolidando](http://diy.elmolidelanoguera.com/) with the label "Spring Boot"
 
## google-sign-in
We use Google Sign-In to generate an ID token in the browser application, which will accompany requests to our server in the form of Bearer Authentication. Our server can use this token to identify the user once its validity has been verified with the authorization server.

POST: [Spring OAuth 2.0 Resource Server - Google Sign In ](https://diy.elmolidelanoguera.com/2023/11/seguridad-autentificacion-spring-boot.html)

## Spring Boot REST + Google Sign In + Google Datastore
A previously authenticated user using the Google Identity authorization server will be allowed to sign up for our Consolidating application by entering relevant information, including an image. Part of the user information will be accessible publicly through an API.

To create the application, we will use Spring Boot. With Spring Data REST, we will create the API, use Google Cloud Storage to save the image, and Google Datastore to store user information. We will also document our API with springdoc-openapi and Swagger UI. Additionally, we discuss how to implement caching on the server and in the browser.

POST: [Working on it ](https://diy.elmolidelanoguera.com)