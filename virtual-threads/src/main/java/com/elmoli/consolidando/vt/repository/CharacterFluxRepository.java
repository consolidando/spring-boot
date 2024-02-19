/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
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


