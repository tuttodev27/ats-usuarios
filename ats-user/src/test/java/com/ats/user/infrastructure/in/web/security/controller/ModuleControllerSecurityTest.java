package com.ats.user.infrastructure.in.web.security.controller;

import com.ats.user.AtsUserApplication;
import com.ats.user.domain.exception.ModuleNotFoundException;
import com.ats.user.domain.model.Module;
import com.ats.user.domain.port.in.ModuleUseCase;
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
class ModuleControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModuleUseCase moduleUseCase;

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
    void createModuleShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/modules")
                        .contentType(APPLICATION_JSON)
                        .content(validModuleRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_READ"})
    void createModuleShouldReturn403WhenUserDoesNotHaveCreatePermission() throws Exception {
        mockMvc.perform(post("/api/modules")
                        .contentType(APPLICATION_JSON)
                        .content(validModuleRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_CREATE"})
    void createModuleShouldReturn201WhenUserHasCreatePermission() throws Exception {
        when(moduleUseCase.createModule(any())).thenReturn(sampleModule());

        mockMvc.perform(post("/api/modules")
                        .contentType(APPLICATION_JSON)
                        .content(validModuleRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ATS"));
    }

    @Test
    void listModulesShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/modules"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_CREATE"})
    void listModulesShouldReturn403WhenUserDoesNotHaveReadPermission() throws Exception {
        mockMvc.perform(get("/api/modules"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_READ"})
    void listModulesShouldReturn200WhenUserHasReadPermission() throws Exception {
        when(moduleUseCase.getModules()).thenReturn(List.of(sampleModule()));

        mockMvc.perform(get("/api/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("ATS"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_READ"})
    void getModuleByIdShouldReturn200WhenUserHasReadPermission() throws Exception {
        when(moduleUseCase.getModuleById(1L)).thenReturn(sampleModule());

        mockMvc.perform(get("/api/modules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ATS Core"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_READ"})
    void getModuleByIdShouldReturn404WhenModuleDoesNotExist() throws Exception {
        when(moduleUseCase.getModuleById(99L))
                .thenThrow(new ModuleNotFoundException("Module not found: 99"));

        mockMvc.perform(get("/api/modules/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MODULE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_READ"})
    void updateModuleShouldReturn403WhenUserDoesNotHaveUpdatePermission() throws Exception {
        mockMvc.perform(put("/api/modules/1")
                        .contentType(APPLICATION_JSON)
                        .content(validModuleRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_UPDATE"})
    void updateModuleShouldReturn200WhenUserHasUpdatePermission() throws Exception {
        when(moduleUseCase.updateModule(anyLong(), any())).thenReturn(sampleModule());

        mockMvc.perform(put("/api/modules/1")
                        .contentType(APPLICATION_JSON)
                        .content(validModuleRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ATS"));
    }

    @Test
    void updateModuleStatusShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(patch("/api/modules/1/status")
                        .contentType(APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_READ"})
    void updateModuleStatusShouldReturn403WhenUserDoesNotHaveStatusUpdatePermission() throws Exception {
        mockMvc.perform(patch("/api/modules/1/status")
                        .contentType(APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_STATUS_UPDATE"})
    void updateModuleStatusShouldReturn200WhenUserHasStatusUpdatePermission() throws Exception {
        when(moduleUseCase.updateModuleStatus(anyLong(), anyBoolean())).thenReturn(sampleModule());

        mockMvc.perform(patch("/api/modules/1/status")
                        .contentType(APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ATS"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_DELETE"})
    void deleteModuleShouldReturn204WhenUserHasDeletePermission() throws Exception {
        mockMvc.perform(delete("/api/modules/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"MODULE_READ"})
    void deleteModuleShouldReturn403WhenUserDoesNotHaveDeletePermission() throws Exception {
        mockMvc.perform(delete("/api/modules/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_DELETE"})
    void deleteModuleShouldReturn404WhenModuleDoesNotExist() throws Exception {
        doThrow(new ModuleNotFoundException("Module not found: 99"))
                .when(moduleUseCase).deleteModuleById(99L);

        mockMvc.perform(delete("/api/modules/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MODULE_NOT_FOUND"));
    }

    private Module sampleModule() {
        return Module.builder()
                .id(1L)
                .code("ATS")
                .name("ATS Core")
                .description("Modulo principal")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private String validModuleRequestJson() {
        return """
                {
                  "code": "ATS",
                  "name": "ATS Core",
                  "description": "Modulo principal",
                  "active": true
                }
                """;
    }
}
