package com.ats.user.infrastructure.adapter.in.web.controller;

import com.ats.user.AtsUserApplication;
import com.ats.user.domain.model.Permission;
import com.ats.user.domain.port.in.PermissionUseCase;
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

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissionUseCase permissionUseCase;

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
    @WithMockUser(authorities = {"PERMISSION_READ"})
    void listShouldReturn200WithAllPermissions() throws Exception {
        when(permissionUseCase.list(null, null)).thenReturn(List.of(
                Permission.builder().id(1L).code("USER_READ").name("Ver Usuarios").active(true).build(),
                Permission.builder().id(2L).code("ROLE_READ").name("Ver Roles").active(true).build()
        ));

        mockMvc.perform(get("/api/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("USER_READ"))
                .andExpect(jsonPath("$[1].code").value("ROLE_READ"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(authorities = {"PERMISSION_READ"})
    void listShouldFilterByModuleId() throws Exception {
        when(permissionUseCase.list(1L, null)).thenReturn(List.of(
                Permission.builder().id(1L).code("USER_READ").name("Ver Usuarios").moduleId(1L).active(true).build()
        ));

        mockMvc.perform(get("/api/permissions?moduleId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("USER_READ"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(authorities = {"PERMISSION_READ"})
    void listShouldFilterByActive() throws Exception {
        when(permissionUseCase.list(null, true)).thenReturn(List.of(
                Permission.builder().id(1L).code("USER_READ").name("Ver Usuarios").active(true).build()
        ));

        mockMvc.perform(get("/api/permissions?active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("USER_READ"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(authorities = {"PERMISSION_READ"})
    void listShouldFilterByModuleIdAndActive() throws Exception {
        when(permissionUseCase.list(1L, true)).thenReturn(List.of(
                Permission.builder().id(1L).code("USER_READ").name("Ver Usuarios").moduleId(1L).active(true).build()
        ));

        mockMvc.perform(get("/api/permissions?moduleId=1&active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
