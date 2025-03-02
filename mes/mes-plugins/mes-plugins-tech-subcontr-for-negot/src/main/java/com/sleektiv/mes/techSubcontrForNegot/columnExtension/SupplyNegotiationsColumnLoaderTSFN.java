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
package com.sleektiv.mes.techSubcontrForNegot.columnExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.mes.supplyNegotiations.SupplyNegotiationsColumnLoaderService;
import com.sleektiv.mes.techSubcontrForNegot.constants.TechSubcontrForNegotConstants;

@Component
public class SupplyNegotiationsColumnLoaderTSFN {

    private static final Logger LOG = LoggerFactory.getLogger(SupplyNegotiationsColumnLoaderTSFN.class);

    @Autowired
    private SupplyNegotiationsColumnLoaderService supplyNegotiationsColumnLoaderService;

    public void addColumnsForRequestsTSFN() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Columns for requests table will be populated ...");
        }

        supplyNegotiationsColumnLoaderService.fillColumnsForRequests(TechSubcontrForNegotConstants.PLUGIN_IDENTIFIER);
    }

    public void deleteColumnsForRequestsTSFN() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Columns for requests table will be unpopulated ...");
        }

        supplyNegotiationsColumnLoaderService.clearColumnsForRequests(TechSubcontrForNegotConstants.PLUGIN_IDENTIFIER);
    }

    public void addColumnsForOffersTSFN() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Columns for offers table will be populated ...");
        }

        supplyNegotiationsColumnLoaderService.fillColumnsForOffers(TechSubcontrForNegotConstants.PLUGIN_IDENTIFIER);
    }

    public void deleteColumnsForOffersTSFN() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Columns for offers table will be unpopulated ...");
        }

        supplyNegotiationsColumnLoaderService.clearColumnsForOffers(TechSubcontrForNegotConstants.PLUGIN_IDENTIFIER);
    }

}
