/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.controller;

import com.elmoli.consolidando.vt.repository.CharacterDto;
import com.elmoli.consolidando.vt.repository.CharacterFlux;
import com.elmoli.consolidando.vt.repository.CharacterFluxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
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

    public CharacterFluxController(CharacterFluxRepository characterRepository)
    {
        this.characterRepository = characterRepository;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<CharacterFlux>> getCharacter(@PathVariable Integer id)
    {
        logger.info("-- Running GET in thread: {}", Thread.currentThread());

        return characterRepository.findById(id)
                .map(character -> ResponseEntity.ok(character))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/ids")
    public Flux<CharacterDto> getAllCharacters()
    {
        logger.info("-- Running GET in thread: {}", Thread.currentThread());
        return characterRepository.findAllNameAndId();
    }
}
