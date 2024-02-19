/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.vt.repository;

import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author joanr
 */
@Profile({"servlet"})
public interface CharacterRepository extends CrudRepository<Character, Integer>
{
    @Query("SELECT c.name AS name, c.id AS id FROM CHARACTER c")
    List<CharacterDto> findAllNameAndId();

}

