/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.vt.client;


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
