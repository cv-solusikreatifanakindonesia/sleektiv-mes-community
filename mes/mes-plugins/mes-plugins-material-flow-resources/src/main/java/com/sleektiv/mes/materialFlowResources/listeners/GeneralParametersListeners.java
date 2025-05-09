/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
 * Version: 1.4
 * <p>
 * This file is part of Sleektiv.
 * <p>
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.materialFlowResources.listeners;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.materialFlowResources.constants.ParameterFieldsMFR;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.AwesomeDynamicListComponent;

@Service
public class GeneralParametersListeners {

	public void costsSourceChanged(final ViewDefinitionState state, final ComponentState componentState, final String[] args) {
		AwesomeDynamicListComponent warehousesADL = (AwesomeDynamicListComponent) state.getComponentByReference(ParameterFieldsMFR.WAREHOUSES);

		String costsSource = (String) componentState.getFieldValue();

		if ("01mes".equals(costsSource)) {
			warehousesADL.setEnabled(true);
		} else {
			warehousesADL.setFieldValue(null);
			warehousesADL.setEnabled(false);
		}
	}

}
