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
package com.sleektiv.mes.masterOrders.rowStyleResolvers;

import com.google.common.collect.Sets;
import com.sleektiv.mes.masterOrders.constants.SalesVolumeFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.constants.RowStyle;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class SalesVolumesListResolver {
    
    public Set<String> fillRowStyles(final Entity salesVolume) {
        final Set<String> rowStyles = Sets.newHashSet();

        Integer stockForDays = salesVolume.getIntegerField(SalesVolumeFields.STOCK_FOR_DAYS);

        if (Objects.nonNull(stockForDays) && Integer.valueOf(0).compareTo(stockForDays) == 0) {
            rowStyles.add(RowStyle.RED_BACKGROUND);
        }

        return rowStyles;
    }

}
