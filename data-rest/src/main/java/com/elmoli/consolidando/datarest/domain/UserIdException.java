/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.domain;

/**
 *
 * @author joanr
 */
public class UserIdException extends RuntimeException
{

    public UserIdException()
    {
        super("The user email does not correspond to the path id.");
    }
}
