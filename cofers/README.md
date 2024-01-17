# Cofers technical test for backend

### Overview
When the application starts, it retrieves a random episode from the 
[Rick & Morty API](https://rickandmortyapi.com/) and stores the characters 
from that episode in an in-memory H2 database. The objective is to minimize 
the impact on the application's startup time by performing all API calls in 
parallel. Furthermore, the application exposes an endpoint `/apis/characters` 
that returns the list of stored characters in JSON format, displaying only 
the 'name' and 'status' fields.

## EpisodeApiClient Java class
The `EpisodeApiClient` Java class serves as a client for the 
[Rick and Morty API](https://rickandmortyapi.com/). It is implemented as a 
Spring service, making use of the Spring WebClient for efficient HTTP 
communication with the API endpoints.

1. **Get Number of Episodes**: Retrieve the count of episodes from the Rick and 
Morty API using a GraphQL query to obtain only the necessary information.

2. **Get Episode Information**: Fetch details about a specific episode, 
including a list of character IDs present in that episode, using GraphQL 
queries for targeted data retrieval.

3. **Get Character Information**: Utilize the WebClient in a reactive manner to 
retrieve comprehensive details about a character by their ID from the Rick and Morty API.

4. **Parallel Calls**: Leverage reactive programming principles through the 
use of WebClient to make parallel calls, optimizing the efficiency of data retrieval.

## Spring Data Rest Integration

Spring Data Rest has been chosen for its seamless integration of built-in collection 
pagination and sorting, offering a paginated list of characters within the 
`_embedded` section. Additionally, it incorporates the `_links` section 
containing HATEOAS and the `page` section with page-related information.

In this utility, all HTTP verbs have been filtered, retaining only the GET method 
for retrieving collections of character projections (implemented through the 
`CharacterInfoProjection` interface). This ensures a streamlined and focused usage, 
specifically tailored for efficiently fetching character data.



## Database Application Initialization Process with Parallelism

The `run` method in `DemoApplication` leverages parallel processing for efficient 
execution during the application's initialization:

1. Retrieves the total number of episodes from the Rick and Morty API.
2. Selects a random episode.
3. Fetches information about characters in the selected episode.

   - Utilizes parallel processing with reactive programming (using Project Reactor).
   - Asynchronously retrieves detailed character information in parallel.
   - Saves character information in the repository and logs their names concurrently.

This parallelized approach optimizes the initialization process, enhancing the 
application's performance and responsiveness.

The `dataBaseState` variable in `DemoApplication` represents the state of the database initialization, allowing synchronization of tests. It is set to either `NOT_INITIALIZED` or `INITIALIZED` based on the database's readiness.

The `episodeId` variable holds the randomly selected episode number retrieved from the Rick and Morty API during the application's initialization process.


## Synchronization of Tests

The synchronization of tests relies on the `DemoApplication` `dataBaseState` to determine when the database is initialized, enabling the commencement of content tests. This synchronization ensures a controlled and reliable environment for validating the functionality of the application's database interactions. 

Additionally, the tests can obtain the `episodeId` from `DemoApplication` to understand which episode's information should be present in the database. This ensures that the tests have insight into the expected data in the database based on the randomly selected episode during application initialization.

## Welcome Page and Swagger UI

Upon application startup, a welcome page (`index.html`) is available with links 
to various endpoints:

- [/swagger-ui.html](http://localhost:8080/swagger-ui.html): Explore and interact with the API using Swagger documentation.
- [/apis](http://localhost:8080/apis): Access the main API endpoint.
- [/apis/characters](http://localhost:8080//apis/characters): Retrieve information about characters.
- [/apis-docs](http://localhost:8080/apis-docs): Access additional API documentation.
- [/h2-console](http://localhost:8080/h2-console): H2 console, (user=sa, password=password).

## Executable - fat Jar

## application.yaml

### Reference Documentation
* [The Rick and Morty API - Documentation](https://rickandmortyapi.com/documentation)