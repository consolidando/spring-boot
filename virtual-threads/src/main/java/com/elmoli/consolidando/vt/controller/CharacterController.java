/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.vt.controller;

import com.elmoli.consolidando.vt.metrics.CharacterIdStatistics;
import com.elmoli.consolidando.vt.metrics.RealTimeStatistics;
import com.elmoli.consolidando.vt.repository.Character;
import com.elmoli.consolidando.vt.repository.CharacterDto;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author joanr
 */
@Profile(
        {
            "servlet"
        })
@RestController
@RequestMapping("apis/characters")
public class CharacterController
{

    private static final Logger logger = LoggerFactory.getLogger(CharacterController.class);        
    // 
    private static final RealTimeStatistics getIdRealTimeStatistics = new RealTimeStatistics();
    private static final AtomicLong getIdConcurrentRequest = new AtomicLong(0L);
    private static final AtomicLong getIdMaximumConcurrentRequest = new AtomicLong(0L);
    
    private final CharacterRepository characterRepository;

    public CharacterController(CharacterRepository characterRepository)
    {
        this.characterRepository = characterRepository;
    }
    
    @GetMapping("/id/statistics")
    public ResponseEntity<CharacterIdStatistics> getIdStatistics()
    {
        
        CharacterIdStatistics characterIdStatistics = 
            new CharacterIdStatistics(
                    getIdRealTimeStatistics.getCount(),
                    getIdMaximumConcurrentRequest.get(), 
                    getIdRealTimeStatistics.getMinimumRequestTime(),
                    getIdRealTimeStatistics.getMaximumRequestTime(),
                    getIdRealTimeStatistics.calculateMean(),
                    getIdRealTimeStatistics.calculateStandardDeviation()
            );
        
        return(ResponseEntity.ok(characterIdStatistics));
        
    }
    
    @DeleteMapping("/id/statistics")
    public ResponseEntity<Void> deleteIdStatistics()
    {
        getIdRealTimeStatistics.reset();
        getIdMaximumConcurrentRequest.set(0);
        getIdConcurrentRequest.set(0);
        return(ResponseEntity.ok().build());
    }
         

    @GetMapping("/{id}")
    public ResponseEntity<Character> getCharacter(@PathVariable Integer id)
    {
        long startTime = System.nanoTime(); 
        
        long concurrentRequest = getIdConcurrentRequest.incrementAndGet(); 
        getIdMaximumConcurrentRequest.updateAndGet(current -> Math.max(current, concurrentRequest));
                       
        Optional<Character> characterOptional = characterRepository.findById(id);
        
        getIdConcurrentRequest.decrementAndGet();
        if (characterOptional.isPresent())
        {
            long elapsedTime = System.nanoTime()- startTime; 
            //logger.info("-- Running GET {} in thread: {}. find time: {} ns", 
            //        id, Thread.currentThread(), elapsedTime);
            getIdRealTimeStatistics.addValue(elapsedTime);

            Character character = characterOptional.get();
            return ResponseEntity.ok(character);
        } else
        {
            return ResponseEntity.notFound().build();
        }
    }
    


    @GetMapping("/ids")
    public List<CharacterDto> getAllCharacters()
    {
        logger.info("-- Running GET in thread: {}", Thread.currentThread());
        return characterRepository.findAllNameAndId();
    }

}
