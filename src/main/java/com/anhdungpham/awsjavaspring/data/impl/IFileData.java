package com.anhdungpham.awsjavaspring.data.impl;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public interface IFileData {
    void saveFile(String path, String fileName, InputStream inputStream, Optional<Map<String, String>> optionalMetaData);

    byte[] downloadFile(String s3Path, String fileName);

    String deleteFile(String s3Path, String fileName);
}
