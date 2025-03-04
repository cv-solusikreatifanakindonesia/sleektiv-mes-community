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
package com.sleektiv.mes.orders.constants.deviationReasonTypes;

import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.constants.ReasonTypeCorrectionDateFromFields;
import com.sleektiv.mes.orders.constants.ReasonTypeCorrectionDateToFields;
import com.sleektiv.mes.orders.constants.ReasonTypeDeviationEffectiveEndFields;
import com.sleektiv.mes.orders.constants.ReasonTypeDeviationEffectiveStartFields;
import com.sleektiv.mes.orders.constants.TypeOfCorrectionCausesFields;

public final class OrderDeviationModelDescribers {

    private OrderDeviationModelDescribers(){}
    
    public static final DeviationModelDescriber START_DATE_DEVIATION = new DeviationModelDescriber(
            OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_REASON_TYPE_CORRECTION_DATE_FROM,
            ReasonTypeCorrectionDateFromFields.REASON_TYPE_OF_CHANGING_ORDER_STATE);

    public static final DeviationModelDescriber FINISH_DATE_DEVIATION = new DeviationModelDescriber(
            OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_REASON_TYPE_CORRECTION_DATE_TO,
            ReasonTypeCorrectionDateToFields.REASON_TYPE_OF_CHANGING_ORDER_STATE);

    public static final DeviationModelDescriber EFFECTIVE_START_DATE_DEVIATION = new DeviationModelDescriber(
            OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_REASON_TYPE_DEVIATION_EFFECTIVE_START,
            ReasonTypeDeviationEffectiveStartFields.REASON_TYPE_OF_CHANGING_ORDER_STATE);

    public static final DeviationModelDescriber EFFECTIVE_FINISH_DATE_DEVIATION = new DeviationModelDescriber(
            OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_REASON_TYPE_DEVIATION_EFFECTIVE_END,
            ReasonTypeDeviationEffectiveEndFields.REASON_TYPE_OF_CHANGING_ORDER_STATE);

    public static final DeviationModelDescriber QUANTITY_DEVIATION = new DeviationModelDescriber(
            OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_TYPE_OF_CORRECTION_CAUSES,
            TypeOfCorrectionCausesFields.REASON_TYPE);

}
