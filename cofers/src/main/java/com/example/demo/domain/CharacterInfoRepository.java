package com.example.demo.domain;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;


/**
 * Repository interface for managing {@link CharacterInfo} entities exposed as a Spring Data REST resource.
 * This repository provides customizations to control the exposure of CRUD operations for the "characters" collection.
 * Additionally, specific operations like save, delete, and findById for individual items are customized for hiding from the REST API.
 *
 * @RepositoryRestResource annotation is used to customize the resource path, collection resource relation, and specify an excerpt projection.
 * @RestResource(exported = false) is used on specific CRUD methods to disable their exposure in the API.
 */

@Tag(name="GET Character Info Projection Collection")
@RepositoryRestResource(
        collectionResourceRel = "characters",
        path = "characters",
        excerptProjection = CharacterInfoProjection.class)
public interface CharacterInfoRepository extends
        PagingAndSortingRepository<CharacterInfo, Long>,
        CrudRepository<CharacterInfo, Long>
{
    @Override
    @RestResource(exported = false)
    void deleteById(Long aLong);

    @Override
    @RestResource(exported = false)
    <S extends CharacterInfo> S save(S entity);

    @Override
    @RestResource(exported = false)
    <S extends CharacterInfo> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @RestResource(exported = false)
    void delete(CharacterInfo entity);

    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends CharacterInfo> entities);

    @Override
    @RestResource(exported = false)
    void deleteAll();
    
    @Override
    @RestResource(exported = false)
    Optional<CharacterInfo> findById(Long aLong);

}
