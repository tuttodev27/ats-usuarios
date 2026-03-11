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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private MockMvc mockMvc;

    @Autowired
    void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

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
    @WithMockUser(username = "recruiter@ats.local", roles = {"RECRUITER"})
    void saveUserShouldReturn403WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateUserRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", roles = {"ADMIN"})
    void saveUserShouldReturn201WhenRoleIsAdmin() throws Exception {
        when(userUseCase.create(any(), anyString(), anyString()))
                .thenReturn(UserDumpData.domainUserExisting());

        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(validCreateUserRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("admin@ats.local"));
    }

    @Test
    void deleteUserShouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "recruiter@ats.local", roles = {"RECRUITER"})
    void deleteUserShouldReturn403WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @WithMockUser(username = "admin@ats.local", roles = {"ADMIN"})
    void deleteUserShouldReturn204WhenRoleIsAdmin() throws Exception {
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
}
