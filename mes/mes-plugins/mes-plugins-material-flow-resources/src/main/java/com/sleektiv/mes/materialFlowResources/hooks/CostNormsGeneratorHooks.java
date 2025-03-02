package com.sleektiv.mes.materialFlowResources.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.materialFlowResources.constants.CostNormsGeneratorFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class CostNormsGeneratorHooks {

	public void onBeforeRender(final ViewDefinitionState view) {
		FormComponent costNormsGeneratorForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
		LookupComponent productLookup = (LookupComponent) view.getComponentByReference("productsLookup");

		Entity costNormsGenerator = costNormsGeneratorForm.getEntity();

		String costsSource = costNormsGenerator.getStringField(CostNormsGeneratorFields.COSTS_SOURCE);

		FilterValueHolder filterValueHolder = productLookup.getFilterValue();
		filterValueHolder.put("costsSource", costsSource);

		productLookup.setFilterValue(filterValueHolder);
	}

}
