package com.core2web.model;

public class Admin {


private String uid;
private String name;
private String email;
private String phone;
private String status;
private String createdAt;
private String profileImageUrl;
private String role;

public Admin() {
}

public Admin(String uid, String name, String email, String phone,
             String status, String createdAt, String profileImageUrl,
             String role) {

    this.uid = uid;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.status = status;
    this.createdAt = createdAt;
    this.profileImageUrl = profileImageUrl;
    this.role = role;
}

public String getUid() {
    return uid;
}

public void setUid(String uid) {
    this.uid = uid;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getEmail() {
    return email;
}

public void setEmail(String email) {
    this.email = email;
}

public String getPhone() {
    return phone;
}

public void setPhone(String phone) {
    this.phone = phone;
}

public String getStatus() {
    return status;
}

public void setStatus(String status) {
    this.status = status;
}

public String getCreatedAt() {
    return createdAt;
}

public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
}

public String getProfileImageUrl() {
    return profileImageUrl;
}

public void setProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
}

public String getRole() {
    return role;
}

public void setRole(String role) {
    this.role = role;
}


}

