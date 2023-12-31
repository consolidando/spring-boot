/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.domain;

import org.springframework.data.rest.core.config.Projection;

/**
 *
 * @author joanr
 */
@Projection(name = "userProjection", types = { User.class })
public interface UserProjection
{
    String getId();
    String getName();
    String getDescription();
    String getPicture();
    String getWeb();
}
