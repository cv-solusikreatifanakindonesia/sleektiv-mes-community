package com.sleektiv.plugins.users.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.plugins.users.constants.GroupDetailsConstants;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class GroupDetailsHooks {

    public void fillCriteriaModifiers(final ViewDefinitionState viewDefinitionState) {
        LookupComponent roles = (LookupComponent) viewDefinitionState.getComponentByReference("roleLookup");
        FormComponent form = (FormComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_FORM);
        if (form.getEntityId() != null) {
                FilterValueHolder filter = roles.getFilterValue();
                filter.put(GroupDetailsConstants.GROUP_ID, form.getEntityId());
                roles.setFilterValue(filter);
            }
        roles.requestComponentUpdateState();
    }
}
