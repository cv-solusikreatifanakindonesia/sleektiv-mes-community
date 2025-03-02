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
package com.sleektiv.mes.costCalculation.hooks;

import com.sleektiv.mes.costCalculation.constants.StandardLaborCostFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Service
public class StandardLaborCostHooks {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void onSave(final DataDefinition standardLaborCostDD, final Entity standardLaborCost) {
        setStandardLaborCostNumber(standardLaborCost);
    }

    private void setStandardLaborCostNumber(final Entity standardLaborCost) {
        if (checkIfShouldInsertNumber(standardLaborCost)) {
            String number = jdbcTemplate.queryForObject("select generate_standard_labor_cost_number()", Collections.emptyMap(),
                    String.class);
            standardLaborCost.setField(StandardLaborCostFields.NUMBER, number);
        }
    }

    private boolean checkIfShouldInsertNumber(final Entity standardLaborCost) {
        if (!Objects.isNull(standardLaborCost.getId())) {
            return false;
        }
        return !StringUtils.isNotBlank(standardLaborCost.getStringField(StandardLaborCostFields.NUMBER));
    }

}
