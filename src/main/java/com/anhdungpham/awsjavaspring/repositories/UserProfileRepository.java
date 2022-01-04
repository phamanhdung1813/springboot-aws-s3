package com.anhdungpham.awsjavaspring.repositories;

import com.anhdungpham.awsjavaspring.entities.FakeUserProfileEntity;
import com.anhdungpham.awsjavaspring.entities.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserProfileRepository {
    private final FakeUserProfileEntity fakeUserProfileEntity;

    public List<UserProfile> getUserProfiles() {
        return fakeUserProfileEntity.getUserProfile();
    }
}
