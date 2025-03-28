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
package com.sleektiv.mes.materialFlowResources.hooks;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.materialFlowResources.constants.DocumentPositionParametersItemFields;
import com.sleektiv.mes.materialFlowResources.constants.ParameterFieldsMFR;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ParametersHooksMFR {

	private static final String TRANSLATION_PREFIX = "materialFlowResources.materialFlowResourcesParameters.documentPositionParameters.";

	@Autowired
	private TranslationService translationService;

	public void onCreate(final DataDefinition parameterDD, final Entity parameter) {
		setCostsSourceDefaultValue(parameter);
	}

	private void setCostsSourceDefaultValue(final Entity parameter) {
		if (Objects.isNull(parameter.getStringField(ParameterFieldsMFR.COSTS_SOURCE))) {
			parameter.setField(ParameterFieldsMFR.COSTS_SOURCE, "01mes");
		}
	}

	public boolean validatesWith(final DataDefinition parameterDD, final Entity parameter) {
		boolean automaticUpdateCostNorms = parameter.getBooleanField(ParameterFieldsMFR.AUTOMATIC_UPDATE_COST_NORMS);
		String costsSource = parameter.getStringField(ParameterFieldsMFR.COSTS_SOURCE);

		if (automaticUpdateCostNorms && Objects.isNull(costsSource)) {
			parameter.addError(parameterDD.getField(ParameterFieldsMFR.COSTS_SOURCE), "sleektivView.validate.field.error.missing");

			return false;
		}

		return true;
	}

	public void onBeforeRender(final ViewDefinitionState view) {
		GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

		for (Entity entity : grid.getEntities()) {
			if (!entity.getBooleanField(DocumentPositionParametersItemFields.FOR_ATTRIBUTE)) {
				String name = entity.getStringField("name");
				String displayName = translationService.translate(TRANSLATION_PREFIX + name, view.getLocale());

				entity.setField("name", displayName);
			}
		}
	}

	public void onBeforeRenderItemDetails(final ViewDefinitionState view) {
		FormComponent documentPositionParametersItemForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
		FieldComponent displayNameField = (FieldComponent) view.getComponentByReference("displayName");

		Entity item = documentPositionParametersItemForm.getPersistedEntityWithIncludedFormValues();
		String name = item.getStringField("name");
		String displayName = translationService.translate(TRANSLATION_PREFIX + name, displayNameField.getLocale());
		displayNameField.setFieldValue(displayName);

		boolean editable = item.getBooleanField("editable");
		((FieldComponent) view.getComponentByReference("checked")).setEnabled(editable);
		((FieldComponent) view.getComponentByReference("editable")).setEnabled(false);
	}

}
