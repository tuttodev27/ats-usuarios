package com.ats.user.infrastructure.adapter.in.web.security.controller;

import com.ats.user.AtsUserApplication;
import com.ats.user.domain.exception.MenuNotFoundException;
import com.ats.user.domain.exception.ModuleNotFoundException;
import com.ats.user.domain.model.Menu;
import com.ats.user.domain.port.in.MenuUseCase;
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
import java.util.List;

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
class MenuControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuUseCase menuUseCase;

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
    void createShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/menus")
                        .contentType(APPLICATION_JSON)
                        .content(validMenuRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(authorities = {"MENU_READ"})
    void createShouldReturn403WhenUserDoesNotHaveCreatePermission() throws Exception {
        mockMvc.perform(post("/api/menus")
                        .contentType(APPLICATION_JSON)
                        .content(validMenuRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(authorities = {"MENU_CREATE"})
    void createShouldReturn201WhenUserHasCreatePermission() throws Exception {
        when(menuUseCase.create(any())).thenReturn(sampleMenu());

        mockMvc.perform(post("/api/menus")
                        .contentType(APPLICATION_JSON)
                        .content(validMenuRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.path").value("/dashboard"));
    }

    @Test
    @WithMockUser(authorities = {"MENU_CREATE"})
    void createShouldReturn404WhenModuleDoesNotExist() throws Exception {
        when(menuUseCase.create(any())).thenThrow(new ModuleNotFoundException("Module not found: 99"));

        mockMvc.perform(post("/api/menus")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Dashboard",
                                  "path": "/dashboard",
                                  "icon": "dashboard",
                                  "orderIndex": 1,
                                  "requiredPermissionCode": "ATS_DASHBOARD_VIEW",
                                  "active": true,
                                  "moduleId": 99
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MODULE_NOT_FOUND"));
    }

    @Test
    void listShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(authorities = {"MENU_READ"})
    void listShouldReturn200WhenUserHasReadPermission() throws Exception {
        when(menuUseCase.list()).thenReturn(List.of(sampleMenu()));

        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Dashboard"));
    }

    @Test
    @WithMockUser(authorities = {"MENU_READ"})
    void getByIdShouldReturn404WhenMissing() throws Exception {
        when(menuUseCase.getById(99L)).thenThrow(new MenuNotFoundException("Menu not found: 99"));

        mockMvc.perform(get("/api/menus/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MENU_NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = {"MENU_UPDATE"})
    void updateShouldReturn200WhenUserHasUpdatePermission() throws Exception {
        when(menuUseCase.update(anyLong(), any())).thenReturn(sampleMenu());

        mockMvc.perform(put("/api/menus/1")
                        .contentType(APPLICATION_JSON)
                        .content(validMenuRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").value("/dashboard"));
    }

    @Test
    void updateStatusShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(patch("/api/menus/1/status")
                        .contentType(APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(authorities = {"MENU_READ"})
    void updateStatusShouldReturn403WhenUserDoesNotHaveStatusUpdatePermission() throws Exception {
        mockMvc.perform(patch("/api/menus/1/status")
                        .contentType(APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(authorities = {"MENU_STATUS_UPDATE"})
    void updateStatusShouldReturn200WhenUserHasStatusUpdatePermission() throws Exception {
        when(menuUseCase.updateStatus(anyLong(), anyBoolean())).thenReturn(sampleMenu());

        mockMvc.perform(patch("/api/menus/1/status")
                        .contentType(APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").value("/dashboard"));
    }

    @Test
    @WithMockUser(authorities = {"MENU_DELETE"})
    void deleteShouldReturn204WhenUserHasDeletePermission() throws Exception {
        mockMvc.perform(delete("/api/menus/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"MENU_DELETE"})
    void deleteShouldReturn404WhenMissing() throws Exception {
        doThrow(new MenuNotFoundException("Menu not found: 99"))
                .when(menuUseCase).delete(99L);

        mockMvc.perform(delete("/api/menus/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MENU_NOT_FOUND"));
    }

    private Menu sampleMenu() {
        return Menu.builder()
                .id(1L)
                .title("Dashboard")
                .path("/dashboard")
                .icon("dashboard")
                .moduleId(1L)
                .orderIndex(1)
                .requiredPermissionCode("ATS_DASHBOARD_VIEW")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private String validMenuRequestJson() {
        return """
                {
                  "title": "Dashboard",
                  "path": "/dashboard",
                  "icon": "dashboard",
                  "orderIndex": 1,
                  "requiredPermissionCode": "ATS_DASHBOARD_VIEW",
                  "active": true,
                  "moduleId": 1
                }
                """;
    }
}
