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
package com.sleektiv.mes.materialFlowResources.hooks;

import com.sleektiv.mes.materialFlowResources.constants.PositionFields;
import com.sleektiv.mes.materialFlowResources.constants.ReservationFields;
import com.sleektiv.mes.materialFlowResources.service.ReservationsService;
import com.sleektiv.mes.materialFlowResources.service.ResourceReservationsService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PositionModelHooks {

    @Autowired
    private ReservationsService reservationsService;

    @Autowired
    private ResourceReservationsService resourceReservationsService;

    public void onSave(final DataDefinition positionDD, final Entity position) {
        if (position.getId() != null) {
            reservationsService.updateReservationFromDocumentPosition(position);
        }
    }

    public void onCopy(final DataDefinition positionDD, final Entity position) {
        position.setField(PositionFields.RESOURCE, null);
    }

    public void onCreate(final DataDefinition positionDD, final Entity position) {
        reservationsService.createReservationFromDocumentPosition(position);
    }

    public boolean onDelete(final DataDefinition positionDD, final Entity position) {
        resourceReservationsService.updateResourceQuantites(position,
                position.getDecimalField(ReservationFields.QUANTITY).negate());
        return true;
    }

}
