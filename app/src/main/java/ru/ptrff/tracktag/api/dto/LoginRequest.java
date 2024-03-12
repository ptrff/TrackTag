package ru.ptrff.tracktag.api.dto;

import androidx.annotation.NonNull;

public class LoginRequest {
    private String grantType;
    @NonNull
    private String username;
    @NonNull
    private String password;
    private String scope;
    private String clientId;
    private String clientSecret;

    public LoginRequest(@NonNull String username, @NonNull String password) {
        grantType = "";
        this.username = username;
        this.password = password;
        scope = "";
        clientId = "";
        clientSecret = "";
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
