package com.example.demo.service;

public record EpisodeData(Data data) {
    public static record Data(Episodes episodes) {
        public static record Episodes(Info info) {
            public static record Info(int count) {}
        }
    }
}
