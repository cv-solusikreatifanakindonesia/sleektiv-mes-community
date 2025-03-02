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
package com.sleektiv.mes.productionPerShift.validators;

import static com.sleektiv.model.api.search.SearchProjections.id;
import static com.sleektiv.model.api.search.SearchRestrictions.belongsTo;
import static com.sleektiv.model.api.search.SearchRestrictions.idNe;
import static com.sleektiv.model.api.search.SearchRestrictions.isNotNull;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.productionPerShift.constants.DailyProgressFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;

@Service
public class DailyProgressValidators {

    public boolean validateShiftUniqueness(final DataDefinition dailyProgressDD, final Entity dailyProgress) {
        Entity progressForDay = dailyProgress.getBelongsToField(DailyProgressFields.PROGRESS_FOR_DAY);
        if (progressForDay == null) {
            return true;
        }
        SearchCriteriaBuilder scb = dailyProgressDD.find();
        scb.setProjection(id());
        scb.add(belongsTo(DailyProgressFields.SHIFT, dailyProgress.getBelongsToField(DailyProgressFields.SHIFT)));
        scb.add(belongsTo(DailyProgressFields.PROGRESS_FOR_DAY, progressForDay));
        scb.add(isNotNull(DailyProgressFields.PROGRESS_FOR_DAY));
        if (dailyProgress.getId() != null) {
            scb.add(idNe(dailyProgress.getId()));
        }
        if (Optional.ofNullable(scb.setMaxResults(1).uniqueResult()).isPresent()) {
            dailyProgress.addError(dailyProgressDD.getField(DailyProgressFields.SHIFT),
                    "productionPerShift.dailyProgress.shiftAndProgressForDay.mustBeUnique");
            return false;
        }
        return true;
    }
}
