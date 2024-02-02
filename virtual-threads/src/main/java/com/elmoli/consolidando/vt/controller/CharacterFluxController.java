/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.controller;

import com.elmoli.consolidando.vt.repository.Character;
import com.elmoli.consolidando.vt.repository.CharacterFluxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@Profile({"flux"})
@RestController
@RequestMapping("apis/characters")
public class CharacterFluxController {
    private static final Logger logger = LoggerFactory.getLogger(CharacterController.class);
    private final CharacterFluxRepository characterRepository;

    public CharacterFluxController(CharacterFluxRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @GetMapping
    public Flux<Character> getAllCharacters() {
        logger.info("-- Running GET in thread: {}", Thread.currentThread());
        return characterRepository.findAll();
    }
}

