package com.ats.user.domain.port.in;

import com.ats.user.domain.model.Module;

import java.util.List;

public interface ModuleUserCase {
        Module createModule(Module module);
        List<Module> getModules();
        Module getModuleById(Long id);
        Module updateModule(Long id, Module module);
        void deleteModuleById(Long id);

}
