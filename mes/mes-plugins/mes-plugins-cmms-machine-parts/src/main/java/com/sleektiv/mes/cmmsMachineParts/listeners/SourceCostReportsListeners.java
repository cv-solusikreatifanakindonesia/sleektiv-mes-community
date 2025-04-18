package com.sleektiv.mes.cmmsMachineParts.listeners;

import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.cmmsMachineParts.constants.SourceCostReportFilterFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class SourceCostReportsListeners {

    private static final SimpleDateFormat df = new SimpleDateFormat(DateUtils.L_DATE_FORMAT);

    public void generateReport(final ViewDefinitionState view, final ComponentState state, final String args[]) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        form.performEvent(view, "save");

        if (args != null && args.length > 0) {
            Entity filter = form.getPersistedEntityWithIncludedFormValues();

            if (filter.isValid()) {
                Entity sourceCost = filter.getBelongsToField(SourceCostReportFilterFields.SOURCE_COST);
                Date dateFrom = filter.getDateField(SourceCostReportFilterFields.FROM_DATE);
                Date dateTo = filter.getDateField(SourceCostReportFilterFields.TO_DATE);
                if (dateFrom.after(dateTo)) {
                    view.addMessage("cmmsMachineParts.workerCostsReport.error.wrongDateOrder", MessageType.FAILURE);
                    return;
                }
                String reportType = args[0];

                switch (reportType) {
                    case "workerCosts":
                        generateWorkerCostsReport(view, sourceCost, dateFrom, dateTo);
                        break;
                    case "timeReport":
                        generateTimeReport(sourceCost, dateFrom, dateTo);
                        break;

                    case "partsCosts":
                        generatePartsCosts(sourceCost, dateFrom, dateTo);
                        break;

                    default: {
                        throw new IllegalArgumentException(String.format("Unknow event to generate report: '%s'", reportType));
                    }
                }
            }
        }
    }

    private void generatePartsCosts(Entity sourceCost, Date dateFrom, Date dateTo) {
    }

    private void generateTimeReport(Entity sourceCost, Date dateFrom, Date dateTo) {
    }

    private void generateWorkerCostsReport(ViewDefinitionState view, Entity sourceCost, Date dateFrom, Date dateTo) {
        String url = "/cmmsMachineParts/workerCosts.xls?sourceCost=" + (sourceCost == null ? "" : sourceCost.getId()) + "&dateFrom=" + df.format(dateFrom) + "&dateTo=" + df.format(dateTo);
        view.redirectTo(url, true, false);
    }
}
