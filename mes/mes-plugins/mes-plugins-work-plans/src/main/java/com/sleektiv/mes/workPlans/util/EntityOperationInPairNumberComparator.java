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
package com.sleektiv.mes.workPlans.util;

import com.sleektiv.model.api.Entity;
import com.sleektiv.report.api.Pair;

import java.io.Serializable;
import java.util.Comparator;

public class EntityOperationInPairNumberComparator implements Comparator<Pair<Entity, Entity>>, Serializable {

    private static final long serialVersionUID = 678028538725290392L;

    private static final String L_OPERATION = "operation";

    private static final String L_NUMBER = "number";

    @Override
    public int compare(final Pair<Entity, Entity> o1, final Pair<Entity, Entity> o2) {
        return ((Entity) o1.getKey().getField(L_OPERATION)).getField(L_NUMBER).toString()
                .compareTo(((Entity) o2.getKey().getField(L_OPERATION)).getField(L_NUMBER).toString());
    }

}
