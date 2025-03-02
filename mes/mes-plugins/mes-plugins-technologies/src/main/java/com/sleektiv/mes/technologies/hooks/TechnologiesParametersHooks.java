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
package com.sleektiv.mes.technologies.hooks;

import com.sleektiv.mes.technologies.constants.ParameterFieldsT;
import com.sleektiv.mes.technologies.constants.Range;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.AwesomeDynamicListComponent;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.LookupComponent;
import org.springframework.stereotype.Service;

@Service
public class TechnologiesParametersHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        setDimensionControlAttributesEnabled(view);
        setDivisionField(view);
    }

    private void setDimensionControlAttributesEnabled(final ViewDefinitionState view) {
        CheckBoxComponent dimensionControlOfProductsCheckBox = (CheckBoxComponent) view.getComponentByReference(ParameterFieldsT.DIMENSION_CONTROL_OF_PRODUCTS);
        AwesomeDynamicListComponent dimensionControlAttributesADL = (AwesomeDynamicListComponent) view
                .getComponentByReference(ParameterFieldsT.DIMENSION_CONTROL_ATTRIBUTES);

        if (dimensionControlOfProductsCheckBox.isChecked()) {
            dimensionControlAttributesADL.setEnabled(true);
            dimensionControlAttributesADL.getFormComponents().forEach(formComponent -> formComponent.setFormEnabled(true));
        } else {
            dimensionControlAttributesADL.setEnabled(false);
            dimensionControlAttributesADL.setFieldValue(null);
        }

        dimensionControlAttributesADL.requestComponentUpdateState();
    }

    private void setDivisionField(final ViewDefinitionState view) {
        FieldComponent rangeField = (FieldComponent) view.getComponentByReference(ParameterFieldsT.RANGE);
        LookupComponent divisionField = (LookupComponent) view.getComponentByReference(ParameterFieldsT.DIVISION);

        String range = (String) rangeField.getFieldValue();

        boolean isOneDivision = Range.ONE_DIVISION.getStringValue().equals(range);
        boolean isManyDivisions = Range.MANY_DIVISIONS.getStringValue().equals(range);

        if (isManyDivisions) {
            divisionField.setFieldValue(null);
        }

        divisionField.setVisible(isOneDivision);
        divisionField.requestComponentUpdateState();
    }

}
