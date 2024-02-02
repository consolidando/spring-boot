/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

@Profile({"flux"})
public interface CharacterFluxRepository extends ReactiveCrudRepository<Character, Long> 
{
    
    //@Query("SELECT c.name AS name, c.status AS status FROM CHARACTER c")
    //Flux<CharacterDto> findAll();
}


