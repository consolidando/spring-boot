/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
public interface CharacterRepository extends CrudRepository<Character, Long>
{
    @Query("SELECT c.name AS name, c.status AS status FROM CHARACTER c")
    List<CharacterDto> findAllNameAndStatus();

}

