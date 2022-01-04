package com.anhdungpham.awsjavaspring.services;

import com.anhdungpham.awsjavaspring.buckets.BucketMain;
import com.anhdungpham.awsjavaspring.data.FileData;
import com.anhdungpham.awsjavaspring.entities.ImageName;
import com.anhdungpham.awsjavaspring.entities.UserProfile;
import com.anhdungpham.awsjavaspring.repositories.UserProfileRepository;
import com.anhdungpham.awsjavaspring.services.impl.IUserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
@RequiredArgsConstructor
public class UserProfileService implements IUserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final FileData fileData;
    private final ImageName imageName;

    public List<UserProfile> getUserProfiles() {
        return userProfileRepository.getUserProfiles();
    }

    public void uploadS3ProfileImage(UUID userProfileId, MultipartFile file) {
        // check image not empty
        isNotEmptyFile(file);

        // check file is an image
        fileIsImage(file);

        // check userprofile is exist on entity
        UserProfile userProfile = userProfileCheck(userProfileId);

        // metadata from file
        Map<String, String> metaData = metaDataMapper(file);

        // store to s3 and save database image URL
        s3Save(file, userProfile, metaData);
    }

    public byte[] downloadS3ProfileImage(UUID userProfileId) {
        UserProfile userProfile = userProfileCheck(userProfileId);
        String s3Path = String.format("%s/%s",
                BucketMain.PROFILE_IMAGE.getBucketName(),
                userProfile.getUserProfileId()
        );

        return userProfile.getImagePath().map(fileName -> fileData.downloadFile(s3Path, fileName))
                .orElse(new byte[0]);
    }

    public void deleteS3ProfileImage(UUID userProfileId) {
        UserProfile userProfile = userProfileCheck(userProfileId);
        String s3Path = String.format("%s/%s",
                BucketMain.PROFILE_IMAGE.getBucketName(),
                userProfile.getUserProfileId()
        );
        userProfile.getImagePath()
                .map(fileName -> fileData.deleteFile(s3Path, fileName))
                .orElseThrow(() -> new IllegalStateException("CANNOT DELETE FILE !!!"));
    }

    @Override
    public void isNotEmptyFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException(String.format("FILE IS EMPTY WITH: [ %s ]", file.getSize()));
        }
    }

    @Override
    public void fileIsImage(MultipartFile file) {
        if (!List.of(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType(), IMAGE_SVG.getMimeType())
                .contains(file.getContentType())) {
            throw new IllegalStateException(String.format("FILE IS NOT AN IMAGE: [ %s ]", file.getContentType()));
        }
    }

    @Override
    public UserProfile userProfileCheck(UUID userProfileId) {
        return userProfileRepository.getUserProfiles().stream()
                .filter(user -> user.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("USER PROFILE DOES NOT EXIST: [ %s ]", userProfileId))
                );
    }

    @Override
    public Map<String, String> metaDataMapper(MultipartFile file) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("Content-Type", file.getContentType());
        metaData.put("Content-Length", String.valueOf(file.getSize()));
        metaData.put("Content-Disposition", file.getOriginalFilename());
        return metaData;
    }

    @Override
    public void s3Save(MultipartFile file, UserProfile userProfile, Map<String, String> metaData) {
        String s3Path = String.format("%s/%s", BucketMain.PROFILE_IMAGE.getBucketName(), userProfile.getUserProfileId());
        String fileName = String.format("%s_%s", BucketMain.PROFILE_IMAGE.getBucketName(), file.getOriginalFilename());

        try {
            fileData.saveFile(s3Path, fileName, file.getInputStream(), Optional.of(metaData));

            // save image path to Database
            userProfile.setImagePath(fileName);
            imageName.setImageName(fileName);
        } catch (IOException e) {
            throw new IllegalStateException("CANNOT SAVE DATA TO S3 BUCKET", e);
        }
    }


}
