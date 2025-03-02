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
package com.sleektiv.view.internal.module;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import com.sleektiv.plugin.api.ModuleFactory;
import com.sleektiv.view.internal.api.InternalViewDefinitionService;
import com.sleektiv.view.internal.xml.ViewDefinitionParser;

public class ViewModuleFactory extends ModuleFactory<ViewModule> {

    @Autowired
    private ViewDefinitionParser viewDefinitionParser;

    @Autowired
    private InternalViewDefinitionService viewDefinitionService;

    @Override
    protected ViewModule parseElement(final String pluginIdentifier, final Element element) {
        String resource = getRequiredAttribute(element, "resource");

        return new ViewModule(pluginIdentifier, new ClassPathResource(pluginIdentifier + "/" + resource), viewDefinitionParser,
                viewDefinitionService);
    }

    @Override
    public String getIdentifier() {
        return "view";
    }

}
