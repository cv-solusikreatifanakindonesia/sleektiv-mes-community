package com.sleektiv.security.api;

import com.sleektiv.model.api.Entity;

public interface PasswordResetTokenService {

    Entity createPasswordResetTokenForUser(Entity userEntity);

    Entity processPasswordChange(String token, String password, String passwordConfirmation);

}
