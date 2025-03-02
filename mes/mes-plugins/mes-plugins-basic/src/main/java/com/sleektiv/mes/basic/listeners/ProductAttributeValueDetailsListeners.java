package com.sleektiv.mes.basic.listeners;

import com.sleektiv.mes.basic.constants.AttributeValueFields;
import com.sleektiv.mes.basic.constants.ProductAttributeValueFields;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.LookupComponent;

import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
public class ProductAttributeValueDetailsListeners {

    public void onChangeAttribute(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        LookupComponent attributeValueLookup = (LookupComponent) view.getComponentByReference(ProductAttributeValueFields.ATTRIBUTE_VALUE);
        FieldComponent valueField = (FieldComponent) view.getComponentByReference(ProductAttributeValueFields.VALUE);
        attributeValueLookup.setFieldValue(null);
        attributeValueLookup.requestComponentUpdateState();
        valueField.setFieldValue(null);
        valueField.requestComponentUpdateState();
    }

    public void onChangeAttributeValue(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        LookupComponent attributeValueLookup = (LookupComponent) view.getComponentByReference(ProductAttributeValueFields.ATTRIBUTE_VALUE);
        FieldComponent valueField = (FieldComponent) view.getComponentByReference(ProductAttributeValueFields.VALUE);
        if(Objects.nonNull(attributeValueLookup.getEntity())) {
            valueField.setFieldValue(attributeValueLookup.getEntity().getStringField(AttributeValueFields.VALUE));
            valueField.requestComponentUpdateState();
        }
    }
}
