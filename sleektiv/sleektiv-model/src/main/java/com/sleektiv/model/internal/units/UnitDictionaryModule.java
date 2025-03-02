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
package com.sleektiv.model.internal.units;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.model.constants.SleektivModelConstants;
import com.sleektiv.model.internal.api.InternalDictionaryService;
import com.sleektiv.plugin.api.Module;

@Service
public class UnitDictionaryModule extends Module {

    @Autowired
    private InternalDictionaryService dictionaryService;

    @Override
    public void multiTenantDisable() {
        disable();
    }

    @Override
    public void disable() {
        dictionaryService.disable(SleektivModelConstants.PLUGIN_IDENTIFIER, SleektivModelConstants.DICTIONARY_UNITS);
    }
}
