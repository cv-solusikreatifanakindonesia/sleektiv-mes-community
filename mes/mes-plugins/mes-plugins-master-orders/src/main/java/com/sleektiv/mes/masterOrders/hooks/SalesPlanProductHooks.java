package com.sleektiv.mes.masterOrders.hooks;

import com.sleektiv.mes.masterOrders.constants.SalesPlanProductFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SalesPlanProductHooks {

    public void onCopy(final DataDefinition salesPlanProductDD, final Entity salesPlanProduct) {
        salesPlanProduct.setField(SalesPlanProductFields.ORDERED_QUANTITY, BigDecimal.ZERO);
        salesPlanProduct.setField(SalesPlanProductFields.ORDERED_TO_WAREHOUSE, BigDecimal.ZERO);
        salesPlanProduct.setField(SalesPlanProductFields.SURPLUS_FROM_PLAN,
                salesPlanProduct.getDecimalField(SalesPlanProductFields.PLANNED_QUANTITY));
    }

}
