/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
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
package com.sleektiv.mes.productFlowThruDivision.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.productFlowThruDivision.constants.ParameterFieldsPFTD;
import com.sleektiv.mes.productFlowThruDivision.constants.ProductFlowThruDivisionConstants;
import com.sleektiv.mes.productFlowThruDivision.listeners.SupplyParametersListenersPFTD;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.constans.WarehouseIssueFields;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants.WarehouseIssueState;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.model.api.search.SearchResult;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;

@Service
public class SupplyParametersHooksPFTD {

    @Autowired
    SupplyParametersListenersPFTD supplyParametersListenersPFTD;

    @Autowired
    DataDefinitionService dataDefinitionService;

    public void onBeforeRender(final ViewDefinitionState view) {
        ComponentState warehouseIssueProductsSource = view
                .getComponentByReference(ParameterFieldsPFTD.WAREHOUSE_ISSUE_PRODUCTS_SOURCE);
        supplyParametersListenersPFTD.toggleGenerateIssuesTo(view, warehouseIssueProductsSource, null);
        toggleStateReservation(view);
    }

    private void toggleStateReservation(ViewDefinitionState view) {
        CheckBoxComponent warehouseIssuesReserveStatesCheckbox = (CheckBoxComponent) view
                .getComponentByReference(ParameterFieldsPFTD.WAREHOUSE_ISSUES_RESERVE_STATES);
        boolean shouldBeEnabled = draftOrInProgressWarehouseIssuesDoesntExist();
        warehouseIssuesReserveStatesCheckbox.setEnabled(shouldBeEnabled);
        warehouseIssuesReserveStatesCheckbox.requestComponentUpdateState();
    }

    private boolean draftOrInProgressWarehouseIssuesDoesntExist() {
        DataDefinition dd = dataDefinitionService.get(ProductFlowThruDivisionConstants.PLUGIN_IDENTIFIER,
                ProductFlowThruDivisionConstants.MODEL_WAREHOUSE_ISSUE);
        SearchResult result = dd.find().add(SearchRestrictions.or(
                        SearchRestrictions.eq(WarehouseIssueFields.STATE, WarehouseIssueState.DRAFT.getStringValue()),
                        SearchRestrictions.eq(WarehouseIssueFields.STATE, WarehouseIssueState.IN_PROGRESS.getStringValue()))).list();
        return result.getTotalNumberOfEntities() == 0;
    }


}
