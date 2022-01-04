package com.anhdungpham.awsjavaspring.buckets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BucketMain {
    PROFILE_IMAGE("stanleypham-aws-springboot");

    private final String bucketName;
}
