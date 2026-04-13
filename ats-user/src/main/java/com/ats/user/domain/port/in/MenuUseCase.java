package com.ats.user.domain.port.in;

import com.ats.user.domain.model.Menu;

import java.util.List;

public interface MenuUseCase {
    Menu create(Menu menu);
    List<Menu> list();
    Menu getById(Long id);
    Menu update(Long id, Menu menu);
    void delete(Long id);
}
