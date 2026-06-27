package com.ats.user.domain.port.in;

import com.ats.user.domain.model.Module;

import java.util.List;

public interface ModuleUseCase {
        Module createModule(Module module);
        List<Module> getModules();
        Module getModuleById(Long id);
        Module updateModule(Long id, Module module);
        Module updateModuleStatus(Long id, boolean active);
        void deleteModuleById(Long id);

}
