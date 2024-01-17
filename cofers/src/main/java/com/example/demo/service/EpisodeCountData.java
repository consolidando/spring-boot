package com.example.demo.service;


/**
 * Represents data obtained from a GraphQL query to the Rick and Morty API 
 * for getting the episodes number.
 * The GraphQL query used is: "{ episodes { info { count } } }".
 * This data structure is designed to match the response format from the API.
 */
public record EpisodeCountData(Data data) {
    public static record Data(Episodes episodes) {
        public static record Episodes(Info info) {
            public static record Info(int count) {}
        }
    }
}
