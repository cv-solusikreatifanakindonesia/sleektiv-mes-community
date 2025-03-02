package com.sleektiv.mes.deliveries.hooks;

import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DeliveryByPalletTypeReportHooks {

    public void onBeforeRender(final ViewDefinitionState view) {

        if (view.isViewAfterRedirect()) {
            FieldComponent dateToField = (FieldComponent) view.getComponentByReference("toDate");
            dateToField.setFieldValue(DateUtils.toDateTimeString(new Date()));

            FieldComponent fromDateField = (FieldComponent) view.getComponentByReference("fromDate");
            fromDateField.setFieldValue(DateUtils.toDateTimeString(new DateTime().withDayOfMonth(1).toDate()));

            dateToField.requestComponentUpdateState();
            fromDateField.requestComponentUpdateState();
        }

    }
}
