package com.ats.user.infrastructure.adapter.in.web.controller;

import com.ats.user.AtsUserApplication;
import com.ats.user.domain.exception.ModuleAlreadyExistsException;
import com.ats.user.domain.exception.ModuleNotFoundException;
import com.ats.user.domain.model.Module;
import com.ats.user.domain.port.in.ModuleUseCase;
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
class ModuleControllerTest {

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
    @WithMockUser(authorities = {"MODULE_CREATE"})
    void createShouldReturn201WhenRequestIsValid() throws Exception {
        when(moduleUseCase.createModule(any())).thenReturn(sampleModule());

        mockMvc.perform(post("/api/modules")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateModuleRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ATS"))
                .andExpect(jsonPath("$.name").value("ATS Core"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_CREATE"})
    void createShouldReturn409WhenModuleAlreadyExists() throws Exception {
        when(moduleUseCase.createModule(any()))
                .thenThrow(new ModuleAlreadyExistsException("Module already exists: ATS"));

        mockMvc.perform(post("/api/modules")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateModuleRequestJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("MODULE_ALREADY_EXISTS"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_READ"})
    void listShouldReturn200() throws Exception {
        when(moduleUseCase.getModules()).thenReturn(List.of(sampleModule()));

        mockMvc.perform(get("/api/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("ATS"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_READ"})
    void getByIdShouldReturn200WhenModuleExists() throws Exception {
        when(moduleUseCase.getModuleById(1L)).thenReturn(sampleModule());

        mockMvc.perform(get("/api/modules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ATS"))
                .andExpect(jsonPath("$.name").value("ATS Core"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_READ"})
    void getByIdShouldReturn404WhenModuleNotFound() throws Exception {
        when(moduleUseCase.getModuleById(99L))
                .thenThrow(new ModuleNotFoundException("Module not found: 99"));

        mockMvc.perform(get("/api/modules/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MODULE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_UPDATE"})
    void updateShouldReturn200WhenRequestIsValid() throws Exception {
        when(moduleUseCase.updateModule(anyLong(), any())).thenReturn(sampleModule());

        mockMvc.perform(put("/api/modules/1")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateModuleRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ATS"))
                .andExpect(jsonPath("$.name").value("ATS Core"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_UPDATE"})
    void updateShouldReturn404WhenModuleNotFound() throws Exception {
        when(moduleUseCase.updateModule(anyLong(), any()))
                .thenThrow(new ModuleNotFoundException("Module not found: 99"));

        mockMvc.perform(put("/api/modules/99")
                        .contentType(APPLICATION_JSON)
                        .content(validUpdateModuleRequestJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MODULE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_STATUS_UPDATE"})
    void updateStatusShouldReturn200WhenRequestIsValid() throws Exception {
        when(moduleUseCase.updateModuleStatus(anyLong(), anyBoolean())).thenReturn(sampleModule());

        mockMvc.perform(patch("/api/modules/1/status")
                        .contentType(APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_STATUS_UPDATE"})
    void updateStatusShouldReturn404WhenModuleNotFound() throws Exception {
        when(moduleUseCase.updateModuleStatus(anyLong(), anyBoolean()))
                .thenThrow(new ModuleNotFoundException("Module not found: 99"));

        mockMvc.perform(patch("/api/modules/99/status")
                        .contentType(APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MODULE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(authorities = {"MODULE_DELETE"})
    void deleteShouldReturn204WhenModuleExists() throws Exception {
        mockMvc.perform(delete("/api/modules/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"MODULE_DELETE"})
    void deleteShouldReturn404WhenModuleNotFound() throws Exception {
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

    private String validCreateModuleRequestJson() {
        return """
                {
                  "code": "ATS",
                  "name": "ATS Core",
                  "description": "Modulo principal",
                  "active": true
                }
                """;
    }

    private String validUpdateModuleRequestJson() {
        return """
                {
                  "code": "ATS",
                  "name": "ATS Core",
                  "description": "Modulo principal actualizado"
                }
                """;
    }
}
