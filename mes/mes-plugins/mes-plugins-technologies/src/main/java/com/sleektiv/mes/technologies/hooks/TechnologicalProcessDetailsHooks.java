package com.sleektiv.mes.technologies.hooks;

import com.sleektiv.mes.basic.constants.WorkstationFields;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.sleektiv.mes.technologies.constants.TechnologicalProcessFields.*;

@Service
public class TechnologicalProcessDetailsHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void onBeforeRender(final ViewDefinitionState view) {
        CheckBoxComponent extendedTimeForSizeGroupField = (CheckBoxComponent) view
                .getComponentByReference(EXTENDED_TIME_FOR_SIZE_GROUP);
        FieldComponent increasePercentField = (FieldComponent) view.getComponentByReference(INCREASE_PERCENT);
        LookupComponent sizeGroupLookup = (LookupComponent) view.getComponentByReference(SIZE_GROUP);

        if (extendedTimeForSizeGroupField.isChecked()) {
            increasePercentField.setEnabled(true);
            increasePercentField.setRequired(true);
            sizeGroupLookup.setEnabled(true);
            sizeGroupLookup.setRequired(true);
        } else {
            increasePercentField.setEnabled(false);
            increasePercentField.setFieldValue(null);
            increasePercentField.setRequired(false);
            sizeGroupLookup.setEnabled(false);
            sizeGroupLookup.setFieldValue(null);
            sizeGroupLookup.setRequired(false);
        }
        increasePercentField.requestComponentUpdateState();
        sizeGroupLookup.requestComponentUpdateState();

        LookupComponent workstationLookup = (LookupComponent) view.getComponentByReference(WORKSTATION);

        LookupComponent workstationTypeLookup = (LookupComponent) view.getComponentByReference(WORKSTATION_TYPE);

        FilterValueHolder filterValueHolder = workstationLookup.getFilterValue();

        if (workstationTypeLookup.getFieldValue() != null) {
            filterValueHolder.put(WorkstationFields.WORKSTATION_TYPE, workstationTypeLookup.getEntity().getId());
        } else {
            filterValueHolder.remove(WorkstationFields.WORKSTATION_TYPE);
        }

        workstationLookup.setFilterValue(filterValueHolder);

        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Long technologicalProcessId = form.getEntityId();
        if (Objects.nonNull(technologicalProcessId)) {
            Entity workstationType = workstationTypeLookup.getEntity();
            Entity workstationTypeFromDB = dataDefinitionService
                    .get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_TECHNOLOGICAL_PROCESS)
                    .get(technologicalProcessId).getBelongsToField(WORKSTATION_TYPE);
            if (workstationType == null && workstationTypeFromDB != null
                    || workstationType != null && workstationTypeFromDB == null
                    || workstationType != null && !workstationType.getId().equals(workstationTypeFromDB.getId())) {
                workstationLookup.setFieldValue(null);
                workstationLookup.requestComponentUpdateState();
            }
        }
    }
}
