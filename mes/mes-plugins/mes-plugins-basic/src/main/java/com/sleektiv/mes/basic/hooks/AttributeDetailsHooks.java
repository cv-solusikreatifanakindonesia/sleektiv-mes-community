package com.sleektiv.mes.basic.hooks;

import com.sleektiv.mes.basic.constants.AttributeDataType;
import com.sleektiv.mes.basic.constants.AttributeFields;
import com.sleektiv.mes.basic.constants.AttributeValueType;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.utils.NumberGeneratorService;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AttributeDetailsHooks {

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    public void onBeforeRender(final ViewDefinitionState view) {
        numberGeneratorService.generateAndInsertNumber(view, BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_ATTRIBUTE, SleektivViewConstants.L_FORM,
                AttributeFields.NUMBER);

        FieldComponent dataType = (FieldComponent) view.getComponentByReference(AttributeFields.DATA_TYPE);
        FieldComponent valueType = (FieldComponent) view.getComponentByReference(AttributeFields.VALUE_TYPE);
        FieldComponent precision = (FieldComponent) view.getComponentByReference(AttributeFields.PRECISION);
        FieldComponent unit = (FieldComponent) view.getComponentByReference(AttributeFields.UNIT);
        if (Objects.nonNull(valueType.getFieldValue())
                && AttributeValueType.NUMERIC.getStringValue().equals(valueType.getFieldValue())) {
            precision.setEnabled(true);
            unit.setEnabled(true);
            if (Objects.isNull(precision.getFieldValue()) || StringUtils.isEmpty((String) precision.getFieldValue())) {
                precision.setFieldValue("0");
                precision.requestComponentUpdateState();
            }
        } else {
            unit.setEnabled(false);
            unit.setFieldValue(null);
            precision.setEnabled(false);
            precision.setFieldValue(null);
            precision.requestComponentUpdateState();
        }
        unit.requestComponentUpdateState();

        GridComponent attributeValues = (GridComponent) view.getComponentByReference(AttributeFields.ATTRIBUTE_VALUES);
        if (Objects.nonNull(dataType.getFieldValue())
                && AttributeDataType.CONTINUOUS.getStringValue().equals(dataType.getFieldValue())) {
            attributeValues.setEditable(false);
            attributeValues.setEnabled(false);
        } else {
            attributeValues.setEditable(true);
            attributeValues.setEnabled(true);
        }

        disableFormComponentsIfAttributeAssign(view);
    }

    private void disableFormComponentsIfAttributeAssign(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent dataType = (FieldComponent) view.getComponentByReference(AttributeFields.DATA_TYPE);
        FieldComponent valueType = (FieldComponent) view.getComponentByReference(AttributeFields.VALUE_TYPE);
        FieldComponent precision = (FieldComponent) view.getComponentByReference(AttributeFields.PRECISION);
        FieldComponent unit = (FieldComponent) view.getComponentByReference(AttributeFields.UNIT);
        CheckBoxComponent forProduct = (CheckBoxComponent) view.getComponentByReference(AttributeFields.FOR_PRODUCT);
        CheckBoxComponent forResource = (CheckBoxComponent) view.getComponentByReference(AttributeFields.FOR_RESOURCE);
        CheckBoxComponent forQualityControl = (CheckBoxComponent) view
                .getComponentByReference(AttributeFields.FOR_QUALITY_CONTROL);
        dataType.setEnabled(true);
        valueType.setEnabled(true);
        precision.setEnabled(true);
        unit.setEnabled(true);
        forProduct.setEnabled(true);
        forResource.setEnabled(true);
        forQualityControl.setEnabled(true);
        if (Objects.nonNull(form.getEntityId())) {
            Entity attribute = form.getEntity().getDataDefinition().get(form.getEntity().getId());
            if (!attribute.getHasManyField(AttributeFields.PRODUCT_ATTRIBUTE_VALUES).isEmpty()) {
                dataType.setEnabled(false);
                valueType.setEnabled(false);
                precision.setEnabled(false);
                unit.setEnabled(false);
                forProduct.setEnabled(false);
            }

            if (!attribute.getHasManyField(AttributeFields.RESOURCE_ATTRIBUTE_VALUES).isEmpty()) {
                dataType.setEnabled(false);
                valueType.setEnabled(false);
                precision.setEnabled(false);
                unit.setEnabled(false);
                forResource.setEnabled(false);
            }

            if (!attribute.getHasManyField(AttributeFields.QUALITY_CONTROL_ATTRIBUTE).isEmpty()) {
                dataType.setEnabled(false);
                valueType.setEnabled(false);
                precision.setEnabled(false);
                unit.setEnabled(false);
                forQualityControl.setEnabled(false);
            }

            if (!attribute.getHasManyField(AttributeFields.ATTRIBUTE_VALUES).isEmpty()) {
                dataType.setEnabled(false);
                valueType.setEnabled(false);
                precision.setEnabled(false);
            }
        }
    }
}
