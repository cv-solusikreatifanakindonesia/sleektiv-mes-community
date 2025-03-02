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
package com.sleektiv.report.internal.module;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import com.sleektiv.plugin.api.ModuleFactory;
import com.sleektiv.report.internal.templates.ReportTemplateService;

public class ReportTemplateModuleFactory extends ModuleFactory<ReportTemplateModule> {

    @Autowired
    private ReportTemplateService reportTemplateService;

    @Override
    protected ReportTemplateModule parseElement(final String pluginIdentifier, final Element element) {
        String resource = getRequiredAttribute(element, "resource");
        String name = getRequiredAttribute(element, "name");

        return new ReportTemplateModule(pluginIdentifier, name, new ClassPathResource(pluginIdentifier + "/" + resource),
                reportTemplateService);
    }

    @Override
    public String getIdentifier() {
        return "report-template";
    }

}
