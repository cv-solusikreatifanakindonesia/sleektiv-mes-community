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
package com.sleektiv.customTranslation.internal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sleektiv.customTranslation.api.CustomTranslationCacheService;
import com.sleektiv.customTranslation.api.CustomTranslationManagementService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.Module;

@Service
public class CustomTranslationsOnStartupService extends Module {

    @Autowired
    private CustomTranslationManagementService customTranslationManagementService;

    @Autowired
    private CustomTranslationCacheService customTranslationCacheService;

    @Value("${useCustomTranslations}")
    private boolean useCustomTranslations;

    @Override
    @Transactional
    public void multiTenantEnableOnStartup() {
        if (useCustomTranslations) {
            final List<Entity> customTranslations = customTranslationManagementService.getCustomTranslations();
            customTranslationCacheService.loadCustomTranslations(customTranslations);
        }
    }

}
