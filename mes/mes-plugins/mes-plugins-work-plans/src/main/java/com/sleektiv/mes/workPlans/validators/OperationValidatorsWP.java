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
package com.sleektiv.mes.workPlans.validators;

import com.sleektiv.mes.workPlans.WorkPlansService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.FieldDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperationValidatorsWP {

    @Autowired
    private WorkPlansService workPlansService;

    public boolean validatesWith(final DataDefinition operationDD, final FieldDefinition attachmentFieldDef,
            final Entity operation, final Object oldValue, final Object newValue) {
        return checkAttachmentExtension(operationDD, attachmentFieldDef, operation, oldValue, newValue);
    }

    private boolean checkAttachmentExtension(final DataDefinition operationDD, final FieldDefinition attachmentFieldDef,
            final Entity operation, final Object oldValue, final Object newValue) {
        return workPlansService.checkAttachmentExtension(operationDD, attachmentFieldDef, operation, oldValue, newValue);
    }

}
