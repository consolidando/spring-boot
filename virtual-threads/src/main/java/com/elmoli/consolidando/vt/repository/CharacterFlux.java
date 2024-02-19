/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.vt.repository;

/**
 *
 * @author joanr
 */
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

@Table("characterflux")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterFlux implements Persistable<Integer>
{

    @Id
    private Integer id;

    @Version
    private Integer version;

    private String name;

    private String status;

    private String species;

    private String type;

    @Transient
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL, prefix = "origin_")
    private OriginInfo origin;

    @Transient
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL, prefix = "location_")
    private LocationInfo location;

    private String image;

    @Column("episode")
    private List<String> episode;

    private String url;

    private Instant created;

    @Override
    public Integer getId()
    {
        return (id);
    }

    @Override
    public boolean isNew()
    {
        return (true);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OriginInfo
    {

        private String name;
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfo
    {

        private String name;
        private String url;
    }
}
