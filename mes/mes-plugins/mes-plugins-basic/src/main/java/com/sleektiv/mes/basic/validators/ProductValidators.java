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
package com.sleektiv.mes.basic.validators;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basic.constants.ParameterFields;
import com.sleektiv.plugin.api.PluginUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.FieldDefinition;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchProjections;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class ProductValidators {

    @Autowired
    private ParameterService parameterService;

    public boolean checkEanUniqueness(final DataDefinition productDD, final FieldDefinition eanFieldDefinition,
            final Entity product, final Object eanOldValue, final Object eanNewValue) {

        if(!PluginUtils.isEnabled("urcBasic") && parameterService.getParameter().getBooleanField(ParameterFields.MANY_ARTICLES_WITH_THE_SAME_EAN)) {
            return true;
        }

        String ean = (String) eanNewValue;
        if (StringUtils.isEmpty(ean) || ObjectUtils.equals(eanOldValue, ean)) {
            return true;
        }

        if (productWithEanAlreadyExists(productDD, ean)) {
            product.addError(eanFieldDefinition, "sleektivView.validate.field.error.duplicated");
            return false;
        }

        return true;
    }

    private boolean productWithEanAlreadyExists(final DataDefinition productDD, final String notEmptyEan) {
        SearchCriteriaBuilder scb = productDD.find();
        scb.setProjection(SearchProjections.id());
        scb.add(SearchRestrictions.eq(ProductFields.EAN, notEmptyEan));
        return scb.setMaxResults(1).uniqueResult() != null;
    }

}
