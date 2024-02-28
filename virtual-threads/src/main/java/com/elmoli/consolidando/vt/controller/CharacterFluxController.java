/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.vt.controller;

import com.elmoli.consolidando.vt.metrics.CharacterIdStatistics;
import com.elmoli.consolidando.vt.metrics.RealTimeStatistics;
import com.elmoli.consolidando.vt.repository.CharacterDto;
import com.elmoli.consolidando.vt.repository.CharacterFlux;
import com.elmoli.consolidando.vt.repository.CharacterFluxRepository;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Profile(
        {
            "flux"
        })
@RestController
@RequestMapping("apis/characters")
public class CharacterFluxController
{

    private static final Logger logger = LoggerFactory.getLogger(CharacterFluxController.class);
    private final CharacterFluxRepository characterRepository;

    // 
    private final RealTimeStatistics getIdRealTimeStatistics = new RealTimeStatistics();
    private static final AtomicLong getIdConcurrentRequest = new AtomicLong(0L);
    private static final AtomicLong getIdMaximumConcurrentRequest = new AtomicLong(0L);

    public CharacterFluxController(CharacterFluxRepository characterRepository)
    {
        this.characterRepository = characterRepository;
    }

    private CharacterIdStatistics createCharacterIdStatistics()
    {

        return new CharacterIdStatistics(
                getIdRealTimeStatistics.getCount(),
                getIdMaximumConcurrentRequest.get(),
                getIdRealTimeStatistics.getMinimumRequestTime(),
                getIdRealTimeStatistics.getMaximumRequestTime(),
                getIdRealTimeStatistics.calculateMean(),
                getIdRealTimeStatistics.calculateStandardDeviation()
        );
    }

    private void resetStatistics()
    {
        getIdRealTimeStatistics.reset();
        getIdMaximumConcurrentRequest.set(0);
        getIdConcurrentRequest.set(0);
    }

    @GetMapping("/id/statistics")
    public Mono<ResponseEntity<CharacterIdStatistics>> getIdStatistics()
    {
        return Mono.just(ResponseEntity.ok(createCharacterIdStatistics()));
    }

    @DeleteMapping("/id/statistics")
    public Mono<ResponseEntity<Void>> deleteIdStatistics()
    {
        resetStatistics();
        return Mono.just(ResponseEntity.ok().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<CharacterFlux>> getCharacter(@PathVariable Integer id)
    {
        long startTime = System.nanoTime();

        long concurrentRequest = getIdConcurrentRequest.incrementAndGet();
        getIdMaximumConcurrentRequest.updateAndGet(current -> Math.max(current, concurrentRequest));

        return characterRepository.findById(id)
                .map(character ->
                {
                    long elapsedTime = System.nanoTime() - startTime;
//                    logger.info("-- Running GET {id} in thread: {}. Find time: {} ns", 
//                            id, Thread.currentThread(), elapsedTime);
                    getIdRealTimeStatistics.addValue(elapsedTime);
                    getIdConcurrentRequest.decrementAndGet();

                    return (ResponseEntity.ok(character));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/ids")
    public Flux<CharacterDto> getAllCharacters()
    {
        logger.info("-- Running GET in thread: {}", Thread.currentThread());
        return characterRepository.findAllNameAndId();
    }
}
