package com.ats.user.domain.port.out;

import java.util.Map;

public interface TokenPort {
    String generateToken(String subject, Map<String, Object> claims);
}
