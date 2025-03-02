package com.sleektiv.mes.materialFlowResources.hooks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.materialFlowResources.constants.ParameterFieldsMFR;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;

@Service
public class DocumentPositionsAttributesHooks {

    @Autowired
    private ParameterService parameterService;

    public void initializeDates(final ViewDefinitionState view) {
        FieldComponent dateFrom = (FieldComponent) view.getComponentByReference("dateFrom");
        FieldComponent dateTo = (FieldComponent) view.getComponentByReference("dateTo");
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.MONTH,
                -parameterService.getParameter().getBelongsToField(ParameterFieldsMFR.DOCUMENT_POSITION_PARAMETERS)
                        .getIntegerField("numberOfMonthsForPositionsData"));
        Date dateAgo = calendar.getTime();
        dateFrom.setFieldValue(setDateField(dateAgo));
        dateTo.setFieldValue(setDateField(now));
    }

    private Object setDateField(final Date date) {
        return new SimpleDateFormat(DateUtils.L_DATE_FORMAT, Locale.getDefault()).format(date);
    }
}
