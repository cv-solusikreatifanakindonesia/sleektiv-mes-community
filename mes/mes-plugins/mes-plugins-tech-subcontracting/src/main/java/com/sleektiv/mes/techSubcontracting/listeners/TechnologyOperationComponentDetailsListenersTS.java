package com.sleektiv.mes.techSubcontracting.listeners;

import com.sleektiv.mes.techSubcontracting.constants.OperationFieldsTS;
import com.sleektiv.mes.techSubcontracting.hooks.TechnologyOperationComponentDetailsHooksTS;
import com.sleektiv.mes.technologies.constants.TechnologyOperationComponentFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.LookupComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TechnologyOperationComponentDetailsListenersTS {

    @Autowired
    private NumberService numberService;

    @Autowired
    private TechnologyOperationComponentDetailsHooksTS technologyOperationComponentDetailsHooksTS;

    public final void onIsSubcontractingChange(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        technologyOperationComponentDetailsHooksTS.setUnitCostField(view);
    }

    public void copySubcontractingFieldsFromLowerInstance(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        LookupComponent operationLookup = (LookupComponent) view.getComponentByReference(TechnologyOperationComponentFields.OPERATION);
        CheckBoxComponent isSubcontractingCheckBox = (CheckBoxComponent) view.getComponentByReference(OperationFieldsTS.IS_SUBCONTRACTING);
        FieldComponent unitCostField = (FieldComponent) view.getComponentByReference(OperationFieldsTS.UNIT_COST);

        Entity operation = operationLookup.getEntity();

        if (Objects.nonNull(operation)) {
            boolean isSubcontracting = operation.getBooleanField(OperationFieldsTS.IS_SUBCONTRACTING);

            isSubcontractingCheckBox.setChecked(isSubcontracting);
            isSubcontractingCheckBox.requestComponentUpdateState();
            unitCostField.setFieldValue(numberService.formatWithMinimumFractionDigits(operation.getDecimalField(OperationFieldsTS.UNIT_COST), 0));
            unitCostField.setEnabled(isSubcontracting);
            unitCostField.requestComponentUpdateState();
        }
    }

}
