package com.example.demo.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
        collectionResourceRel = "characters",
        path = "characters",
        excerptProjection = CharacterInfoProjection.class)
public interface CharacterInfoRepository extends
        PagingAndSortingRepository<CharacterInfo, Long>,
        CrudRepository<CharacterInfo, Long>
{

}
