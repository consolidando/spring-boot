package com.example.demo.service;


import java.util.List;

public record EpisodeInfoData(Data data) {
    public record Data(Episode episode) {
        public record Episode(int id, List<Character> characters) {}
    }

    public record Character(int id) {
    }
}

