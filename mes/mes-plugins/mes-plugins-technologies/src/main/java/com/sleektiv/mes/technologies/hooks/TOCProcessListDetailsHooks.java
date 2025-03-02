package com.sleektiv.mes.technologies.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.technologies.constants.TechnologicalProcessListFields;
import com.sleektiv.mes.technologies.constants.TechnologyOperationComponentFields;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class TOCProcessListDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        Long operationId = ((FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM))
                .getPersistedEntityWithIncludedFormValues().getBelongsToField(TechnologyOperationComponentFields.OPERATION)
                .getId();
        LookupComponent technologicalProcessList = (LookupComponent) view
                .getComponentByReference(TechnologyOperationComponentFields.TECHNOLOGICAL_PROCESS_LIST);
        FilterValueHolder filterValueHolder = technologicalProcessList.getFilterValue();

        filterValueHolder.put(TechnologicalProcessListFields.OPERATION, operationId);

        technologicalProcessList.setFilterValue(filterValueHolder);
    }
}
