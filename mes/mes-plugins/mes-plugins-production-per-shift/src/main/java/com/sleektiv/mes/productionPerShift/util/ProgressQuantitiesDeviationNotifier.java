/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
 * Version: 1.4
 *
 * This file is part of Sleektiv.
 *
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.productionPerShift.util;

import com.google.common.base.Optional;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.productionPerShift.constants.ProductionPerShiftFields;
import com.sleektiv.mes.productionPerShift.dataProvider.ProductionPerShiftDataProvider;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProgressQuantitiesDeviationNotifier {

    @Autowired
    private NumberService numberService;

    @Autowired
    private ProductionPerShiftDataProvider productionPerShiftDataProvider;

    public void compareAndNotify(final ViewDefinitionState view, final Entity pps, boolean shouldBeCorrected) {
        Optional<BigDecimal> maybeQuantitiesDifference = calculateQuantitiesDifference(pps, shouldBeCorrected);
        for (BigDecimal quantitiesDifference : maybeQuantitiesDifference.asSet()) {
            int compareResult = quantitiesDifference.compareTo(BigDecimal.ZERO);
            if (compareResult > 0) {
                showQuantitiesDeviationNotice(view, quantitiesDifference,
                        "productionPerShift.productionPerShiftDetails.sumPlanedQuantityPSSmaller");
            } else if (compareResult < 0) {
                showQuantitiesDeviationNotice(view, quantitiesDifference,
                        "productionPerShift.productionPerShiftDetails.sumPlanedQuantityPSGreater");
            }
        }
    }

    private Optional<BigDecimal> calculateQuantitiesDifference(final Entity pps, boolean shouldBeCorrected) {

        Entity order = pps.getBelongsToField(ProductionPerShiftFields.ORDER);

        BigDecimal sumOfDailyPlannedQuantities = productionPerShiftDataProvider.countSumOfQuantities(pps, shouldBeCorrected);
        BigDecimal planedQuantityFromOrder = order.getDecimalField(OrderFields.PLANNED_QUANTITY);
        return Optional.of(planedQuantityFromOrder.subtract(sumOfDailyPlannedQuantities, numberService.getMathContext()));
    }

    private void showQuantitiesDeviationNotice(final ViewDefinitionState view, final BigDecimal quantitiesDifference,
            final String messageKey) {
        for (ComponentState productionPerShiftForm : view.tryFindComponentByReference(SleektivViewConstants.L_FORM).asSet()) {
            productionPerShiftForm.addMessage(messageKey, ComponentState.MessageType.INFO, false,
                    numberService.formatWithMinimumFractionDigits(quantitiesDifference.abs(numberService.getMathContext()), 0));
        }
    }

}
