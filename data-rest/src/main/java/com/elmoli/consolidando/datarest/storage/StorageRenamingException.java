/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
