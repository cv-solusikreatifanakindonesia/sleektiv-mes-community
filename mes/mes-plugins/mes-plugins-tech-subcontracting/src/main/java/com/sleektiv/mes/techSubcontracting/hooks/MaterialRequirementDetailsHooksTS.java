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
package com.sleektiv.mes.techSubcontracting.hooks;

import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import static com.sleektiv.mes.technologies.constants.MrpAlgorithm.COMPONENTS_AND_SUBCONTRACTORS_PRODUCTS;

@Service
public class MaterialRequirementDetailsHooksTS {

    public void setMrpAlgoritmValue(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        if (form.getEntityId() != null) {
            return;
        }
        FieldComponent mrpAlgoritm = (FieldComponent) view.getComponentByReference("mrpAlgorithm");
        mrpAlgoritm.setFieldValue(COMPONENTS_AND_SUBCONTRACTORS_PRODUCTS.getStringValue());
        mrpAlgoritm.requestComponentUpdateState();
    }

}
