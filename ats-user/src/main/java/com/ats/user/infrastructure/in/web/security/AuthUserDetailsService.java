package com.ats.user.infrastructure.in.web.security;


import com.ats.user.infrastructure.out.entity.RolePermissionEntity;
import com.ats.user.infrastructure.out.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthUserDetailsService implements UserDetailsService {

    public final UserJpaRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + email));

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .filter(role -> Boolean.TRUE.equals(role.getActive()))
                .flatMap(role -> {
                    String normalizedRole = role.getName().toUpperCase().replace("ROLE_", "");
                    GrantedAuthority roleAuthority = new SimpleGrantedAuthority("ROLE_" + normalizedRole);

                    Stream<GrantedAuthority> permissionAuthorities = role.getRolePermissions().stream()
                            .filter(rolePermission -> Boolean.TRUE.equals(rolePermission.getActive()))
                            .map(RolePermissionEntity::getPermission)
                            .filter(permission -> permission != null && Boolean.TRUE.equals(permission.getActive()))
                            .map(permission -> new SimpleGrantedAuthority(permission.getCode()));

                    return Stream.concat(Stream.of(roleAuthority), permissionAuthorities);
                })
                .distinct()
                .toList();

        if (authorities.isEmpty()) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .disabled(Boolean.FALSE.equals(user.getActive()))
                .authorities(authorities)
                .build();
    }
}
