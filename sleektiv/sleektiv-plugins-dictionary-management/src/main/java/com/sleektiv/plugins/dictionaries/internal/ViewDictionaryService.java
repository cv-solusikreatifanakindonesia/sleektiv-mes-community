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
package com.sleektiv.plugins.dictionaries.internal;

import com.sleektiv.model.api.DictionaryService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.CustomRestriction;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewDictionaryService {

    @Autowired
    private DictionaryService dictionaryService;

    public void translateLabel(final ViewDefinitionState state) {
        GridComponent grid = (GridComponent) state.getComponentByReference(SleektivViewConstants.L_GRID);

        List<Entity> entities = grid.getEntities();

        for (Entity entity : entities) {
            entity.setField("name", dictionaryService.getName(entity.getStringField("name"), state.getLocale()));
        }
    }

    public void addRestrictionToDictionariesGrid(final ViewDefinitionState viewDefinitionState) {
        GridComponent grid = (GridComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_GRID);
        grid.setCustomRestriction(new CustomRestriction() {

            @Override
            public void addRestriction(final SearchCriteriaBuilder searchCriteriaBuilder) {
                searchCriteriaBuilder.add(SearchRestrictions.eq("active", true));
            }
        });
    }

}
