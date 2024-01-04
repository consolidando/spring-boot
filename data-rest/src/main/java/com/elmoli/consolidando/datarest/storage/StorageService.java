/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.datarest.storage;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.CopyWriter;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author joanr
 */
@Service
@RequiredArgsConstructor
public class StorageService
{

    @Autowired
    private Storage storage;

    @Value("${data-rest.storage.bucketName}")
    public String bucketName;

    public BlobId getBlobId(String fileName)
    {
        return (BlobId.of(bucketName, fileName));
    }

    public InputStream read(String fileName) throws StorageReadingException
    {
        try (ReadChannel reader = storage.reader(getBlobId(fileName)))
        {
            return Channels.newInputStream(reader);
        } catch (StorageException e)
        {
            throw new StorageReadingException(fileName, e);
        }
    }

    public String save(String fileName, InputStream file)
    {
        String mediaLink = null;
        try
        {
            BlobInfo blobInfo = BlobInfo.newBuilder(getBlobId(fileName))
                    //.setAcl(publicAccess)
                    //                .setContentType("image/jpg")
                    //.setCacheControl("max-age=604800")
                    .build();

            Blob blob = storage.createFrom(blobInfo, file);

            mediaLink = blob.getMediaLink();

        } catch (IOException e)
        {
            throw new StorageWritingException(fileName, e);
        }

        // includes generation query parameter that changes each update
        return (mediaLink);
    }

    public String rename(String sourceFileName, String targetFileName, String cacheControl)
    {
        String mediaLink = null;
        try
        {
            BlobId source = getBlobId(sourceFileName);
            BlobId target = getBlobId(targetFileName);
            
            BlobInfo targetBlobInfo = BlobInfo.newBuilder(target)                    
                    .setCacheControl(cacheControl)  
                    .build();
            
            CopyWriter copyWriter
                    = storage.copy(
                            Storage.CopyRequest.newBuilder()
                                    .setSource(source)
                                    .setTarget(targetBlobInfo,                                             
                                            Storage.BlobTargetOption
                                                    .predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ)
                                    ).build());

            storage.get(source).delete();

            mediaLink = copyWriter.getResult().getMediaLink();

        } catch (StorageException e)
        {
            throw new StorageRenamingException(sourceFileName, e);
        }

        return (mediaLink);
    }

    public String getMediaLink(String fileName)
    {
        String mediaLink = null;

        BlobId blobId = getBlobId(fileName);
        Blob blob = storage.get(blobId);

        if (blob != null)
        {
            mediaLink = blob.getMediaLink();
        }

        return (mediaLink);
    }

    public boolean exists(String FileName)
    {
        return (storage.get(getBlobId(FileName)) != null);
    }

    public void delete(String FileName)
    {
        Blob blob = storage.get(getBlobId(FileName));
        if (blob != null)
        {
            blob.delete();
        }
    }

}
