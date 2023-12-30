/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.datarest.storage;

/**
 *
 * @author joanr
 */
public class StorageReadingException extends RuntimeException
{

   public StorageReadingException(String fileName, Throwable cause) 
   {
        super("Error reading file " + fileName + " from Google Cloud Storage.", cause);
   }
}
