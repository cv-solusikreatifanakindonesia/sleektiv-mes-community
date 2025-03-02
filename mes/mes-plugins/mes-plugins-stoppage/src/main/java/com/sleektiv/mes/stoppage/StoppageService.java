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
package com.sleektiv.mes.stoppage;

import com.google.common.collect.Maps;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StoppageService {

    private static final String L_GRID_OPTIONS = "grid.options";

    private static final String L_FILTERS = "filters";



    public void showStoppage(final ViewDefinitionState viewDefinitionState, final ComponentState triggerState, final String[] args) {
        FormComponent form = (FormComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity order = form.getEntity();
        Map<String, String> filters = Maps.newHashMap();
        filters.put("orderNumber", applyInOperator(order.getStringField(OrderFields.NUMBER)));

        Map<String, Object> gridOptions = Maps.newHashMap();
        gridOptions.put(L_FILTERS, filters);

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(L_GRID_OPTIONS, gridOptions);
        parameters.put("window.showBack", true);
        parameters.put("grid.forOrder", order.getId());

        String url = "../page/stoppage/stoppagesForOrderList.html";

        viewDefinitionState.openModal(url, parameters);

    }

    private String applyInOperator(final String value) {
        StringBuilder builder = new StringBuilder();
        return builder.append("[").append(value).append("]").toString();
    }

}
