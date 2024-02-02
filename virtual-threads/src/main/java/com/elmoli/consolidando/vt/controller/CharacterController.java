/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.vt.controller;

import com.elmoli.consolidando.vt.repository.CharacterDto;
import com.elmoli.consolidando.vt.repository.CharacterRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author joanr
 */
@Profile({"servlet"})
@RestController
@RequestMapping("apis/characters")
public class CharacterController
{
    private static final Logger logger = LoggerFactory.getLogger(CharacterController.class);
    private final CharacterRepository characterRepository;

    public CharacterController(CharacterRepository characterRepository)
    {
        this.characterRepository = characterRepository;
    }

    @GetMapping
    public List<CharacterDto> getAllCharacters()
    {
        logger.info("-- Running GET in thread: {}", Thread.currentThread());   
        return characterRepository.findAllNameAndStatus();
    }

}

