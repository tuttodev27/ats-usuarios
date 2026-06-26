package com.ats.user.domain.port.in;

import com.ats.user.domain.model.AuthResult;

public interface AuthUseCase {
    AuthResult login(String email);
}
