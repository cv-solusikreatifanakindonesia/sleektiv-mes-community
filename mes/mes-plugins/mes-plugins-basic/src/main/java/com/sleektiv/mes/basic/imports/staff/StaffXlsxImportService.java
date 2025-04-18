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
package com.sleektiv.mes.basic.imports.staff;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.StaffFields;
import com.sleektiv.mes.basic.constants.WorkstationFields;
import com.sleektiv.mes.basic.imports.services.XlsxImportService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class StaffXlsxImportService extends XlsxImportService {

    private static final String L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_CUSTOM = "sleektivView.validate.field.error.custom";

    @Override
    public void validateEntity(final Entity staff, final DataDefinition staffDD) {
        validateWorkstation(staff, staffDD);
    }

    private void validateWorkstation(final Entity staff, final DataDefinition staffDD) {
        Entity workstation = staff.getBelongsToField(StaffFields.WORKSTATION);
        Entity division = staff.getBelongsToField(StaffFields.DIVISION);

        if (Objects.nonNull(workstation)) {
            if (Objects.nonNull(division)) {
                Entity workstationDivision = workstation.getBelongsToField(WorkstationFields.DIVISION);

                if (!workstationDivision.getId().equals(division.getId())) {
                    staff.addError(staffDD.getField(StaffFields.WORKSTATION), L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_CUSTOM);
                }
            }
        }
    }

}
