package com.sleektiv.mes.cmmsMachineParts.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.cmmsMachineParts.constants.TimeUsageReportFilterFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class TimeUsageReportFilterHooks {

    public void onSave(final DataDefinition timeUsageFilterDD, final Entity timeUsageFilter) {
        String selected = timeUsageFilter.getStringField(TimeUsageReportFilterFields.WORKERS_SELECTION);
        if ("01all".equals(selected)) {
            timeUsageFilter.setField(TimeUsageReportFilterFields.WORKERS, null);
        }
    }
}
