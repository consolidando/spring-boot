/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

@Profile({"flux"})
public interface CharacterFluxRepository extends ReactiveCrudRepository<CharacterFlux, Integer> 
{
    
    @Query("SELECT c.name AS name, c.id AS id FROM CHARACTERFLUX c")
    Flux<CharacterDto> findAllNameAndId();
}


