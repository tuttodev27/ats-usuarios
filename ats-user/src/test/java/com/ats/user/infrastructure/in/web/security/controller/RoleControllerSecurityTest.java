package com.ats.user.infrastructure.in.web.security.controller;

import com.ats.user.AtsUserApplication;
import com.ats.user.domain.exception.PermissionNotFoundException;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.in.RoleUseCase;
import com.ats.user.infrastructure.out.repository.MenuJpaRepository;
import com.ats.user.infrastructure.out.repository.ModuleJpaRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.ats.user.domain.exception.RoleNotFoundException;

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

    @MockitoBean
    private ModuleJpaRepository moduleJpaRepository;

    @MockitoBean
    private MenuJpaRepository menuJpaRepository;

    @Test
    void createRoleShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/roles")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateRoleRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_UPDATE"})
    void createRoleShouldReturn403WhenUserDoesNotHaveCreatePermission() throws Exception {
        mockMvc.perform(post("/api/roles")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateRoleRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_CREATE"})
    void createRoleShouldReturn201WhenUserHasCreatePermission() throws Exception {
        when(roleUseCase.create(any(Role.class))).thenReturn(sampleRole());

        mockMvc.perform(post("/api/roles")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateRoleRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SUPERVISOR"));
    }

    @Test
    void listRolesShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_UPDATE"})
    void listRolesShouldReturn403WhenUserDoesNotHaveReadPermission() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_READ"})
    void listRolesShouldReturn200WhenUserHasReadPermission() throws Exception {
        when(roleUseCase.list()).thenReturn(java.util.List.of(sampleRole()));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("SUPERVISOR"));
    }

    @Test
    void getRoleByIdShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_UPDATE"})
    void getRoleByIdShouldReturn403WhenUserDoesNotHaveReadPermission() throws Exception {
        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_READ"})
    void getRoleByIdShouldReturn200WhenUserHasReadPermission() throws Exception {
        when(roleUseCase.getById(1L)).thenReturn(sampleRole());

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPERVISOR"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_CREATE"})
    void updateRoleShouldReturn403WhenUserDoesNotHaveUpdatePermission() throws Exception {
        mockMvc.perform(put("/api/roles/1")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateRoleRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_UPDATE"})
    void updateRoleShouldReturn200WhenUserHasUpdatePermission() throws Exception {
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
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_UPDATE"})
    void deleteRoleShouldReturn403WhenUserDoesNotHaveDeletePermission() throws Exception {
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_DELETE"})
    void deleteRoleShouldReturn204WhenUserHasDeletePermission() throws Exception {
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void assignPermissionsShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(put("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validPermissionRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_UPDATE"})
    void assignPermissionsShouldReturn403WhenUserDoesNotHaveAssignPermission() throws Exception {
        mockMvc.perform(put("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validPermissionRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_PERMISSION_ASSIGN"})
    void assignPermissionsShouldReturn200WhenUserHasAssignPermission() throws Exception {
        when(roleUseCase.assignPermissions(anyLong(), any())).thenReturn(sampleRole());

        mockMvc.perform(put("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validPermissionRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("SUPERVISOR"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_PERMISSION_ASSIGN"})
    void assignPermissionsShouldReturn400WhenRequestDoesNotIncludePermissions() throws Exception {
        mockMvc.perform(put("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "permissionIds": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_PERMISSION_ASSIGN"})
    void assignPermissionsShouldReturn404WhenRoleDoesNotExist() throws Exception {
        when(roleUseCase.assignPermissions(99L, java.util.List.of(1L, 2L)))
                .thenThrow(new RoleNotFoundException("Role not found: 99"));

        mockMvc.perform(put("/api/roles/99/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validPermissionRequestJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_PERMISSION_ASSIGN"})
    void assignPermissionsShouldReturn404WhenAnyPermissionDoesNotExist() throws Exception {
        when(roleUseCase.assignPermissions(1L, java.util.List.of(1L, 2L)))
                .thenThrow(new PermissionNotFoundException("One or more permissions were not found"));

        mockMvc.perform(put("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validPermissionRequestJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PERMISSION_NOT_FOUND"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_DELETE"})
    void deletePermissionsShouldReturn403WhenUserDoesNotHaveRemovePermission() throws Exception {
        mockMvc.perform(delete("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validPermissionRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_PERMISSION_REMOVE"})
    void deletePermissionsShouldReturn200WhenUserHasRemovePermission() throws Exception {
        when(roleUseCase.deletePermissions(anyLong(), any())).thenReturn(sampleRole());

        mockMvc.perform(delete("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validPermissionRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("SUPERVISOR"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_UPDATE"})
    void updateStatusShouldReturn403WhenUserDoesNotHaveStatusUpdatePermission() throws Exception {
        mockMvc.perform(patch("/api/roles/1/status")
                        .contentType(APPLICATION_JSON)
                        .content(validStatusRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_STATUS_UPDATE"})
    void updateStatusShouldReturn200WhenUserHasStatusUpdatePermission() throws Exception {
        when(roleUseCase.updateStatus(anyLong(), anyBoolean())).thenReturn(sampleRole());

        mockMvc.perform(patch("/api/roles/1/status")
                        .contentType(APPLICATION_JSON)
                        .content(validStatusRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPERVISOR"));
    }

    @Test
    @WithMockUser(username = "user@ats.local", authorities = {"ROLE_STATUS_UPDATE"})
    void updateStatusShouldReturn404WhenRoleDoesNotExist() throws Exception {
        when(roleUseCase.updateStatus(99L, false))
                .thenThrow(new RoleNotFoundException("Role not found: 99"));

        mockMvc.perform(patch("/api/roles/99/status")
                        .contentType(APPLICATION_JSON)
                        .content(validStatusRequestJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_FOUND"));
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
