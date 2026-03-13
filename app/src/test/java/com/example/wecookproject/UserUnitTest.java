package com.example.wecookproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.example.wecookproject.model.User;

import org.junit.Test;

public class UserUnitTest {

    @Test
    public void testClearProfile() {
        User user = new User(
                "123 Main St", "Apt 4", "device123", "1990-01-01",
                "Edmonton", "Canada", "John", "Doe", "T6G 2R3",
                true, "entrant"
        );

        user.clearProfile();

        assertEquals("Deleted", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("", user.getBirthday());
        assertEquals("", user.getAddressLine1());
        assertEquals("", user.getAddressLine2());
        assertEquals("", user.getCity());
        assertEquals("", user.getPostalCode());
        assertEquals("", user.getCountry());
        assertFalse(user.isProfileCompleted());

        assertEquals("entrant", user.getRole());
        assertEquals("device123", user.getAndroidId());
    }

}
