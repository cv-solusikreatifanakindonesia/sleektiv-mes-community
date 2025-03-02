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
package com.sleektiv.mes.productionLines.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class DivisionDetailsHooksPL {

    

    public void fillCriteriaModifiers(final ViewDefinitionState viewDefinitionState) {
        GridComponent workstations = (GridComponent) viewDefinitionState.getComponentByReference("workstations");
        FormComponent form = (FormComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_FORM);
        if (form.getEntityId() != null) {
            FilterValueHolder filter = workstations.getFilterValue();
            filter.put("division", form.getEntityId());
            workstations.setFilterValue(filter);
        }
        workstations.reloadEntities();
    }
}
