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
package com.sleektiv.mes.basic.hooks;

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service public class FormHooks {

    private static final String L_QCADOO_VIEW_VALIDATE_FIELD_ERROR_MISSING = "sleektivView.validate.field.error.missing";

    public boolean validatesWith(final DataDefinition dataDefinition, final Entity form) {

        BigDecimal size = form.getDecimalField("size");

        if (size != null && StringUtils.isEmpty(form.getStringField("unit"))) {
            form.addError(dataDefinition.getField("unit"), L_QCADOO_VIEW_VALIDATE_FIELD_ERROR_MISSING);

            return false;
        }
        return true;
    }
}
