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
public class StorageRenamingException extends RuntimeException
{

    public StorageRenamingException(String fileName, Throwable cause)
    {
        super("Error renaming file " + fileName + " in the Google Cloud Storage.", cause);
    }
}
