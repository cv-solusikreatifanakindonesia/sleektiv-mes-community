package com.sleektiv.mes.cmmsMachineParts.listeners;

import com.sleektiv.mes.cmmsMachineParts.constants.TimeUsageReportFilterFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.EntityList;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class TimeUsageReportListeners {

    public void printXlsReport(final ViewDefinitionState view, final ComponentState state, final String args[]) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        form.performEvent(view, "save");
        Entity filterEntity = form.getPersistedEntityWithIncludedFormValues();
        if (workersPresent(filterEntity, view) && form.isValid()) {
            view.redirectTo("/cmmsMachineParts/timeUsageReport.xls?filterId=" + filterEntity.getId(), true, false);
        }
    }

    private boolean workersPresent(Entity filterEntity, ViewDefinitionState view) {
        String selection = filterEntity.getStringField(TimeUsageReportFilterFields.WORKERS_SELECTION);
        if ("02selected".equals(selection)) {
            EntityList workers = filterEntity.getHasManyField(TimeUsageReportFilterFields.WORKERS);
            if (workers.isEmpty()) {
                view.addMessage("cmmsMachineParts.timeUsageReport.error.noWorkers", MessageType.FAILURE);
                return false;
            }
            return true;
        }
        return true;
    }

    public void workersSelectionChanged(final ViewDefinitionState state, final ComponentState componentState, final String[] args) {
        GridComponent workersGrid = (GridComponent) state.getComponentByReference("workers");
        String selected = (String) componentState.getFieldValue();
        if ("01all".equals(selected)) {
            workersGrid.setEntities(Collections.emptyList());
            workersGrid.setEnabled(false);
        } else {
            workersGrid.setEnabled(true);
        }
    }
}
