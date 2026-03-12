package com.ats.user.infrastructure.in.web.security.controller;

import com.ats.user.AtsUserApplication;
import com.ats.user.domain.port.in.UserUseCase;
import com.ats.user.infrastructure.out.repository.PermissionJpaRepository;
import com.ats.user.infrastructure.out.repository.RoleJpaRepository;
import com.ats.user.infrastructure.out.repository.UserJpaRepository;
import com.ats.user.support.UserDumpData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class UserControllerSecurityTest {

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

    @Test
    void saveUserShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateUserRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_READ"})
    void saveUserShouldReturn403WhenUserDoesNotHaveCreatePermission() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateUserRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", authorities = {"USER_CREATE"})
    void saveUserShouldReturn201WhenUserHasCreatePermission() throws Exception {
        when(userUseCase.create(any(), anyString(), anyString()))
                .thenReturn(UserDumpData.domainUserExisting());

        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateUserRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("admin@ats.local"));
    }

    @Test
    void listUsersShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_CREATE"})
    void listUsersShouldReturn403WhenUserDoesNotHaveReadPermission() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_READ"})
    void listUsersShouldReturn200WhenUserHasReadPermission() throws Exception {
        when(userUseCase.listActive()).thenReturn(List.of(UserDumpData.domainUserExisting()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void listAvailableRolesShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/users/roles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_UPDATE"})
    void listAvailableRolesShouldReturn403WhenUserDoesNotHaveReadPermission() throws Exception {
        mockMvc.perform(get("/api/users/roles"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_READ"})
    void listAvailableRolesShouldReturn200WhenUserHasReadPermission() throws Exception {
        when(userUseCase.listAvailableRoles()).thenReturn(List.of("ADMIN", "RECRUITER"));

        mockMvc.perform(get("/api/users/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("ADMIN"))
                .andExpect(jsonPath("$[1]").value("RECRUITER"));
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
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_READ"})
    void getUserByIdShouldReturn200WhenUserHasReadPermission() throws Exception {
        when(userUseCase.getById(1L)).thenReturn(UserDumpData.domainUserExisting());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@ats.local"));
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

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_UPDATE"})
    void updateUserShouldReturn200WhenUserHasUpdatePermission() throws Exception {
        when(userUseCase.update(anyLong(), any()))
                .thenReturn(UserDumpData.domainUserExisting());

        mockMvc.perform(put("/api/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateUserRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@ats.local"));
    }

    @Test
    void deleteUserShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_READ"})
    void deleteUserShouldReturn403WhenUserDoesNotHaveDeletePermission() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", authorities = {"USER_DELETE"})
    void deleteUserShouldReturn204WhenUserHasDeletePermission() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
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
                  "role": "RECRUITER"
                }
                """;
    }

    private String validUpdateUserRequestJson() {
        return """
                {
                  "name": "Pablo Updated",
                  "lastName": "Gallegos Updated",
                  "countryCode": "+51",
                  "phone": "987123123",
                  "active": true
                }
                """;
    }
}
