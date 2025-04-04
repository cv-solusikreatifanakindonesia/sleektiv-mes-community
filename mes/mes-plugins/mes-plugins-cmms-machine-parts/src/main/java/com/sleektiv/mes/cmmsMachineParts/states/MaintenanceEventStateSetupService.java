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
package com.sleektiv.mes.cmmsMachineParts.states;

import com.sleektiv.mes.basic.constants.UserFieldsB;
import com.sleektiv.mes.cmmsMachineParts.constants.MaintenanceEventFields;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.api.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service public class MaintenanceEventStateSetupService {

    private static final String SLEEKTIV_SECURITY = "sleektivSecurity";

    private static final String USER = "user";

    @Autowired private DataDefinitionService dataDefinitionService;

    @Autowired private SecurityService securityService;

    public void setupOnInProgress(final StateChangeContext stateChangeContext) {
        Entity event = stateChangeContext.getOwner();
        if (event.getBelongsToField(MaintenanceEventFields.PERSON_RECEIVING) == null) {
            Entity user = dataDefinitionService.get(SLEEKTIV_SECURITY, USER).get(securityService.getCurrentUserId());
            Entity staff = user.getBelongsToField(UserFieldsB.STAFF);
            if (staff != null) {
                event.setField(MaintenanceEventFields.PERSON_RECEIVING, staff);
                stateChangeContext.setOwner(event);
            }

        }

    }

}
