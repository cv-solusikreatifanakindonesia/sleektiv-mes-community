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
package com.sleektiv.mes.materialFlowResources.criteriaModifiers;

import com.sleektiv.mes.materialFlow.constants.UserFieldsMF;
import com.sleektiv.mes.materialFlow.constants.UserLocationFields;
import com.sleektiv.mes.materialFlowResources.constants.ReservationFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.EntityList;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.constants.SleektivSecurityConstants;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationsCriteriaModifier {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void restrictToUserLocations(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        scb.add(SearchRestrictions.gt(ReservationFields.QUANTITY, BigDecimal.ZERO));
        Long currentUserId = securityService.getCurrentUserId();
        if (Objects.nonNull(currentUserId)) {
            EntityList userLocations = userDataDefinition().get(currentUserId).getHasManyField(UserFieldsMF.USER_LOCATIONS);
            if (!userLocations.isEmpty()) {
                Set<Long> locationIds = userLocations.stream().map(ul -> ul.getBelongsToField(UserLocationFields.LOCATION))
                        .map(Entity::getId).collect(Collectors.toSet());
                scb.add(SearchRestrictions.in("location.id", locationIds));
            }
        }
    }

    private DataDefinition userDataDefinition() {
        return dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER);
    }
}
