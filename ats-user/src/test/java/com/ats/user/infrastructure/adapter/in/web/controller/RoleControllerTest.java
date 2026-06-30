package com.ats.user.infrastructure.adapter.in.web.controller;

import com.ats.user.AtsUserApplication;
import com.ats.user.domain.exception.PermissionNotFoundException;
import com.ats.user.domain.exception.RoleAlreadyExistsException;
import com.ats.user.domain.exception.RoleNotFoundException;
import com.ats.user.domain.model.Page;
import com.ats.user.domain.model.PageQuery;
import com.ats.user.domain.model.Permission;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.in.RoleUseCase;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
class RoleControllerTest {

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
    @WithMockUser(authorities = {"ROLE_CREATE"})
    void createShouldReturn201WhenRequestIsValid() throws Exception {
        when(roleUseCase.create(any())).thenReturn(sampleRole());

        mockMvc.perform(post("/api/roles")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateRoleRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SUPERVISOR"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_CREATE"})
    void createShouldReturn409WhenRoleAlreadyExists() throws Exception {
        when(roleUseCase.create(any()))
                .thenThrow(new RoleAlreadyExistsException("Role already exists: SUPERVISOR"));

        mockMvc.perform(post("/api/roles")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateRoleRequestJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ROLE_ALREADY_EXISTS"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_READ"})
    void listShouldReturn200WithPagedResponse() throws Exception {
        when(roleUseCase.listRoles(null, null, new PageQuery(0, 20)))
                .thenReturn(new Page<>(List.of(sampleRole()), 0, 20, 1));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("SUPERVISOR"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_READ"})
    void listShouldSupportSearchAndPaginationParams() throws Exception {
        when(roleUseCase.listRoles("ADMIN", true, new PageQuery(1, 10)))
                .thenReturn(new Page<>(List.of(), 1, 10, 0));

        mockMvc.perform(get("/api/roles?search=ADMIN&active=true&page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_READ"})
    void getByIdShouldReturn200WithRoleDetailWhenRoleExists() throws Exception {
        var permission = Permission.builder()
                .id(1L).code("USER_READ").name("Ver Usuarios")
                .resource("users").action("read").active(true)
                .build();
        var role = Role.builder()
                .id(1L).name("SUPERVISOR").description("Rol supervisor")
                .active(true).permissions(Set.of(permission))
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(roleUseCase.getById(1L)).thenReturn(role);

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPERVISOR"))
                .andExpect(jsonPath("$.permissions[0].code").value("USER_READ"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_READ"})
    void getByIdShouldReturn404WhenRoleNotFound() throws Exception {
        when(roleUseCase.getById(99L))
                .thenThrow(new RoleNotFoundException("Role not found: 99"));

        mockMvc.perform(get("/api/roles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_UPDATE"})
    void updateShouldReturn200WhenRequestIsValid() throws Exception {
        when(roleUseCase.update(anyLong(), any())).thenReturn(sampleRole());

        mockMvc.perform(put("/api/roles/1")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateRoleRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPERVISOR"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_UPDATE"})
    void updateShouldReturn404WhenRoleNotFound() throws Exception {
        when(roleUseCase.update(anyLong(), any()))
                .thenThrow(new RoleNotFoundException("Role not found: 99"));

        mockMvc.perform(put("/api/roles/99")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateRoleRequestJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_STATUS_UPDATE"})
    void updateStatusShouldReturn200WhenRequestIsValid() throws Exception {
        when(roleUseCase.updateStatus(anyLong(), anyBoolean())).thenReturn(sampleRole());

        mockMvc.perform(patch("/api/roles/1/status")
                        .contentType(APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPERVISOR"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_STATUS_UPDATE"})
    void updateStatusShouldReturn404WhenRoleNotFound() throws Exception {
        when(roleUseCase.updateStatus(anyLong(), anyBoolean()))
                .thenThrow(new RoleNotFoundException("Role not found: 99"));

        mockMvc.perform(patch("/api/roles/99/status")
                        .contentType(APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_DELETE"})
    void deleteShouldReturn204WhenRoleExists() throws Exception {
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_DELETE"})
    void deleteShouldReturn404WhenRoleNotFound() throws Exception {
        doThrow(new RoleNotFoundException("Role not found: 99"))
                .when(roleUseCase).delete(99L);

        mockMvc.perform(delete("/api/roles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_PERMISSION_ASSIGN"})
    void assignPermissionsShouldReturn200WhenRequestIsValid() throws Exception {
        var permission = Permission.builder().id(1L).code("USER_READ").build();
        var role = Role.builder()
                .id(1L).name("SUPERVISOR")
                .permissions(Set.of(permission))
                .build();

        when(roleUseCase.assignPermissions(anyLong(), any())).thenReturn(role);

        mockMvc.perform(put("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validAssignPermissionRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("SUPERVISOR"))
                .andExpect(jsonPath("$.permissionIds[0]").value(1));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_PERMISSION_ASSIGN"})
    void assignPermissionsShouldReturn400WhenPermissionIdsEmpty() throws Exception {
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
    @WithMockUser(authorities = {"ROLE_PERMISSION_ASSIGN"})
    void assignPermissionsShouldReturn404WhenRoleNotFound() throws Exception {
        when(roleUseCase.assignPermissions(anyLong(), any()))
                .thenThrow(new RoleNotFoundException("Role not found: 99"));

        mockMvc.perform(put("/api/roles/99/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validAssignPermissionRequestJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROLE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_PERMISSION_ASSIGN"})
    void assignPermissionsShouldReturn404WhenPermissionNotFound() throws Exception {
        when(roleUseCase.assignPermissions(anyLong(), any()))
                .thenThrow(new PermissionNotFoundException("One or more permissions not found"));

        mockMvc.perform(put("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validAssignPermissionRequestJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PERMISSION_NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_PERMISSION_REMOVE"})
    void deletePermissionsShouldReturn200WhenRequestIsValid() throws Exception {
        var permission = Permission.builder().id(1L).code("USER_READ").build();
        var role = Role.builder()
                .id(1L).name("SUPERVISOR")
                .permissions(new HashSet<>())
                .build();

        when(roleUseCase.deletePermissions(anyLong(), any())).thenReturn(role);

        mockMvc.perform(delete("/api/roles/1/permissions")
                        .contentType(APPLICATION_JSON)
                        .content(validAssignPermissionRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleName").value("SUPERVISOR"))
                .andExpect(jsonPath("$.permissionIds").isEmpty());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_PERMISSION_REMOVE"})
    void removePermissionShouldReturn204WhenPermissionRemoved() throws Exception {
        mockMvc.perform(delete("/api/roles/1/permissions/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_PERMISSION_REMOVE"})
    void removePermissionShouldReturn404WhenNotFound() throws Exception {
        doThrow(new RoleNotFoundException("Role not found: 1"))
                .when(roleUseCase).removePermission(1L, 99L);

        mockMvc.perform(delete("/api/roles/1/permissions/99"))
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
                  "description": "Rol supervisor"
                }
                """;
    }

    private String validAssignPermissionRequestJson() {
        return """
                {
                  "permissionIds": [1, 2]
                }
                """;
    }
}
