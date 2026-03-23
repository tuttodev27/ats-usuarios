package com.ats.user.application.service;

import com.ats.user.domain.port.in.ModuleUserCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService implements ModuleUserCase {
    @Override
    public Module createModule(Module module) {
        return null;
    }

    @Override
    public List<Module> getModules() {
        return List.of();
    }

    @Override
    public Module getModuleById(Long id) {
        return null;
    }

    @Override
    public Module updateModule(Module module) {
        return null;
    }

    @Override
    public void deleteModuleById(Long id) {

    }
}
