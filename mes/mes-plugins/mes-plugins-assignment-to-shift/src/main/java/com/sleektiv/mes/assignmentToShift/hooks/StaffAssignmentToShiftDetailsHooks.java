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
package com.sleektiv.mes.assignmentToShift.hooks;

import com.sleektiv.mes.assignmentToShift.constants.StaffAssignmentToShiftFields;
import com.sleektiv.mes.assignmentToShift.criteriaModifiers.StaffCriteriaModifier;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sleektiv.mes.assignmentToShift.constants.OccupationType.OTHER_CASE;
import static com.sleektiv.mes.assignmentToShift.constants.OccupationType.WORK_ON_LINE;
import static com.sleektiv.mes.assignmentToShift.constants.StaffAssignmentToShiftFields.*;
import static com.sleektiv.model.constants.DictionaryItemFields.NAME;
import static com.sleektiv.model.constants.DictionaryItemFields.TECHNICAL_CODE;

@Service
public class StaffAssignmentToShiftDetailsHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private StaffCriteriaModifier staffCriteriaModifier;

    public void setCriteriaModifiers(final ViewDefinitionState view) {
        LookupComponent staffLookup = (LookupComponent) view.getComponentByReference(StaffAssignmentToShiftFields.WORKER);

        FormComponent staffAssignmentToShiftForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity staffAssignmentToShift = staffAssignmentToShiftForm.getEntity();
        Entity assignmentToShift = staffAssignmentToShift.getBelongsToField(StaffAssignmentToShiftFields.ASSIGNMENT_TO_SHIFT);
        staffCriteriaModifier.setFilterParameters(staffLookup, assignmentToShift);
    }

    public void setFieldsEnabledWhenTypeIsSpecific(final ViewDefinitionState view) {
        FieldComponent occupationType = (FieldComponent) view.getComponentByReference(OCCUPATION_TYPE);

        Entity dictionaryItem = findDictionaryItemByName(occupationType.getFieldValue().toString());

        if (dictionaryItem == null) {
            setFieldsEnabled(view, false, false);
        } else {
            String occupationTypeTechnicalCode = dictionaryItem.getStringField(TECHNICAL_CODE);

            if (WORK_ON_LINE.getStringValue().equals(occupationTypeTechnicalCode)) {
                setFieldsEnabled(view, true, false);
            } else if (OTHER_CASE.getStringValue().equals(occupationTypeTechnicalCode)) {
                setFieldsEnabled(view, false, true);
            } else {
                setFieldsEnabled(view, false, false);
            }
        }
    }

    private void setFieldsEnabled(final ViewDefinitionState view, final boolean visibleOrRequiredProductionLine,
            final boolean visibleOrRequiredOccupationTypeName) {
        FieldComponent productionLine = (FieldComponent) view.getComponentByReference(PRODUCTION_LINE);
        FieldComponent occupationTypeName = (FieldComponent) view.getComponentByReference(OCCUPATION_TYPE_NAME);

        productionLine.setVisible(visibleOrRequiredProductionLine);
        productionLine.setRequired(visibleOrRequiredProductionLine);
        productionLine.requestComponentUpdateState();
        occupationTypeName.setVisible(visibleOrRequiredOccupationTypeName);
        occupationTypeName.setRequired(visibleOrRequiredOccupationTypeName);
        occupationTypeName.requestComponentUpdateState();
    }

    public void setOccupationTypeToDefault(final ViewDefinitionState view) {
        FormComponent staffAssignmentToShiftForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent occupationType = (FieldComponent) view.getComponentByReference(OCCUPATION_TYPE);

        if ((staffAssignmentToShiftForm.getEntityId() == null) && (occupationType.getFieldValue() == null)) {
            Entity dictionaryItem = findDictionaryItemByTechnicalCode(WORK_ON_LINE.getStringValue());

            if (dictionaryItem != null) {
                String occupationTypeName = dictionaryItem.getStringField(NAME);

                occupationType.setFieldValue(occupationTypeName);
                occupationType.requestComponentUpdateState();
            }
        }
    }

    protected Entity findDictionaryItemByName(final String name) {
        return dataDefinitionService.get("sleektivModel", "dictionaryItem").find().add(SearchRestrictions.eq(NAME, name))
                .uniqueResult();
    }

    protected Entity findDictionaryItemByTechnicalCode(final String technicalCode) {
        return dataDefinitionService.get("sleektivModel", "dictionaryItem").find()
                .add(SearchRestrictions.eq(TECHNICAL_CODE, technicalCode)).uniqueResult();
    }

}
