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
package com.sleektiv.mes.materialRequirements.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.materialRequirements.constants.MaterialRequirementFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class MaterialRequirementHooks {

    public void onCopy(final DataDefinition materialRequirementDD, final Entity materialRequirement) {
        clearGeneratedOnCopy(materialRequirementDD, materialRequirement);
    }

    public void clearGeneratedOnCopy(final DataDefinition materialRequirementDD, final Entity materialRequirement) {
        materialRequirement.setField(MaterialRequirementFields.FILE_NAME, null);
        materialRequirement.setField(MaterialRequirementFields.GENERATED, false);
        materialRequirement.setField(MaterialRequirementFields.DATE, null);
        materialRequirement.setField(MaterialRequirementFields.WORKER, null);
    }

}
