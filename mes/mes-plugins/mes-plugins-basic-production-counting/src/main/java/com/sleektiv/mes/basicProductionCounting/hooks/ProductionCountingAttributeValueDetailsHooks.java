package com.sleektiv.mes.basicProductionCounting.hooks;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.AttributeDataType;
import com.sleektiv.mes.basic.constants.AttributeFields;
import com.sleektiv.mes.basicProductionCounting.constants.ProductionCountingAttributeValueFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class ProductionCountingAttributeValueDetailsHooks {

    private static final String L_ATTRIBUTE_ID = "attributeId";

    private static final String L_UNIT = "unit";

    public void onBeforeRender(final ViewDefinitionState view) {
        setValueBold(view);
        setFilters(view);
        LookupComponent attributeLookup = (LookupComponent) view
                .getComponentByReference(ProductionCountingAttributeValueFields.ATTRIBUTE);
        LookupComponent attributeValueLookup = (LookupComponent) view
                .getComponentByReference(ProductionCountingAttributeValueFields.ATTRIBUTE_VALUE);
        FieldComponent valueField = (FieldComponent) view
                .getComponentByReference(ProductionCountingAttributeValueFields.VALUE);

        if (Objects.nonNull(attributeLookup.getEntity())) {
            Entity attribute = attributeLookup.getEntity();
            FieldComponent unitField = (FieldComponent) view.getComponentByReference(L_UNIT);
            unitField.setFieldValue(attribute.getStringField(AttributeFields.UNIT));
            unitField.requestComponentUpdateState();
            if (AttributeDataType.CONTINUOUS.getStringValue().equals(attribute.getStringField(AttributeFields.DATA_TYPE))) {
                valueField.setVisible(true);
                attributeValueLookup.setVisible(false);
            } else {
                valueField.setVisible(false);
                attributeValueLookup.setVisible(true);
            }
        } else {
            valueField.setVisible(false);
            attributeValueLookup.setVisible(false);
        }

    }

    private void setValueBold(ViewDefinitionState view) {
        LookupComponent attributeValueLookup = (LookupComponent) view
                .getComponentByReference(ProductionCountingAttributeValueFields.ATTRIBUTE_VALUE);
        attributeValueLookup.setRequired(true);
        attributeValueLookup.requestComponentUpdateState();
    }

    private void setFilters(ViewDefinitionState view) {
        LookupComponent attributeLookup = (LookupComponent) view
                .getComponentByReference(ProductionCountingAttributeValueFields.ATTRIBUTE);
        LookupComponent attributeValueLookup = (LookupComponent) view
                .getComponentByReference(ProductionCountingAttributeValueFields.ATTRIBUTE_VALUE);
        FilterValueHolder attributeValueLookupFilters = attributeValueLookup.getFilterValue();
        if (Objects.nonNull(attributeLookup.getEntity())) {
            attributeValueLookupFilters.put(L_ATTRIBUTE_ID, attributeLookup.getEntity().getId());
        } else if (attributeValueLookupFilters.has(L_ATTRIBUTE_ID)) {
            attributeValueLookupFilters.remove(L_ATTRIBUTE_ID);
        }
        attributeValueLookup.setFilterValue(attributeValueLookupFilters);
    }
}
