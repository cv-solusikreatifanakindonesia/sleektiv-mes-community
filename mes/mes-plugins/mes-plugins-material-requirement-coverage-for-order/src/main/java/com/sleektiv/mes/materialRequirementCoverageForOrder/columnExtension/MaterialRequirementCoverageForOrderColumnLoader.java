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
package com.sleektiv.mes.materialRequirementCoverageForOrder.columnExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.mes.materialRequirementCoverageForOrder.MaterialRequirementCoverageForOrderColumnLoaderService;
import com.sleektiv.mes.materialRequirementCoverageForOrder.constans.MaterialRequirementCoverageForOrderConstans;

@Component
public class MaterialRequirementCoverageForOrderColumnLoader {

    private static final Logger LOG = LoggerFactory.getLogger(MaterialRequirementCoverageForOrderColumnLoader.class);

    @Autowired
    private MaterialRequirementCoverageForOrderColumnLoaderService mRCForOrderColumnLoaderService;

    public void addColumnsForCoverages() {
        if (!mRCForOrderColumnLoaderService.isColumnsForCoveragesEmpty()) {
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Columns for coverages table will be populated ...");
        }

        mRCForOrderColumnLoaderService.fillColumnsForCoverages(MaterialRequirementCoverageForOrderConstans.PLUGIN_IDENTIFIER);
    }

    public void deleteColumnsForCoverages() {
        if (mRCForOrderColumnLoaderService.isColumnsForCoveragesEmpty()) {
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Columns for coverages table will be unpopulated ...");
        }

        mRCForOrderColumnLoaderService.clearColumnsForCoverages(MaterialRequirementCoverageForOrderConstans.PLUGIN_IDENTIFIER);
    }

}
