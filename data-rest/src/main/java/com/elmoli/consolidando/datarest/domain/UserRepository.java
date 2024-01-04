
/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */


package com.elmoli.consolidando.datarest.domain;

import com.elmoli.consolidando.datarest.config.CacheControl;
import static com.elmoli.consolidando.datarest.config.CachingConfig.CACHE_USER_PUBLIC_INFO;
import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 *
 * @author joanr
 */

@Tag(name="User Info in the Google Datastore")
@RepositoryRestResource(excerptProjection = UserProjection.class,
        collectionResourceDescription = @Description("Public Info of users of Consolidando"))
public interface UserRepository extends DatastoreRepository<User, String>
{

    @Operation(
            summary = "I do not know how to set up Swagger annotations for this method!!!"
    )
    @CacheControl(maxAge = 30, timeUnit = TimeUnit.DAYS)
    @Cacheable(CACHE_USER_PUBLIC_INFO)
    @Override
    public Page<User> findAll(Pageable pageable);

}
