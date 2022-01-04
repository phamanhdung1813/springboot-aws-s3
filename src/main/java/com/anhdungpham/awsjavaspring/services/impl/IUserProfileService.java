package com.anhdungpham.awsjavaspring.services.impl;

import com.anhdungpham.awsjavaspring.entities.UserProfile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

public interface IUserProfileService {
    void isNotEmptyFile(MultipartFile file);

    void fileIsImage(MultipartFile file);

    UserProfile userProfileCheck(UUID userProfileId);

    Map<String, String> metaDataMapper(MultipartFile file);

    void s3Save(MultipartFile file, UserProfile userProfile, Map<String, String> metaData);
}
