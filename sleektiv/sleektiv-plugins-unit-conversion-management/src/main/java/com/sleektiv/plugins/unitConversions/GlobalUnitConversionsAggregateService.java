/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
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
package com.sleektiv.plugins.unitConversions;

/**
 * Service for managing unit conversions
 * 
 * @since 1.1.8
 * @author maku
 */
public interface GlobalUnitConversionsAggregateService {

    /**
     * Returns identifier of existing UnitConversionsAggregate entity. If entity does not exists creates new one.
     * 
     * @return identifier of existing or just created UnitConversionsAggregate entity
     * 
     * @throws IllegalStateException
     *             if created entity has a validation error
     */
    Long getAggregateId();
}
