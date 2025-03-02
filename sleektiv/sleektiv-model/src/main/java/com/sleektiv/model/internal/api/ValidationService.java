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
package com.sleektiv.model.internal.api;

import com.sleektiv.model.api.Entity;

/**
 * Service for entity validation, parsing fields and calling model hooks
 */
public interface ValidationService {

    /**
     * Parse, validate given entity and call model hooks.
     * 
     * @param dataDefinition
     *            model data definition for validated entity
     * @param genericEntity
     *            entity to be validated
     * @param existingGenericEntity
     *            existing entity, might be null if validated entity is currently created
     */
    void validateGenericEntity(InternalDataDefinition dataDefinition, Entity genericEntity, Entity existingGenericEntity);

}