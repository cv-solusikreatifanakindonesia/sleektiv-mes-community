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
package com.sleektiv.model.api.units.restrictions;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Preconditions;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.CustomRestriction;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.model.internal.ProxyEntity;

public class BelongingTo implements CustomRestriction {

    private final String fieldName;

    private final ProxyEntity entity;

    private final transient int hashCode;

    public BelongingTo(final String fieldName, final Entity entity) {
        Preconditions.checkArgument(fieldName != null, "Field name can't be null.");
        Preconditions.checkArgument(entity != null, "Entity can't be null.");
        Preconditions.checkArgument(entity.getId() != null, "Entity id can't be null.");
        this.fieldName = fieldName;
        this.entity = new ProxyEntity(entity.getDataDefinition(), entity.getId());
        this.hashCode = new HashCodeBuilder().append(fieldName).append(entity.getId())
                .append(entity.getDataDefinition().getPluginIdentifier()).append(entity.getDataDefinition().getName())
                .toHashCode();
    }

    @Override
    public void addRestriction(final SearchCriteriaBuilder searchCriteriaBuilder) {
        searchCriteriaBuilder.add(SearchRestrictions.belongsTo(fieldName, entity.getDataDefinition(), entity.getId()));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BelongingTo that = (BelongingTo) o;
        DataDefinition thisDataDef = this.entity.getDataDefinition();
        DataDefinition thatDataDef = that.entity.getDataDefinition();
        return new EqualsBuilder().append(this.fieldName, that.fieldName).append(this.entity.getId(), that.entity.getId())
                .append(thisDataDef.getName(), thatDataDef.getName())
                .append(thisDataDef.getPluginIdentifier(), thatDataDef.getPluginIdentifier()).isEquals();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
