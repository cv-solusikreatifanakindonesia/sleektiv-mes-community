package com.sleektiv.mes.masterOrders.hooks;

import com.sleektiv.mes.masterOrders.constants.OrdersGenerationHelperFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class OrdersGenerationHelperHooks {

    public boolean validateDates(final DataDefinition ordersGenerationHelperDD, final Entity ordersGenerationHelper) {
        Date dateFrom = ordersGenerationHelper.getDateField(OrdersGenerationHelperFields.DATE_FROM);
        Date dateTo = ordersGenerationHelper.getDateField(OrdersGenerationHelperFields.DATE_TO);

        if (Objects.isNull(dateFrom) || Objects.isNull(dateTo) || dateTo.after(dateFrom)) {
            return true;
        }

        ordersGenerationHelper.addError(ordersGenerationHelperDD.getField(OrdersGenerationHelperFields.DATE_TO), "orders.validate.global.error.datesOrder");

        return false;
    }

}
