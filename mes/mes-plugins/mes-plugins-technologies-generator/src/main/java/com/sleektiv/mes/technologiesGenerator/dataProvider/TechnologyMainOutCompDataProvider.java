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
package com.sleektiv.mes.technologiesGenerator.dataProvider;

import static com.sleektiv.model.api.search.SearchOrders.asc;
import static com.sleektiv.model.api.search.SearchRestrictions.eq;
import static com.sleektiv.model.api.search.SearchRestrictions.eqField;
import static com.sleektiv.model.api.search.SearchRestrictions.isNull;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.technologies.constants.OperationProductOutComponentFields;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.constants.TechnologyOperationComponentFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;

@Service
public class TechnologyMainOutCompDataProvider {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public Optional<Entity> find(final Long technologyId) {
        SearchCriteriaBuilder scb = getOpocDD().findWithAlias("opoc");
        scb.createAlias(OperationProductOutComponentFields.OPERATION_COMPONENT, "toc", JoinType.INNER);
        scb.createAlias("toc." + TechnologyOperationComponentFields.TECHNOLOGY, "tech", JoinType.INNER);
        scb.add(eqField("tech.product", "opoc.product"));
        scb.add(isNull("toc.parent"));
        scb.add(eq("tech.id", technologyId));
        scb.addOrder(asc("id"));
        return Optional.ofNullable(scb.setMaxResults(1).uniqueResult());
    }

    private DataDefinition getOpocDD() {
        return dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER,
                TechnologiesConstants.MODEL_OPERATION_PRODUCT_OUT_COMPONENT);
    }

}
