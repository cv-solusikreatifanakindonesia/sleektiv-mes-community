package com.sleektiv.mes.cmmsMachineParts.hooks;

import com.google.common.base.Strings;
import com.sleektiv.mes.cmmsMachineParts.criteriaModifiers.OperationToolCriteriaModifiers;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OperationToolDetailsHooks {
    public static final String L_TOOL = "tool";
    public static final String L_UNIT = "unit";
    public static final String L_QUANTITY = "quantity";

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        if(view.isViewAfterRedirect() && Objects.isNull(form.getEntityId())) {
            FieldComponent quantityField = (FieldComponent) view.getComponentByReference(L_QUANTITY);
            quantityField.setFieldValue(1);
            quantityField.requestComponentUpdateState();
        }
        LookupComponent toolField = (LookupComponent) view.getComponentByReference(L_TOOL);
        FieldComponent unitField = (FieldComponent) view.getComponentByReference(L_UNIT);
        FieldComponent toolCategoryField = (FieldComponent) view.getComponentByReference("toolCategory");

        if(Strings.isNullOrEmpty((String) toolCategoryField.getFieldValue())) {
            toolField.setEnabled(false);
        } else {
            toolField.setEnabled(true);
        }

        if(toolField.isEmpty()) {
            unitField.setEnabled(true);
        } else {
            unitField.setEnabled(false);
        }
        setCriteria(view);
    }

    private void setCriteria(ViewDefinitionState view) {
        FieldComponent toolCategoryField = (FieldComponent) view.getComponentByReference("toolCategory");
        LookupComponent toolField = (LookupComponent) view.getComponentByReference("tool");
        FilterValueHolder filterValueHolder = toolField.getFilterValue();
        if(!Strings.isNullOrEmpty((String) toolCategoryField.getFieldValue())) {
            String toolCategory = (String) toolCategoryField.getFieldValue();
            filterValueHolder.put(OperationToolCriteriaModifiers.TOOL_CATEGORY, toolCategory);
        } else {
            filterValueHolder.remove(OperationToolCriteriaModifiers.TOOL_CATEGORY);
        }

        toolField.setFilterValue(filterValueHolder);
    }


}