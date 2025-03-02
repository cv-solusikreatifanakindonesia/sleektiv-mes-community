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
package com.sleektiv.mes.timeNormsForOperations.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.technologies.constants.OperationProductOutComponentFields;
import com.sleektiv.mes.timeNormsForOperations.constants.TechnologyOperationComponentFieldsTNFO;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class OPOCModelHooksTNFO {

    public void setProductionInOneCycleUNIT(final DataDefinition dataDefinition, final Entity opoc) {
        Entity toc = opoc.getBelongsToField(OperationProductOutComponentFields.OPERATION_COMPONENT);
        if (!opoc.getBooleanField(OperationProductOutComponentFields.WASTE)) {
            Entity product = opoc.getBelongsToField(OperationProductOutComponentFields.PRODUCT);
            toc.setField(TechnologyOperationComponentFieldsTNFO.PRODUCTION_IN_ONE_CYCLE_UNIT, product.getField(ProductFields.UNIT));
            toc.getDataDefinition().save(toc);
        }
    }
}
