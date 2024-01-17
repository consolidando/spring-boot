package com.example.demo.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents character information retrieved from the Rick and Morty API,
 * specifically designed to be saved in the repository using JPA.
 */

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CharacterInfo
{
    @Id
    private int id;
    private String name;
    private String status;
    private String species;
    private String type;
    private String gender;

    @AttributeOverrides(
    {
        @AttributeOverride(name = "name", column = @Column(name = "origin_name")),
        @AttributeOverride(name = "url", column = @Column(name = "origin_url"))
    })
    @Embedded
    private OriginInfo origin;

    @AttributeOverrides(
    {
        @AttributeOverride(name = "name", column = @Column(name = "location_name")),
        @AttributeOverride(name = "url", column = @Column(name = "location_url"))
    })
    @Embedded
    private LocationInfo location;

    private String image;
    private List<String> episode;
    private String url;
    private String created;
}

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
class OriginInfo
{

    private String name;
    private String url;
}

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
class LocationInfo
{

    private String name;
    private String url;
}
