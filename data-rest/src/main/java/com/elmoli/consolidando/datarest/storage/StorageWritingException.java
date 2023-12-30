/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
