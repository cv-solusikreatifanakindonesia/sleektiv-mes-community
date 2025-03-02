package com.sleektiv.mes.cmmsMachineParts.hooks;

import com.sleektiv.mes.cmmsMachineParts.constants.PlannedEventRealizationFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import org.springframework.stereotype.Service;

@Service
public class PlannedEventRealization {

    public void onCreate(final DataDefinition eventRealizationDD, final Entity eventRealization) {
        eventRealization.setField(PlannedEventRealizationFields.CONFIRMED, true);
    }
}
