package com.ats.user.domain.port.in;

import java.util.List;

public interface ModuleUserCase {
        Module createModule(Module module);
        List<Module> getModules();
        Module getModuleById(Long id);
        Module updateModule(Module module);
        void deleteModuleById(Long id);

}
