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
package com.sleektiv.mes.basic.controllers;

import com.google.common.collect.ImmutableMap;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.view.api.crud.CrudService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;
import java.util.Map;

@Controller
public class BasicController {

    @Autowired
    private CrudService crudService;

    @Autowired
    private ParameterService parameterService;

    @RequestMapping(value = "generalParameters", method = RequestMethod.GET)
    public ModelAndView getGeneralParameterPageView(final Locale locale) {
        JSONObject json = new JSONObject(ImmutableMap.of("form.id", parameterService.getParameterId().toString()));

        Map<String, String> arguments = ImmutableMap.of("context", json.toString());

        return crudService.prepareView(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.VIEW_GENERAL_PARAMETERS, arguments, locale);
    }

    @RequestMapping(value = "licenses", method = RequestMethod.GET)
    public ModelAndView getLicensesPageView(final Locale locale) {
        JSONObject json = new JSONObject(ImmutableMap.of("form.id", parameterService.getParameterId().toString()));

        Map<String, String> arguments = ImmutableMap.of("context", json.toString());

        return crudService.prepareView(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.VIEW_LICENSES, arguments, locale);
    }

}
