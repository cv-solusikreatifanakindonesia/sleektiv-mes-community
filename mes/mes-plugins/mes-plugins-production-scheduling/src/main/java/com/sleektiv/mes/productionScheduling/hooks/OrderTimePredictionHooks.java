package com.sleektiv.mes.productionScheduling.hooks;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.productionScheduling.constants.OrderFieldsPS;
import com.sleektiv.mes.productionScheduling.criteriaModifiers.OperCompTimeCalculationsCM;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OrderTimePredictionHooks {

    

    @Autowired
    private ParameterService parameterService;

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        if (form.getEntityId() == null && view.isViewAfterRedirect()) {
            CheckBoxComponent includeTpzField = (CheckBoxComponent) view.getComponentByReference(OrderFieldsPS.INCLUDE_TPZ);
            boolean checkIncludeTpzField = parameterService.getParameter().getBooleanField("includeTpzPS");
            includeTpzField.setChecked(checkIncludeTpzField);
            includeTpzField.requestComponentUpdateState();

            CheckBoxComponent includeAdditionalTimeField = (CheckBoxComponent) view.getComponentByReference(OrderFieldsPS.INCLUDE_ADDITIONAL_TIME);
            boolean checkIncludeAdditionalTimeField = parameterService.getParameter().getBooleanField("includeAdditionalTimePS");
            includeAdditionalTimeField.setChecked(checkIncludeAdditionalTimeField);
            includeAdditionalTimeField.requestComponentUpdateState();
        }
        setCriteriaModifierParameters(view);
    }

    private void setCriteriaModifierParameters(ViewDefinitionState view) {
        LookupComponent techComponent = (LookupComponent) view.getComponentByReference("technology");
        Entity tech = techComponent.getEntity();
        GridComponent grid = (GridComponent) view.getComponentByReference("operCompTimeCalculationsGrid");
        FilterValueHolder holder = grid.getFilterValue();
        if(Objects.nonNull(tech)){
            holder.put(OperCompTimeCalculationsCM.TECHNOLOGY_PARAMETER, tech.getId());
            grid.setFilterValue(holder);
        } else {
            if(holder.has(OperCompTimeCalculationsCM.TECHNOLOGY_PARAMETER)){
                holder.remove(OperCompTimeCalculationsCM.TECHNOLOGY_PARAMETER);
                grid.setFilterValue(holder);
            }
        }


    }
}
