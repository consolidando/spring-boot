package com.example.demo.domain;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "characterInfoProjection", types = { CharacterInfo.class })
public interface CharacterInfoProjection
{
    String getName();
    String getStatus();
}
