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
package com.sleektiv.mes.basic.imports.ean;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.imports.services.XlsxImportService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;

@Service
public class EanXlsxImportService extends XlsxImportService {

    private static final String L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_LOOKUP_CODE_NOT_FOUND = "sleektivView.validate.field.error.lookupCodeNotFound";

    @Override
    public boolean shouldUpdate(final ViewDefinitionState view) {
        return true;
    }

    @Override
    public String getLogType(final String modelName) {
        return new StringBuilder(ProductFields.EAN).append(StringUtils.capitalize(L_IMPORT)).toString();
    }

    @Override
    public void validateEntity(final Entity product, final DataDefinition productDD) {
        validateProduct(product, productDD);
    }

    private void validateProduct(final Entity product, final DataDefinition productDD) {
        if (Objects.isNull(product.getId())) {
            product.addError(productDD.getField(ProductFields.NUMBER), L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_LOOKUP_CODE_NOT_FOUND);
        }
    }

}
