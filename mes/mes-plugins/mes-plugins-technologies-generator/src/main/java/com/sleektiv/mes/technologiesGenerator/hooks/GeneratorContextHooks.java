package com.sleektiv.mes.technologiesGenerator.hooks;

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import org.springframework.stereotype.Service;

@Service
public class GeneratorContextHooks {

    public boolean onDelete(final DataDefinition generatorContextDD, final Entity generatorContext) {

        if(!generatorContext.getDataDefinition().get(generatorContext.getId()).getHasManyField("technologies").isEmpty()){
            generatorContext.addGlobalError("technologiesGenerator.generate.deleteContext.error", false);
            return false;
        }
        return true;
    }
}
