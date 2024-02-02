package com.elmoli.consolidando.vt.client;


import java.util.List;

/**
  * Represents information about an episode.
  * This GraphQL query requests the ID of the episode and the IDs of its associated characters.
  * Example query string: "{ episode(id: %d) { id characters { id } } }"
  */
public record EpisodeCharactersData(Data data) {
    public record Data(Episode episode) {
        public record Episode(int id, List<Character> characters) {}
    }

    public record Character(int id) {
    }
}

