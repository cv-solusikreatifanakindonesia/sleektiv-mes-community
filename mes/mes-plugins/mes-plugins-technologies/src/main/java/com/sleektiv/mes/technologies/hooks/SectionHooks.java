package com.sleektiv.mes.technologies.hooks;

import com.sleektiv.mes.technologies.constants.OperationProductInComponentFields;
import com.sleektiv.mes.technologies.constants.SectionFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SectionHooks {

    public boolean validatesWith(final DataDefinition sectionDD, final Entity section) {
        return checkSectionUnit(sectionDD, section);
    }

    private boolean checkSectionUnit(final DataDefinition sectionDD, final Entity section) {
        List<Entity> sections = section.getBelongsToField(SectionFields.OPERATION_PRODUCT_IN_COMPONENT).getHasManyField(OperationProductInComponentFields.SECTIONS);
        if (!sections.isEmpty()) {
            String unit = section.getStringField(SectionFields.UNIT);
            Entity firstSection = sections.get(0);

            if (!unit.equals(firstSection.getStringField(SectionFields.UNIT))
                    && !(firstSection.getId().equals(section.getId()) && sections.size() == 1)) {
                section.addError(sectionDD.getField(SectionFields.UNIT), "technologies.section.unit.error.differentUnits");

                return false;
            }
        }
        return true;
    }

}
