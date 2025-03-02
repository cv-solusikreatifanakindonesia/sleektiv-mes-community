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
package com.sleektiv.plugins.unitConversions.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.model.constants.DictionaryFields;
import com.sleektiv.model.constants.SleektivModelConstants;
import com.sleektiv.plugins.dictionaries.constants.SleektivDictionariesContants;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;

@Service
public class UnitConversionsViewListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void redirectToDictionary(final ViewDefinitionState viewDefinitionState, final ComponentState componentState,
            final String[] args) {
        final Entity dictionary = getDictionary("units");
        if (dictionary != null) {
            viewDefinitionState.redirectTo(getUrl(dictionary.getId()), false, true);
        }
    }

    private Entity getDictionary(final String name) {
        return dataDefinitionService.get(SleektivModelConstants.PLUGIN_IDENTIFIER, SleektivModelConstants.MODEL_DICTIONARY).find()
                .add(SearchRestrictions.eq(DictionaryFields.NAME, name)).setMaxResults(1).uniqueResult();
    }

    private String getUrl(final Long dictionaryId) {
        final StringBuilder sb = new StringBuilder("../page/");
        sb.append(SleektivDictionariesContants.PLUGIN_IDENTIFIER).append('/');
        sb.append(SleektivDictionariesContants.VIEW_DICTIONARY_DETAILS);
        sb.append(".html?context={\"form.id\":\"").append(dictionaryId).append("\"}");
        return sb.toString();
    }
}
