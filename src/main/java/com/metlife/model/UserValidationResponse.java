package com.metlife.model;

public class UserValidationResponse {
    private boolean valid;
    private String message;
    private User user;

    public UserValidationResponse() {}

    public UserValidationResponse(boolean valid, String message, User user) {
        this.valid = valid;
        this.message = message;
        this.user = user;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
