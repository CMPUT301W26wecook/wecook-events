package com.example.wecookproject.model;

public class User {
    private String androidId;
    private String firstName;
    private String lastName;
    private String birthday;
    private String addressLine1;
    private String city;
    private String postalCode;
    private String country;
    private boolean autoLogin;
    private boolean notificationsEnabled;
    private boolean profileCompleted;

    // Required empty constructor for Firestore
    public User() {
    }

    public User(String androidId, String firstName, String lastName, String birthday,
                String addressLine1, String city, String postalCode, String country,
                boolean autoLogin, boolean notificationsEnabled, boolean profileCompleted) {
        this.androidId = androidId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.autoLogin = autoLogin;
        this.notificationsEnabled = notificationsEnabled;
        this.profileCompleted = profileCompleted;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    // Utility methods for display
    public String getFullName() {
        String fullName = "";
        if (firstName != null && !firstName.isEmpty()) {
            fullName += firstName;
        }
        if (lastName != null && !lastName.isEmpty()) {
            if (!fullName.isEmpty()) fullName += " ";
            fullName += lastName;
        }
        return fullName.isEmpty() ? "Unknown User" : fullName;
    }

    public String getDisplayLocation() {
        String location = "";
        if (city != null && !city.isEmpty()) {
            location += city;
        }
        if (country != null && !country.isEmpty()) {
            if (!location.isEmpty()) location += ", ";
            location += country;
        }
        return location.isEmpty() ? "Location not set" : location;
    }

    public String getDisplayId() {
        if (androidId == null || androidId.isEmpty()) {
            return "Unknown ID";
        }
        return androidId.length() > 10 ? androidId.substring(0, 10) + "..." : androidId;
    }

    public String getStatus() {
        return profileCompleted ? "Active" : "Incomplete";
    }
}