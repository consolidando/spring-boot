/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.client;

import java.time.Instant;
import java.util.List;

/**
 *
 * @author joanr
 */
public record EpisodeCharactersData(Data data){
    public record Data(Episode episode){
        public record Episode(int id, List<Character> characters){
        }
    }

    public record Character(
            int id,
            String name,
            String status,
            String species,
            String gender,
            OriginInfo origin,
            LocationInfo location,
            String image,
            List<Episodes> episode,
            Instant created)
            {
        
        public record Episodes(String id){
        }

        public record OriginInfo(
                String name,
                String id){
        }

        public record LocationInfo(
                String name,
                String id){
        }
    }
}
