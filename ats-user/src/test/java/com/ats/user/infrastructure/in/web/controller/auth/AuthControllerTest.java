package com.ats.user.infrastructure.in.web.controller.auth;

import com.ats.user.AtsUserApplication;
import com.ats.user.infrastructure.out.entity.PermissionEntity;
import com.ats.user.infrastructure.out.entity.RoleEntity;
import com.ats.user.infrastructure.out.entity.RolePermissionEntity;
import com.ats.user.infrastructure.out.entity.RolePermissionId;
import com.ats.user.infrastructure.out.entity.UserEntity;
import com.ats.user.infrastructure.out.repository.MenuJpaRepository;
import com.ats.user.infrastructure.out.repository.ModuleJpaRepository;
import com.ats.user.infrastructure.out.repository.PermissionJpaRepository;
import com.ats.user.infrastructure.out.repository.RoleJpaRepository;
import com.ats.user.infrastructure.out.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = AtsUserApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                        + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
                        + "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
        }
)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserJpaRepository userJpaRepository;

    @MockitoBean
    private RoleJpaRepository roleJpaRepository;

    @MockitoBean
    private PermissionJpaRepository permissionJpaRepository;

    @MockitoBean
    private ModuleJpaRepository moduleJpaRepository;

    @MockitoBean
    private MenuJpaRepository menuJpaRepository;

    @Test
    void loginShouldReturn200AndJwtWhenCredentialsAreValid() throws Exception {
        var permission = PermissionEntity.builder()
                .id(1L)
                .code("USER_CREATE")
                .active(true)
                .build();

        var role = RoleEntity.builder()
                .id(1L)
                .name("ADMIN")
                .active(true)
                .build();

        var rolePermission = RolePermissionEntity.builder()
                .id(new RolePermissionId(1L, 1L))
                .role(role)
                .permission(permission)
                .active(true)
                .build();

        role.setRolePermissions(Set.of(rolePermission));

        var userEntity = UserEntity.builder()
                .id(1L)
                .name("Admin")
                .lastName("User")
                .email("admin@ats.local")
                .phone("123456789")
                .passwordHash("$2a$10$hash")
                .active(true)
                .roles(Set.of(role))
                .build();

        var userDetails = User.builder()
                .username("admin@ats.local")
                .password("$2a$10$hash")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("USER_CREATE")))
                .build();

        when(userJpaRepository.findByEmail("admin@ats.local")).thenReturn(Optional.of(userEntity));
        when(userDetailsService.loadUserByUsername("admin@ats.local")).thenReturn(userDetails);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiredInSeconds").isNumber())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.name").value("Admin"))
                .andExpect(jsonPath("$.user.lastName").value("User"))
                .andExpect(jsonPath("$.user.email").value("admin@ats.local"))
                .andExpect(jsonPath("$.user.phone").value("123456789"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.permissions[0]").value("USER_CREATE"));
    }

    @Test
    void loginShouldReturn401WhenCredentialsAreInvalid() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void loginShouldReturn403WhenUserIsInactive() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("User is disabled"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("USER_INACTIVE"));
    }

    @Test
    void loginShouldReturn403WhenUserHasNoActiveRoles() throws Exception {
        var role = RoleEntity.builder()
                .id(1L)
                .name("INACTIVE_ROLE")
                .active(false)
                .build();

        var userEntity = UserEntity.builder()
                .id(1L)
                .name("Admin")
                .lastName("User")
                .email("admin@ats.local")
                .phone("123456789")
                .passwordHash("$2a$10$hash")
                .active(true)
                .roles(Set.of(role))
                .build();

        when(userJpaRepository.findByEmail("admin@ats.local")).thenReturn(Optional.of(userEntity));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginShouldReturn400WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "not-an-email",
                                  "password": "Clave12345"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void loginShouldReturn400WhenPasswordIsBlank() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@ats.local",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    private String validLoginRequestJson() {
        return """
                {
                  "email": "admin@ats.local",
                  "password": "Clave12345"
                }
                """;
    }
}
