package com.ats.user.infrastructure.in.web.security.controller;

import com.ats.user.AtsUserApplication;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.in.RoleUseCase;
import com.ats.user.infrastructure.out.repository.PermissionJpaRepository;
import com.ats.user.infrastructure.out.repository.RoleJpaRepository;
import com.ats.user.infrastructure.out.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
public class RoleControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleUseCase roleUseCase;

    @MockitoBean
    private UserJpaRepository userJpaRepository;

    @MockitoBean
    private RoleJpaRepository roleJpaRepository;

    @MockitoBean
    private PermissionJpaRepository permissionJpaRepository;

    @Test
    void createRoleShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/roles")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateRoleRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", roles = {"RECRUITER"})
    void createRoleShouldReturn403WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(post("/api/roles")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateRoleRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", roles = {"ADMIN"})
    void createRoleShouldReturn201WhenRoleIsAdmin() throws Exception {
        when(roleUseCase.create(any(Role.class))).thenReturn(sampleRole());

        mockMvc.perform(post("/api/roles")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateRoleRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SUPERVISOR"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", roles = {"RECRUITER"})
    void updateRoleShouldReturn403WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(put("/api/roles/1")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateRoleRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", roles = {"ADMIN"})
    void updateRoleShouldReturn200WhenRoleIsAdmin() throws Exception {
        when(roleUseCase.update(anyLong(), any(Role.class))).thenReturn(sampleRole());

        mockMvc.perform(put("/api/roles/1")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateRoleRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPERVISOR"));
    }

    @Test
    void deleteRoleShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", roles = {"RECRUITER"})
    void deleteRoleShouldReturn403WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", roles = {"ADMIN"})
    void deleteRoleShouldReturn204WhenRoleIsAdmin() throws Exception {
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", roles = {"RECRUITER"})
    void assignPermissionsShouldReturn403WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(put("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validPermissionRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", roles = {"ADMIN"})
    void assignPermissionsShouldReturn200WhenRoleIsAdmin() throws Exception {
        when(roleUseCase.assignPermissions(anyLong(), anyList())).thenReturn(sampleRole());

        mockMvc.perform(put("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validPermissionRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("SUPERVISOR"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", roles = {"RECRUITER"})
    void deletePermissionsShouldReturn403WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validPermissionRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", roles = {"ADMIN"})
    void deletePermissionsShouldReturn200WhenRoleIsAdmin() throws Exception {
        when(roleUseCase.deletePermissions(anyLong(), anyList())).thenReturn(sampleRole());

        mockMvc.perform(delete("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validPermissionRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("SUPERVISOR"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", roles = {"RECRUITER"})
    void updateStatusShouldReturn403WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(patch("/api/roles/1/status")
                        .contentType(APPLICATION_JSON)
                        .content(validStatusRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", roles = {"ADMIN"})
    void updateStatusShouldReturn200WhenRoleIsAdmin() throws Exception {
        when(roleUseCase.updateStatus(anyLong(), anyBoolean())).thenReturn(sampleRole());

        mockMvc.perform(patch("/api/roles/1/status")
                        .contentType(APPLICATION_JSON)
                        .content(validStatusRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPERVISOR"));
    }

    private Role sampleRole() {
        return Role.builder()
                .id(1L)
                .name("SUPERVISOR")
                .description("Rol supervisor")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .permissions(new HashSet<>())
                .build();
    }

    private String validCreateRoleRequestJson() {
        return """
                {
                  "name": "SUPERVISOR",
                  "description": "Rol supervisor",
                  "active": true
                }
                """;
    }

    private String validUpdateRoleRequestJson() {
        return """
                {
                  "name": "SUPERVISOR",
                  "description": "Rol supervisor actualizado",
                  "active": true
                }
                """;
    }

    private String validPermissionRequestJson() {
        return """
                {
                  "permissionIds": [1, 2]
                }
                """;
    }

    private String validStatusRequestJson() {
        return """
                {
                  "active": false
                }
                """;
    }
}
