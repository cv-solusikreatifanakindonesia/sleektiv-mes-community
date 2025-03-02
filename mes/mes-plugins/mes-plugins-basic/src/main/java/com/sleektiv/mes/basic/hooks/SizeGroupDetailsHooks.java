package com.sleektiv.mes.basic.hooks;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class SizeGroupDetailsHooks {

    public void fillCriteriaModifiers(final ViewDefinitionState view) {
        FormComponent sizeGroupForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent sizeLookup = (LookupComponent) view.getComponentByReference("sizeLookup");

        Long sizeGroupId = sizeGroupForm.getEntityId();

        if (Objects.nonNull(sizeGroupId)) {
            FilterValueHolder filter = sizeLookup.getFilterValue();

            filter.put("sizeGroupId", sizeGroupId);

            sizeLookup.setFilterValue(filter);
        }

        sizeLookup.requestComponentUpdateState();
    }

}
