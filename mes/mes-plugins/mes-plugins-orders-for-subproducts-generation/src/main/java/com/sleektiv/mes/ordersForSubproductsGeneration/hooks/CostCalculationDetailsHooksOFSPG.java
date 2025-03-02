package com.sleektiv.mes.ordersForSubproductsGeneration.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.costCalculation.constants.CostCalculationFields;
import com.sleektiv.mes.ordersForSubproductsGeneration.constants.CostCalculationFieldsOFSPG;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class CostCalculationDetailsHooksOFSPG {

    @Autowired
    private ParameterService parameterService;

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        if (form.getEntityId() == null) {
            CheckBoxComponent fieldComponent = (CheckBoxComponent) view.getComponentByReference("includeComponents");
            boolean check = parameterService.getParameter().getBooleanField("includeComponents");
            fieldComponent.setChecked(check);
            fieldComponent.requestComponentUpdateState();
        }
        enableSaveNominalCostForComponentButton(view);
    }

    private void enableSaveNominalCostForComponentButton(final ViewDefinitionState view) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonActionItem saveNominalCostsForComponent = window.getRibbon()
                .getGroupByName(CostCalculationFieldsOFSPG.SAVE_COSTS_EXTENSION)
                .getItemByName(CostCalculationFieldsOFSPG.NOMINAL_COSTS_FOR_COMPONENTS);
        CheckBoxComponent generatedField = (CheckBoxComponent) view.getComponentByReference(CostCalculationFields.GENERATED);
        CheckBoxComponent includeComponents = (CheckBoxComponent) view
                .getComponentByReference(CostCalculationFields.INCLUDE_COMPONENTS);
        includeComponents.setEnabled(!generatedField.isChecked());
        includeComponents.requestComponentUpdateState();
        boolean enable = false;

        if (generatedField.isChecked() && includeComponents.isChecked()) {
            enable = true;
        }
        saveNominalCostsForComponent.setEnabled(enable);
        saveNominalCostsForComponent.requestUpdate(true);
    }
}
