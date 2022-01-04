package com.anhdungpham.awsjavaspring.data;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.anhdungpham.awsjavaspring.data.impl.IFileData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileData implements IFileData {
    private final AmazonS3 amazonS3;

    @Override
    public void saveFile(String path, String fileName,
                         InputStream inputStream,
                         Optional<Map<String, String>> optionalMetaData) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetaData.ifPresent(obj -> {
            if (!obj.isEmpty()) {
                obj.forEach((key, value) -> objectMetadata.addUserMetadata(key, value));
            }
        });

        try {
            amazonS3.putObject(path, fileName, inputStream, objectMetadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("STORE FILE FAILED !!!!", e);
        }
    }

    @Override
    public byte[] downloadFile(String s3Path, String fileName) {
        try {
            S3Object s3Object = amazonS3.getObject(s3Path, fileName);
            S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(s3ObjectInputStream);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("DOWNLOAD FILE FAILED !!!!", e);
        }
    }

    @Override
    public String deleteFile(String s3Path, String fileName) {
        amazonS3.deleteObject(s3Path, fileName);
        return fileName + "HAS BEEN DELETED";
    }
}
