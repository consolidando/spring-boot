# Cofers technical test for backend

### Overview
When the application starts, it retrieves a random episode from the 
[Rick & Morty API](https://rickandmortyapi.com/) and stores the characters 
from that episode in an in-memory H2 database. The objective is to minimize 
the impact on the application's startup time by performing all API calls in 
parallel. Furthermore, the application exposes an endpoint `/apis/characters` 
that returns the list of stored characters in JSON format, displaying only 
the 'name' and 'status' fields.

### EpisodeApiClient Java class
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

### Spring Data REST


### database initialization at boot


### synchronization of tests


### index.html



### Reference Documentation
* [The Rick and Morty API - Documentation](https://rickandmortyapi.com/documentation)