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
package com.sleektiv.mes.masterOrders.util;

import com.sleektiv.mes.masterOrders.constants.MasterOrderProductFields;
import com.sleektiv.mes.masterOrders.constants.MasterOrdersConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.EntityOpResult;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.utils.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.sleektiv.model.api.search.SearchProjections.alias;
import static com.sleektiv.model.api.search.SearchProjections.id;
import static com.sleektiv.model.api.search.SearchRestrictions.belongsTo;

@Service
public class MasterOrderProductsDataService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public EntityOpResult deleteExistingMasterOrderProducts(final Entity masterOrder) {
        if (masterOrder == null || masterOrder.getId() == null) {
            return EntityOpResult.successfull();
        }
        Collection<Long> belongingProductIds = findBelongingProductIds(masterOrder);
        if (belongingProductIds.isEmpty()) {
            return EntityOpResult.successfull();
        }
        return getMasterOrderProductDD().delete(belongingProductIds.toArray(new Long[] {}));
    }

    private Collection<Long> findBelongingProductIds(final Entity masterOrder) {
        SearchCriteriaBuilder scb = getMasterOrderProductDD().find();
        scb.add(belongsTo(MasterOrderProductFields.MASTER_ORDER, masterOrder));
        scb.setProjection(alias(id(), "id"));
        return EntityUtils.getFieldsView(scb.list().getEntities(), "id");
    }

    private DataDefinition getMasterOrderProductDD() {
        return dataDefinitionService.get(MasterOrdersConstants.PLUGIN_IDENTIFIER,
                MasterOrdersConstants.MODEL_MASTER_ORDER_PRODUCT);
    }

}
