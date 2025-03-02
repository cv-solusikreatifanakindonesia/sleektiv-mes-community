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
package com.sleektiv.mes.technologies;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import com.sleektiv.mes.technologies.constants.MrpAlgorithm;
import com.sleektiv.mes.technologies.dto.OperationProductComponentHolder;
import com.sleektiv.mes.technologies.dto.OperationProductComponentWithQuantityContainer;

public interface MrpAlgorithmStrategy {

    boolean isApplicableFor(final MrpAlgorithm mrpAlgorithm);

    Map<Long, BigDecimal> perform(final OperationProductComponentWithQuantityContainer productComponentWithQuantities,
            final Set<OperationProductComponentHolder> nonComponents, MrpAlgorithm mrAlgorithm,
            final String operationProductComponentModelName);

}
