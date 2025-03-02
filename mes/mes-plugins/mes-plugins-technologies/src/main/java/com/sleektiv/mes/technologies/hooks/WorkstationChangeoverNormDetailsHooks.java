package com.sleektiv.mes.technologies.hooks;

import com.sleektiv.mes.basic.criteriaModifiers.AttributeValueCriteriaModifiers;
import com.sleektiv.mes.technologies.constants.WorkstationChangeoverNormChangeoverType;
import com.sleektiv.mes.technologies.constants.WorkstationChangeoverNormFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class WorkstationChangeoverNormDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        setFieldsRequired(view);
        setFieldsState(view);
    }

    private void setFieldsRequired(final ViewDefinitionState view) {
        FieldComponent nameField = (FieldComponent) view.getComponentByReference(WorkstationChangeoverNormFields.NAME);

        nameField.setRequired(true);
    }

    private void setFieldsState(final ViewDefinitionState view) {
        FormComponent workstationChangeoverNormForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent workstationTypeLookup = (LookupComponent) view.getComponentByReference(WorkstationChangeoverNormFields.WORKSTATION_TYPE);
        LookupComponent workstationLookup = (LookupComponent) view.getComponentByReference(WorkstationChangeoverNormFields.WORKSTATION);
        LookupComponent attributeLookup = (LookupComponent) view.getComponentByReference(WorkstationChangeoverNormFields.ATTRIBUTE);
        FieldComponent changeoverTypeField = (FieldComponent) view.getComponentByReference(WorkstationChangeoverNormFields.CHANGEOVER_TYPE);
        LookupComponent fromAttributeValueLookup = (LookupComponent) view.getComponentByReference(WorkstationChangeoverNormFields.FROM_ATTRIBUTE_VALUE);
        LookupComponent toAttributeValueLookup = (LookupComponent) view.getComponentByReference(WorkstationChangeoverNormFields.TO_ATTRIBUTE_VALUE);

        Entity workstationChangeoverNorm = workstationChangeoverNormForm.getEntity();

        Entity workstationType = workstationTypeLookup.getEntity();
        Entity workstation = workstationLookup.getEntity();
        Entity attribute = attributeLookup.getEntity();
        String changeoverType = (String) changeoverTypeField.getFieldValue();

        boolean isEnabled = Objects.isNull(workstationChangeoverNorm.getId());

        setWorkstationOrWorkstationTypeLookup(workstationTypeLookup, workstation, isEnabled);
        setWorkstationOrWorkstationTypeLookup(workstationLookup, workstationType, isEnabled);

        attributeLookup.setEnabled(isEnabled);
        attributeLookup.requestComponentUpdateState();
        changeoverTypeField.setEnabled(isEnabled);
        changeoverTypeField.requestComponentUpdateState();

        setAttributeValueLookup(fromAttributeValueLookup, attribute, changeoverType, isEnabled);
        setAttributeValueLookup(toAttributeValueLookup, attribute, changeoverType, isEnabled);
    }

    private void setWorkstationOrWorkstationTypeLookup(final LookupComponent lookupComponent, final Entity entity, final boolean isEnabled) {
        lookupComponent.setEnabled(isEnabled && Objects.isNull(entity));
        lookupComponent.requestComponentUpdateState();
    }

    private void setAttributeValueLookup(final LookupComponent lookupComponent, final Entity attribute, final String changeoverType,
                                         final boolean isEnabled) {
        FilterValueHolder filterValueHolder = lookupComponent.getFilterValue();

        boolean isAttribute = true;

        if (WorkstationChangeoverNormChangeoverType.BETWEEN_VALUES.getStringValue().equals(changeoverType)) {
            if (Objects.nonNull(attribute)) {
                filterValueHolder.put(AttributeValueCriteriaModifiers.L_ATTRIBUTE_ID, attribute.getId());
            } else {
                filterValueHolder.remove(AttributeValueCriteriaModifiers.L_ATTRIBUTE_ID);

                lookupComponent.setFieldValue(null);
            }
        } else {
            filterValueHolder.remove(AttributeValueCriteriaModifiers.L_ATTRIBUTE_ID);

            lookupComponent.setFieldValue(null);

            isAttribute = false;
        }

        lookupComponent.setEnabled(isEnabled && isAttribute);
        lookupComponent.setFilterValue(filterValueHolder);
        lookupComponent.requestComponentUpdateState();
    }

}
