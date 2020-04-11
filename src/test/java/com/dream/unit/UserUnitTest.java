package com.dream.unit;

import com.dream.models.Authority;
import com.dream.models.User;
import com.dream.utils.Role;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class UserUnitTest {

    private static final User first = User.builder()
            .id(1)
            .email("test@test.com")
            .username("first")
            .password("pass")
            .enabled(true)
            .authorities(Arrays.asList(Authority.builder()
                    .id(1)
                    .role(Role.ROLE_ADMIN)
                    .build()))
            .build();
    private static final User second = User.builder()
            .id(1)
            .email("test@test.com")
            .username("first")
            .password("pass")
            .enabled(true)
            .authorities(Arrays.asList(Authority.builder()
                    .id(1)
                    .role(Role.ROLE_ADMIN)
                    .build()))
            .build();
    private static final User third = User.builder()
            .id(1)
            .email("test@test.test")
            .username("third")
            .password("pass")
            .enabled(true)
            .authorities(Arrays.asList(Authority.builder()
                    .id(1)
                    .role(Role.ROLE_ADMIN)
                    .build()))
            .build();

    @Test
    public void userEqualTest() {
        Assert.assertTrue("First user should be equal to second one", first.equals(second));
        Assert.assertFalse("First user should not be equal to third one", first.equals(third));
        third.setId(null);
        Assert.assertFalse("First user should not be equal to third one", first.equals(third));
        third.setId(3);
        Assert.assertFalse("First user should not be equal to third one", first.equals(third));
        Assert.assertFalse("First user should not be equal to null object", first.equals(null));
    }
}
