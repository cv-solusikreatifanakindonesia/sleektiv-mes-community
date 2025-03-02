/*
 * **************************************************************************
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
 * **************************************************************************
 */
package com.sleektiv.mes.materialFlowResources.imports.parsers;

import com.sleektiv.mes.advancedGenealogy.constants.AdvancedGenealogyConstants;
import com.sleektiv.mes.advancedGenealogy.constants.BatchFields;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.imports.helpers.CellErrorsAccessor;
import com.sleektiv.mes.basic.imports.helpers.CellParser;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;

import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BatchCellParser implements CellParser {

    private static final String L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_LOOKUP_CODE_NOT_FOUND = "sleektivView.validate.field.error.lookupCodeNotFound";
    private static final String L_BATCH_ERROR = "materialFlow.document.importPositions.batchError";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    public void parse(final String cellValue, final String dependentCellValue, final CellErrorsAccessor errorsAccessor,
            final Consumer<Object> valueConsumer) {
        Entity product = getProduct(dependentCellValue);
        if (Objects.isNull(product)) {
            errorsAccessor.addError(L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_LOOKUP_CODE_NOT_FOUND);
            return;
        }

        Entity batch = getBatch(cellValue, product);

        if (Objects.isNull(batch)) {
            Entity newBatch = create(cellValue, product);
            if(newBatch.isValid()) {
                valueConsumer.accept(newBatch);
            } else {
                errorsAccessor.addError(L_BATCH_ERROR);
            }

        } else {
            valueConsumer.accept(batch);
        }

    }

    private Entity create(String number, Entity product) {
        Entity batch = getBatchDD().create();
        batch.setField(BatchFields.NUMBER, number);
        batch.setField(BatchFields.PRODUCT, product.getId());
        return batch.getDataDefinition().save(batch);
    }

    private Entity getBatch(final String number, final Entity product) {

        return getBatchDD().find().add(SearchRestrictions.eq(BatchFields.NUMBER, number))
                .add(SearchRestrictions.belongsTo(BatchFields.PRODUCT, product)).setMaxResults(1).uniqueResult();
    }

    private Entity getProduct(String productNumber) {
        return dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT).find()
                .add(SearchRestrictions.eq(BatchFields.NUMBER, productNumber)).setMaxResults(1).uniqueResult();
    }

    private DataDefinition getBatchDD() {
        return dataDefinitionService.get(AdvancedGenealogyConstants.PLUGIN_IDENTIFIER, AdvancedGenealogyConstants.MODEL_BATCH);
    }

}
