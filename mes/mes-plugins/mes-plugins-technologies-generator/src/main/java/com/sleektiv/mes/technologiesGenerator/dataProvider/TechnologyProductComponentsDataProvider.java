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

import static com.sleektiv.model.api.search.SearchRestrictions.eq;
import static com.sleektiv.model.api.search.SearchRestrictions.idEq;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ProductFamilyElementType;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.technologies.constants.OperationProductInComponentFields;
import com.sleektiv.mes.technologies.constants.OperationProductOutComponentFields;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.constants.TechnologyOperationComponentFields;
import com.sleektiv.mes.technologies.domain.TechnologyId;
import com.sleektiv.mes.technologiesGenerator.constants.ProductFieldsTG;
import com.sleektiv.mes.technologiesGenerator.domain.OutputProductComponentId;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class TechnologyProductComponentsDataProvider {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public List<Entity> findInputs(final TechnologyId technologyId, boolean generationMode) {
        SearchCriteriaBuilder scb = null;
        if (generationMode) {
            scb = createGenerationModeBaseCriteria(TechnologiesConstants.MODEL_OPERATION_PRODUCT_IN_COMPONENT,
                    OperationProductInComponentFields.OPERATION_COMPONENT, OperationProductInComponentFields.PRODUCT,
                    technologyId);
        } else {
            scb = createBaseCriteria(TechnologiesConstants.MODEL_OPERATION_PRODUCT_IN_COMPONENT,
                    OperationProductInComponentFields.OPERATION_COMPONENT, OperationProductInComponentFields.PRODUCT,
                    technologyId);
        }
        return scb.list().getEntities();
    }

    public List<Entity> findOutputs(final TechnologyId technologyId,
            final Optional<OutputProductComponentId> excludedComponentId, boolean generationMode) {
        SearchCriteriaBuilder scb = null;
        if (generationMode) {
            scb = createGenerationModeBaseCriteria(TechnologiesConstants.MODEL_OPERATION_PRODUCT_OUT_COMPONENT,
                    OperationProductOutComponentFields.OPERATION_COMPONENT, OperationProductOutComponentFields.PRODUCT,
                    technologyId);
        } else {
            scb = createBaseCriteria(TechnologiesConstants.MODEL_OPERATION_PRODUCT_OUT_COMPONENT,
                    OperationProductOutComponentFields.OPERATION_COMPONENT, OperationProductOutComponentFields.PRODUCT,
                    technologyId);
        }
        excludedComponentId.map(OutputProductComponentId::get).map(SearchRestrictions::idNe).ifPresent(scb::add);
        return scb.list().getEntities();
    }

    private SearchCriteriaBuilder createBaseCriteria(final String modelName, final String tocFieldName,
            final String productFieldName, final TechnologyId technologyId) {
        DataDefinition dd = dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER, modelName);
        SearchCriteriaBuilder opicCriteria = dd.find();
        opicCriteria.createCriteria(tocFieldName, "toc_alias", JoinType.INNER)
                .createCriteria(TechnologyOperationComponentFields.TECHNOLOGY, "tech_alias", JoinType.INNER)
                .add(idEq(technologyId.get()));
        opicCriteria.createCriteria(productFieldName, "prod_alias", JoinType.INNER).add(
                eq(ProductFields.ENTITY_TYPE, ProductFamilyElementType.PRODUCTS_FAMILY.getStringValue()));
        return opicCriteria;
    }

    private SearchCriteriaBuilder createGenerationModeBaseCriteria(final String modelName, final String tocFieldName,
            final String productFieldName, final TechnologyId technologyId) {
        DataDefinition dd = dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER, modelName);
        SearchCriteriaBuilder opicCriteria = dd.find();
        opicCriteria.createCriteria(tocFieldName, "toc_alias", JoinType.INNER)
                .createCriteria(TechnologyOperationComponentFields.TECHNOLOGY, "tech_alias", JoinType.INNER)
                .add(idEq(technologyId.get()));
        opicCriteria.createCriteria(productFieldName, "prod_alias", JoinType.INNER).add(
                eq(ProductFieldsTG.FROM_GENERATOR, true));
        return opicCriteria;
    }

}
