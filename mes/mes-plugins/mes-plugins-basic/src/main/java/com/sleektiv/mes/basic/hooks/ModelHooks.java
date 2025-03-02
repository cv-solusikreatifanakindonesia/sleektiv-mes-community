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
package com.sleektiv.mes.basic.hooks;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ModelFields;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class ModelHooks {

    public void onSave(final DataDefinition modelDD, final Entity model) {
        updateProductAssortments(modelDD, model);
    }

    private void updateProductAssortments(final DataDefinition modelDD, final Entity model) {
        Long modelId = model.getId();
        Entity assortment = model.getBelongsToField(ModelFields.ASSORTMENT);

        if (Objects.nonNull(modelId)) {
            Entity modelFromDB = modelDD.get(modelId);

            Entity assortmentFromDB = modelFromDB.getBelongsToField(ModelFields.ASSORTMENT);

            boolean areSame = (Objects.isNull(assortment) ? Objects.isNull(assortmentFromDB)
                    : assortment.equals(assortmentFromDB));

            if (!areSame) {
                List<Entity> products = model.getHasManyField(ModelFields.PRODUCTS);

                products.forEach(product -> {
                    product.setField(ProductFields.MODEL_ASSORTMENT, assortment);
                    product.setField(ProductFields.ASSORTMENT, assortment);

                    product.getDataDefinition().save(product);
                });
            }
        }
    }

}
