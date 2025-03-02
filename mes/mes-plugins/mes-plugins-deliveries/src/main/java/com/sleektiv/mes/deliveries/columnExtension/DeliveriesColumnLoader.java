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
package com.sleektiv.mes.deliveries.columnExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.mes.deliveries.DeliveriesColumnLoaderService;
import com.sleektiv.mes.deliveries.constants.DeliveriesConstants;

@Component
public class DeliveriesColumnLoader {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveriesColumnLoader.class);

    @Autowired
    private DeliveriesColumnLoaderService deliveriesColumnLoaderService;

    public void addColumnsForDeliveries() {
        if (!deliveriesColumnLoaderService.isColumnsForDeliveriesEmpty()) {
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Columns for deliveries table will be populated ...");
        }

        deliveriesColumnLoaderService.fillColumnsForDeliveries(DeliveriesConstants.PLUGIN_IDENTIFIER);
    }

    public void deleteColumnsForDeliveries() {
        if (deliveriesColumnLoaderService.isColumnsForDeliveriesEmpty()) {
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Columns for deliveries table will be unpopulated ...");
        }

        deliveriesColumnLoaderService.clearColumnsForDeliveries(DeliveriesConstants.PLUGIN_IDENTIFIER);
    }

    public void addColumnsForOrders() {
        if (!deliveriesColumnLoaderService.isColumnsForOrdersEmpty()) {
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Columns for orders table will be populated ...");
        }

        deliveriesColumnLoaderService.fillColumnsForOrders(DeliveriesConstants.PLUGIN_IDENTIFIER);
    }

    public void deleteColumnsForOrders() {
        if (deliveriesColumnLoaderService.isColumnsForOrdersEmpty()) {
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Columns for orders table will be unpopulated ...");
        }

        deliveriesColumnLoaderService.clearColumnsForOrders(DeliveriesConstants.PLUGIN_IDENTIFIER);
    }

}
