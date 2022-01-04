package com.anhdungpham.awsjavaspring.controllers;

import com.anhdungpham.awsjavaspring.entities.ImageName;
import com.anhdungpham.awsjavaspring.entities.UserProfile;
import com.anhdungpham.awsjavaspring.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(value = "*", maxAge = 3600L)
@RequiredArgsConstructor
@RequestMapping(path = "api/s3/profile")
public class S3Controller {
    private final UserProfileService userProfileService;
    private final ImageName imageName;

    @GetMapping
    public List<UserProfile> getUserProfiles() {
        return userProfileService.getUserProfiles();
    }

    @PostMapping(
            path = "{userProfileId}/image/post",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void uploadS3ProfileImage(@PathVariable("userProfileId") UUID userProfileId,
                                     @RequestParam("file") MultipartFile file) {
        userProfileService.uploadS3ProfileImage(userProfileId, file);
    }


    @GetMapping(path = "{userProfileId}/image/download")
    public ResponseEntity<ByteArrayResource> downloadS3ProfileImage(@PathVariable("userProfileId") UUID userProfileId) {
        byte[] downloadProfileData = userProfileService.downloadS3ProfileImage(userProfileId);
        ByteArrayResource byteArrayResource = new ByteArrayResource(downloadProfileData);

        String downloadedImageProfileName = imageName.getImageName();

        return ResponseEntity.ok().contentLength(downloadProfileData.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-description", "attachment; " +
                        "filename=\"" + downloadedImageProfileName + "\"")
                .body(byteArrayResource);
    }

    @DeleteMapping("{userProfileId}/image/delete")
    @ResponseStatus(HttpStatus.RESET_CONTENT)
    public String deleteS3ProfileImage(@PathVariable("userProfileId") UUID userProfileId) {
        userProfileService.deleteS3ProfileImage(userProfileId);
        String deletedFileName = imageName.getImageName();
        return "DELETED " + deletedFileName;
    }
}
