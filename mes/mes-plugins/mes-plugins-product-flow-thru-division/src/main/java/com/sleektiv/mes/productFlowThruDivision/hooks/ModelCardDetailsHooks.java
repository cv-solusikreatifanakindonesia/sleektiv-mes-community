package com.sleektiv.mes.productFlowThruDivision.hooks;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.productFlowThruDivision.constants.ModelCardFields;
import com.sleektiv.mes.productFlowThruDivision.constants.ParameterFieldsPFTD;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.*;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ModelCardDetailsHooks {

    private static final String L_GENERATE = "generate";

    private static final String L_EXPORT = "export";

    private static final String L_PDF = "pdf";

    @Autowired
    private ParameterService parameterService;

    public void onBeforeRender(final ViewDefinitionState view) {
        fillDefaultsFromParameters(view);
        setRibbonEnabled(view);
        setFormEnabled(view);
    }

    private void setRibbonEnabled(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        CheckBoxComponent generated = (CheckBoxComponent) view.getComponentByReference(ModelCardFields.GENERATED);

        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        Ribbon ribbon = window.getRibbon();

        RibbonGroup generateRibbonGroup = ribbon.getGroupByName(L_GENERATE);
        RibbonGroup exportRibbonGroup = ribbon.getGroupByName(L_EXPORT);

        RibbonActionItem generateRibbonActionItem = generateRibbonGroup.getItemByName(L_GENERATE);
        RibbonActionItem exportRibbonActionItem = exportRibbonGroup.getItemByName(L_PDF);

        Long modelCardId = form.getEntityId();

        boolean entityIdIsNotNull = Objects.nonNull(modelCardId);
        boolean isGenerated = generated.isChecked();

        generateRibbonActionItem.setEnabled(entityIdIsNotNull && !isGenerated);
        generateRibbonActionItem.requestUpdate(true);
        exportRibbonActionItem.setEnabled(entityIdIsNotNull && isGenerated);
        exportRibbonActionItem.requestUpdate(true);
    }

    private void setFormEnabled(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        CheckBoxComponent generated = (CheckBoxComponent) view.getComponentByReference(ModelCardFields.GENERATED);

        Long modelCardId = form.getEntityId();

        boolean entityIdIsNull = Objects.isNull(modelCardId);
        boolean isGenerated = generated.isChecked();

        form.setFormEnabled(entityIdIsNull || !isGenerated);
        grid.setEnabled(!entityIdIsNull && !isGenerated);
    }

    private void fillDefaultsFromParameters(ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        if (form.getEntityId() == null) {
            Entity parameter = parameterService.getParameter();
            fillWithProperty(ModelCardFields.MATERIAL_COSTS_USED,
                    parameter.getStringField(ParameterFieldsPFTD.MATERIAL_COSTS_USED_MC), view);
            fillCheckboxWithProperty(ModelCardFields.USE_NOMINAL_COST_PRICE_NOT_SPECIFIED,
                    parameter.getBooleanField(ParameterFieldsPFTD.USE_NOMINAL_COST_PRICE_NOT_SPECIFIED_MC), view);
        }
    }

    private void fillCheckboxWithProperty(String componentName, boolean propertyValue, ViewDefinitionState view) {
        CheckBoxComponent component = (CheckBoxComponent) view.getComponentByReference(componentName);
        component.setFieldValue(propertyValue);
    }

    private void fillWithProperty(String componentName, String propertyValue, ViewDefinitionState view) {
        FieldComponent component = (FieldComponent) view.getComponentByReference(componentName);
        if (propertyValue != null) {
            component.setFieldValue(propertyValue);
        }
    }

}
