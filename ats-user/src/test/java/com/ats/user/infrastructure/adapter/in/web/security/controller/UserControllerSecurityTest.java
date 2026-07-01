package com.ats.user.infrastructure.adapter.in.web.security.controller;

import com.ats.user.AtsUserApplication;
import com.ats.user.domain.model.Page;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.in.UserUseCase;
import com.ats.user.infrastructure.adapter.out.persistence.repository.MenuJpaRepository;
import com.ats.user.infrastructure.adapter.out.persistence.repository.ModuleJpaRepository;
import com.ats.user.infrastructure.adapter.out.persistence.repository.PermissionJpaRepository;
import com.ats.user.infrastructure.adapter.out.persistence.repository.RoleJpaRepository;
import com.ats.user.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @MockitoBean
    private ModuleJpaRepository moduleJpaRepository;

    @MockitoBean
    private MenuJpaRepository menuJpaRepository;

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
    @WithMockUser(username = "admin@ats.local", authorities = {"ROLE_ADMIN", "USER_CREATE"})
    void saveUserShouldReturn201WhenUserHasCreatePermission() throws Exception {
        when(userUseCase.create(any(), anyLong(), anyString()))
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
    @WithMockUser(username = "recruiter@ats.local", authorities = {"ROLE_ADMIN", "USER_READ"})
    void listUsersShouldReturn200WhenUserHasReadPermission() throws Exception {
        var userPage = new Page<>(List.of(UserDumpData.domainUserExisting()), 0, 20, 1);
        when(userUseCase.listUsers(null, null, new com.ats.user.domain.model.PageQuery(0, 20)))
                .thenReturn(userPage);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].active").value(true))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"ROLE_ADMIN", "USER_READ"})
    void listUsersShouldReturn200WhenFilteringActiveUsers() throws Exception {
        var userPage = new Page<>(List.of(UserDumpData.domainUserExisting()), 0, 20, 1);
        when(userUseCase.listUsers(null, true, new com.ats.user.domain.model.PageQuery(0, 20)))
                .thenReturn(userPage);

        mockMvc.perform(get("/api/users").param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].active").value(true));
    }

    @Test
    void listAvailableRolesShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/users/roles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"USER_CREATE"})
    void listAvailableRolesShouldReturn403WhenUserDoesNotHaveReadOrUpdatePermission() throws Exception {
        mockMvc.perform(get("/api/users/roles"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"ROLE_ADMIN", "USER_READ"})
    void listAvailableRolesShouldReturn200WhenUserHasReadPermission() throws Exception {
        var admin = Role.builder().id(1L).name("ADMIN").description("Administrator role").active(true).build();
        var recruiter = Role.builder().id(2L).name("RECRUITER").description("Recruiter role").active(true).build();
        when(userUseCase.listAvailableRoles()).thenReturn(List.of(admin, recruiter));

        mockMvc.perform(get("/api/users/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("ADMIN"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("RECRUITER"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", authorities = {"ROLE_ADMIN", "USER_UPDATE"})
    void listAvailableRolesShouldReturn200WhenUserHasUpdatePermission() throws Exception {
        var admin = Role.builder().id(1L).name("ADMIN").description("Administrator role").active(true).build();
        when(userUseCase.listAvailableRoles()).thenReturn(List.of(admin));

        mockMvc.perform(get("/api/users/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("ADMIN"));
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
    @WithMockUser(username = "recruiter@ats.local", authorities = {"ROLE_ADMIN", "USER_READ"})
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
    @WithMockUser(username = "recruiter@ats.local", authorities = {"ROLE_ADMIN", "USER_UPDATE"})
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
    @WithMockUser(username = "admin@ats.local", authorities = {"ROLE_ADMIN", "USER_DELETE"})
    void deleteUserShouldReturn204WhenUserHasDeletePermission() throws Exception {
        when(userUseCase.delete(1L)).thenReturn(UserDumpData.domainUserExisting());

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
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
}
