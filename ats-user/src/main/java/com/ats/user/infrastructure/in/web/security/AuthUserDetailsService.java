package com.ats.user.infrastructure.in.web.security;


import com.ats.user.infrastructure.out.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthUserDetailsService implements UserDetailsService {

    public final UserJpaRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user= userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + email));

        List<String> roleNames = user.getRoles().stream()
                .filter(role -> Boolean.TRUE.equals(role.getActive()))
                .map(role -> role.getName().toUpperCase().replace("ROLE_", ""))
                .distinct()
                .toList();

        String[] roles = roleNames.isEmpty()
                ? new String[]{"USER"}
                : roleNames.toArray(new String[0]);

        return User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .disabled(Boolean.FALSE.equals(user.getActive()))
                .roles(roles)
                .build();

    }
}
