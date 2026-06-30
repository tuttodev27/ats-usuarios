package com.ats.user.infrastructure.adapter.in.web.controller;

import com.ats.user.AtsUserApplication;
import com.ats.user.domain.exception.EmailAlreadyExistException;
import com.ats.user.domain.exception.RoleNotAvailableException;
import com.ats.user.domain.exception.UserNotFoundException;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.model.User;
import com.ats.user.domain.port.in.UserUseCase;
import com.ats.user.infrastructure.adapter.out.persistence.repository.MenuJpaRepository;
import com.ats.user.infrastructure.adapter.out.persistence.repository.ModuleJpaRepository;
import com.ats.user.infrastructure.adapter.out.persistence.repository.PermissionJpaRepository;
import com.ats.user.infrastructure.adapter.out.persistence.repository.RoleJpaRepository;
import com.ats.user.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserUseCase userUseCase;

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
    @WithMockUser(username = "admin@ats.local", authorities = {"ROLE_ADMIN", "USER_CREATE"})
    void createUserShouldReturn201WhenRequestIsValid() throws Exception {
        var role = Role.builder().id(2L).name("RECRUITER").active(true).build();
        var user = User.builder()
                .id(1L)
                .name("Pablo")
                .lastName("Gallegos")
                .email("pgallegoscelis86@gmail.com")
                .countryCode("+56")
                .phone("989421155")
                .active(true)
                .roles(Set.of(role))
                .build();

        when(userUseCase.create(any(), anyLong(), anyString())).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateUserRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pablo"))
                .andExpect(jsonPath("$.lastName").value("Gallegos"))
                .andExpect(jsonPath("$.email").value("pgallegoscelis86@gmail.com"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", authorities = {"ROLE_ADMIN", "USER_CREATE"})
    void createUserShouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(userUseCase.create(any(), anyLong(), anyString()))
                .thenThrow(new EmailAlreadyExistException("Email already registered: existing@test.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateUserRequestJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_EXISTS"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", authorities = {"ROLE_ADMIN", "USER_CREATE"})
    void createUserShouldReturn400WhenRoleIsNotFound() throws Exception {
        when(userUseCase.create(any(), anyLong(), anyString()))
                .thenThrow(new RoleNotAvailableException("Role not found: 99"));

        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateUserRequestJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_AVAILABLE"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", authorities = {"ROLE_ADMIN", "USER_CREATE"})
    void createUserShouldReturn400WhenRoleIsInactive() throws Exception {
        when(userUseCase.create(any(), anyLong(), anyString()))
                .thenThrow(new RoleNotAvailableException("Role is not active: 3"));

        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateUserRequestJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_AVAILABLE"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_READ"})
    void createUserShouldReturn403WhenUserDoesNotHaveCreatePermission() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateUserRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    void createUserShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateUserRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", authorities = {"ROLE_ADMIN", "USER_READ"})
    void getUserByIdShouldReturn200WhenUserExists() throws Exception {
        var role = Role.builder().id(1L).name("ADMIN").active(true).build();
        var user = User.builder()
                .id(1L)
                .name("System")
                .lastName("Admin")
                .email("admin@ats.local")
                .countryCode("+57")
                .phone("3001002000")
                .active(true)
                .roles(Set.of(role))
                .build();

        when(userUseCase.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("System"))
                .andExpect(jsonPath("$.lastName").value("Admin"))
                .andExpect(jsonPath("$.email").value("admin@ats.local"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser(username = "admin@ats.local", authorities = {"ROLE_ADMIN", "USER_READ"})
    void getUserByIdShouldReturn404WhenUserNotFound() throws Exception {
        when(userUseCase.getById(99L)).thenThrow(new UserNotFoundException("User not found: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void getUserByIdShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_UPDATE"})
    void getUserByIdShouldReturn403WhenUserDoesNotHaveReadPermission() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", authorities = {"ROLE_ADMIN", "USER_UPDATE"})
    void updateUserShouldReturn200WhenRequestIsValid() throws Exception {
        var role = Role.builder().id(1L).name("ADMIN").active(true).build();
        var updatedUser = User.builder()
                .id(1L)
                .name("Pablo Updated")
                .lastName("Gallegos Updated")
                .email("admin@ats.local")
                .countryCode("+51")
                .phone("987123123")
                .active(true)
                .roles(Set.of(role))
                .build();

        when(userUseCase.update(anyLong(), any())).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateUserRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pablo Updated"))
                .andExpect(jsonPath("$.lastName").value("Gallegos Updated"))
                .andExpect(jsonPath("$.countryCode").value("+51"))
                .andExpect(jsonPath("$.phone").value("987123123"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", authorities = {"ROLE_ADMIN", "USER_UPDATE"})
    void updateUserShouldReturn404WhenUserNotFound() throws Exception {
        when(userUseCase.update(anyLong(), any()))
                .thenThrow(new UserNotFoundException("User not found: 99"));

        mockMvc.perform(put("/api/users/99")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateUserRequestJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void updateUserShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(put("/api/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateUserRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_READ"})
    void updateUserShouldReturn403WhenUserDoesNotHaveUpdatePermission() throws Exception {
        mockMvc.perform(put("/api/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateUserRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    private String validUpdateUserRequestJson() {
        return """
                {
                  "name": "Pablo Updated",
                  "lastName": "Gallegos Updated",
                  "countryCode": "+51",
                  "phone": "987123123"
                }
                """;
    }

    private String validCreateUserRequestJson() {
        return """
                {
                  "name": "Pablo",
                  "lastName": "Gallegos",
                  "email": "pgallegoscelis86@gmail.com",
                  "countryCode": "+56",
                  "phone": "989421155",
                  "password": "Clave12345",
                  "roleId": 2
                }
                """;
    }
}
