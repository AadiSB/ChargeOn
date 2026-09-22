package com.core2web.model;

public class AuthSession {

    private static AuthSession current;

    private final String uid;
    private final String idToken;
    private final String refreshToken;
    private final String email;
    private final String role;

    public AuthSession(String uid, String idToken, String refreshToken, String email, String role) {
        this.uid = uid;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.role = role;
    }

    public static AuthSession getCurrent() {
        return current;
    }

    public static void setCurrent(AuthSession session) {
        current = session;
    }

    public static void clear() {
        current = null;
    }

    public String getUid() { return uid; }
    public String getIdToken() { return idToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
