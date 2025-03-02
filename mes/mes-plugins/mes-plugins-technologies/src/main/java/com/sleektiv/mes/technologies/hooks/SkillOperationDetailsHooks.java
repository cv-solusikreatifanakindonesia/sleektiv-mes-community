package com.sleektiv.mes.technologies.hooks;

import com.sleektiv.mes.basic.constants.SkillFields;
import com.sleektiv.mes.basic.constants.StaffSkillsFields;
import com.sleektiv.mes.technologies.constants.OperationSkillFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SkillOperationDetailsHooks {

    private static final String MAX_LEVEL = "maxLevel";

    private static final String L_SKILL_ID = "skillId";

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity opSkill = form.getEntity();
        FieldComponent maxSkillLevelField = (FieldComponent) view.getComponentByReference(MAX_LEVEL);
        maxSkillLevelField.setFieldValue(opSkill.getBelongsToField(OperationSkillFields.SKILL).getIntegerField(
                SkillFields.MAXIMUM_LEVEL));

        LookupComponent operationLookup = (LookupComponent) view.getComponentByReference(OperationSkillFields.OPERATION);

        FilterValueHolder filterValueHolder = operationLookup.getFilterValue();

        Long skillId = opSkill.getBelongsToField(StaffSkillsFields.SKILL).getId();

        if (Objects.isNull(skillId)) {
            filterValueHolder.remove(L_SKILL_ID);
        } else {
            filterValueHolder.put(L_SKILL_ID, skillId);
        }

        operationLookup.setFilterValue(filterValueHolder);
    }
}
