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
package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.hooks;

import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.productFlowThruDivision.constants.ProductFlowThruDivisionConstants;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.WarehouseIssueGenerator;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.WarehouseIssueParameterService;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.constans.WarehouseIssueFields;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants.WarehouseIssueState;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants.WarehouseIssueStateChangeDescriber;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants.WarehouseIssueStringValues;
import com.sleektiv.mes.states.service.StateChangeEntityBuilder;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class WarehouseIssueHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private StateChangeEntityBuilder stateChangeEntityBuilder;

    @Autowired
    private WarehouseIssueStateChangeDescriber describer;

    @Autowired
    private WarehouseIssueParameterService warehouseIssueParameterService;

    @Autowired
    private WarehouseIssueGenerator warehouseIssueGenerator;

    public void onCreate(final DataDefinition warehouseIssueDD, final Entity warehouseIssue) {
        setInitialState(warehouseIssue);
    }

    void setInitialState(final Entity warehouseIssue) {
        stateChangeEntityBuilder.buildInitial(describer, warehouseIssue, WarehouseIssueState.DRAFT);
    }

    public void onSave(final DataDefinition warehouseIssueDD, final Entity warehouseIssue) {
        if (Objects.isNull(warehouseIssue.getId())) {
            warehouseIssue.setField(WarehouseIssueFields.DATE_OF_CREATION, new Date());
        }
        if (Objects.isNull(warehouseIssue.getField(WarehouseIssueFields.NUMBER))) {
            warehouseIssue.setField(WarehouseIssueFields.NUMBER, warehouseIssueGenerator.setNumberFromSequence());
        }
    }

    public boolean onDelete(final DataDefinition warehouseIssueDD, final Entity warehouseIssue) {
        if (WarehouseIssueStringValues.IN_PROGRESS.equals(warehouseIssue.getStringField(WarehouseIssueFields.STATE))) {
            warehouseIssue.addGlobalError("productFlowThruDivision.warehouseIssue.error.issueInProgress");
            return false;
        }
        return true;
    }

    public void onCopy(final DataDefinition warehouseIssueDD, final Entity warehouseIssue) {
        setInitialState(warehouseIssue);
    }

    private DataDefinition getOrderDD() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER);
    }

    public DataDefinition getWarehouseIssueDD() {
        return dataDefinitionService.get(ProductFlowThruDivisionConstants.PLUGIN_IDENTIFIER,
                ProductFlowThruDivisionConstants.MODEL_WAREHOUSE_ISSUE);
    }

}
