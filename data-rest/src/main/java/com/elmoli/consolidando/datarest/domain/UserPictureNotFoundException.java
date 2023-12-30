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
public class UserPictureNotFoundException extends RuntimeException
{    
    public UserPictureNotFoundException(String pictureName)
    {
        super("User picture " + pictureName  + " not found");
    }
}
