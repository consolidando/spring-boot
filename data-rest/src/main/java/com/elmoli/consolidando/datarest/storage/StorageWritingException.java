/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.storage;

/**
 *
 * @author joanr
 */
public class StorageWritingException extends RuntimeException
{
       public StorageWritingException(String fileName, Throwable cause) 
   {
        super("Error writing " + fileName  + " in the Google Cloud Storage." + fileName, cause);
   }
}
