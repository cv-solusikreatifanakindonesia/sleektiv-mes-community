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
package com.sleektiv.mes.technologies.validators;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.technologies.constants.TechnologicalProcessFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class TechnologicalProcessComponentValidators {

    private static final String L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_MISSING = "sleektivView.validate.field.error.missing";

    public boolean validatesWith(final DataDefinition technologicalProcessComponentDD, final Entity technologicalProcessComponent) {
        return checkRequiredFields(technologicalProcessComponentDD, technologicalProcessComponent);
    }

    public boolean checkRequiredFields(final DataDefinition dataDefinition, final Entity technologicalProcessComponent) {
        boolean isValid = true;
        boolean extendedTimeForSizeGroup = technologicalProcessComponent
                .getBooleanField(TechnologicalProcessFields.EXTENDED_TIME_FOR_SIZE_GROUP);
        if (extendedTimeForSizeGroup
                && technologicalProcessComponent.getIntegerField(TechnologicalProcessFields.INCREASE_PERCENT) == null) {
            technologicalProcessComponent.addError(dataDefinition.getField(TechnologicalProcessFields.INCREASE_PERCENT),
                    L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_MISSING);
            isValid = false;
        }
        if (extendedTimeForSizeGroup && technologicalProcessComponent.getField(TechnologicalProcessFields.SIZE_GROUP) == null) {
            technologicalProcessComponent.addError(dataDefinition.getField(TechnologicalProcessFields.SIZE_GROUP),
                    L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_MISSING);
            isValid = false;
        }
        return isValid;
    }

}
