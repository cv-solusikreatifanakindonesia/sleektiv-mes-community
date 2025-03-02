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
package com.sleektiv.mes.deliveries.hooks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.util.CurrencyService;
import com.sleektiv.mes.deliveries.constants.CompanyFieldsD;
import com.sleektiv.mes.deliveries.constants.DeliveryFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class CompanyHooksD {

    @Autowired
    private CurrencyService currencyService;

    public boolean onDelete(final DataDefinition companyDD, final Entity company) {
        return showDeliveriesForCompany(company);
    }

    private boolean showDeliveriesForCompany(final Entity company) {
        boolean deliveriesDeleted = true;

        List<Entity> deliveries = company.getHasManyField(CompanyFieldsD.DELIVERIES);

        if (!deliveries.isEmpty()) {
            StringBuilder args = new StringBuilder();
            for (Entity delivery : deliveries) {
                if (args.length() > 0) {
                    args.append(", ");
                }

                String number = delivery.getStringField(DeliveryFields.NUMBER);
                args.append(number);
            }

            company.addGlobalError("basic.company.deliveries.error.deliveriesNotDeleted", args.toString());

            deliveriesDeleted = false;
        }

        return deliveriesDeleted;
    }

    public void onCreate(final DataDefinition companyDD, final Entity company) {
        Entity currentCurrency = currencyService.getCurrentCurrency();
        if (company.getBelongsToField(CompanyFieldsD.CURRENCY) == null) {
            company.setField(CompanyFieldsD.CURRENCY, currentCurrency);
        }
    }
}
