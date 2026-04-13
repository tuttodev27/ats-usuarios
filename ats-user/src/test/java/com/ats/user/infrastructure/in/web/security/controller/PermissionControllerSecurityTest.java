package com.ats.user.infrastructure.in.web.security.controller;

import com.ats.user.AtsUserApplication;
import com.ats.user.domain.model.Permission;
import com.ats.user.domain.port.in.PermissionUseCase;
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
class PermissionControllerSecurityTest {

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
    void listShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/permissions"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(authorities = {"MENU_READ"})
    void listShouldReturn403WhenUserDoesNotHavePermissionRead() throws Exception {
        mockMvc.perform(get("/api/permissions"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(authorities = {"PERMISSION_READ"})
    void listShouldReturn200WhenUserHasPermissionRead() throws Exception {
        when(permissionUseCase.list()).thenReturn(List.of(
                Permission.builder().id(1L).code("USER_READ").active(true).build(),
                Permission.builder().id(2L).code("ROLE_READ").active(true).build()
        ));

        mockMvc.perform(get("/api/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("USER_READ"))
                .andExpect(jsonPath("$[1].code").value("ROLE_READ"));
    }
}
