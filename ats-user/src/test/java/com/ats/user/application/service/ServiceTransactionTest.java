package com.ats.user.application.service;

import com.ats.user.domain.exception.RoleAlreadyExistsException;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.out.RoleRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ServiceTransactionTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepositoryPort roleRepository;

    @Test
    void createRole_shouldRollbackOnDuplicateName() {
        roleService.create(Role.builder()
                .name("TEST_UNIQUE")
                .active(true)
                .build());

        assertThrows(RoleAlreadyExistsException.class, () ->
                roleService.create(Role.builder()
                        .name("TEST_UNIQUE")
                        .active(true)
                        .build())
        );

        List<Role> roles = roleRepository.findAll().stream()
                .filter(r -> r.getName().equals("TEST_UNIQUE"))
                .toList();
        assertEquals(1, roles.size());
    }

    @Test
    void roleUpdate_shouldRollbackOnDuplicateName() {
        var role = roleService.create(Role.builder()
                .name("ROLE_A")
                .active(true)
                .build());
        roleService.create(Role.builder()
                .name("ROLE_B")
                .active(true)
                .build());

        assertThrows(RoleAlreadyExistsException.class, () ->
                roleService.update(role.getId(), Role.builder()
                        .name("ROLE_B")
                        .build())
        );

        var updated = roleService.getById(role.getId());
        assertEquals("ROLE_A", updated.getName());
    }
}
