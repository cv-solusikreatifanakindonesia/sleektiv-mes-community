/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
 * Version: 1.4
 *
 * This file is part of Sleektiv.
 *
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.basic.hooks;

import com.sleektiv.mes.basic.constants.SkillFields;
import com.sleektiv.mes.basic.constants.StaffSkillsFields;
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
public class StaffSkillDetailsHooks {

    

    private static final String L_STAFF_ID = "staffId";

    private static final String MAX_LEVEL = "maxLevel";

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent staffSkillForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent skillLookup = (LookupComponent) view.getComponentByReference(StaffSkillsFields.SKILL);

        Entity staffSkill = staffSkillForm.getEntity();
        Entity staff = staffSkill.getBelongsToField(StaffSkillsFields.STAFF);

        filterSkillLookup(skillLookup, staff);
        fillMaxSkill(view, skillLookup);
    }

    private void fillMaxSkill(final ViewDefinitionState view, final LookupComponent skillLookup) {
        Entity skill = skillLookup.getEntity();

        if(Objects.nonNull(skill)) {
            FieldComponent maxSkillLevelField = (FieldComponent) view.getComponentByReference(MAX_LEVEL);
            maxSkillLevelField.setFieldValue(skill.getIntegerField(SkillFields.MAXIMUM_LEVEL));
        }
    }

    private void filterSkillLookup(final LookupComponent skillLookup, final Entity staff) {
        FilterValueHolder filterValueHolder = skillLookup.getFilterValue();

        Long staffId = staff.getId();

        if (Objects.isNull(staffId)) {
            filterValueHolder.remove(L_STAFF_ID);
        } else {
            filterValueHolder.put(L_STAFF_ID, staffId);
        }

        skillLookup.setFilterValue(filterValueHolder);
    }

}
