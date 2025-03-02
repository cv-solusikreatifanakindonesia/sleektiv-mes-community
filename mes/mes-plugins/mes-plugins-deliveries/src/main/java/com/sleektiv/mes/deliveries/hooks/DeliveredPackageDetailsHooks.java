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

import com.google.common.collect.Lists;
import com.sleektiv.mes.deliveries.DeliveriesService;
import com.sleektiv.mes.deliveries.constants.DeliveredProductFields;
import com.sleektiv.view.api.ViewDefinitionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveredPackageDetailsHooks {

    @Autowired
    private DeliveriesService deliveriesService;

    public void onBeforeRender(final ViewDefinitionState view) {
        fillUnitField(view);
    }

    public void fillUnitField(final ViewDefinitionState view) {
        List<String> unitNames = Lists.newArrayList("deliveredQuantityUnit");

        deliveriesService.fillUnitFields(view, DeliveredProductFields.PRODUCT, unitNames);
    }

}
