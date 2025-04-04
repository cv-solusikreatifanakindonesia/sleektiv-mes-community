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
package com.sleektiv.view.internal.controllers;

import com.google.common.collect.Maps;
import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.view.api.crud.CrudService;
import com.sleektiv.view.constants.SystemInfoFields;
import com.sleektiv.view.internal.SystemInfoService;
import com.sleektiv.view.utils.ViewParametersAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

@Controller
public final class SystemInfoController {

    @Value("${buildApplicationName}")
    private String buildApplicationName;

    @Value("${buildApplicationVersion}")
    private String buildApplicationVersion;

    @Value("${buildVersionForUser}")
    private String buildVersionForUser;

    @Value("${buildFrameworkVersion}")
    private String buildFrameworkVersion;

    @Value("${buildTime}")
    private String buildTime;

    @Value("${buildNumber}")
    private String buildNumber;

    @Value("${buildRevision}")
    private String buildRevision;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private CrudService crudController;

    @Autowired
    private ViewParametersAppender viewParametersAppender;

    @Autowired
    private SystemInfoService systemInfoService;

    @RequestMapping(value = "systemInfo", method = RequestMethod.GET)
    public ModelAndView getSystemInfoView(@RequestParam final Map<String, String> arguments, final Locale locale) {
        ModelAndView mav = crudController.prepareView("sleektivView", "systemInfo", arguments, locale);

        viewParametersAppender.appendCommonViewObjects(mav);

        Map<String, String> translationsMap = Maps.newHashMap();

        translationsMap.put("sleektivView.systemInfo.header",
                translationService.translate("sleektivView.systemInfo.header", locale));
        translationsMap.put("sleektivView.systemInfo.buildApplicationName.label",
                translationService.translate("sleektivView.systemInfo.buildApplicationName.label", locale));
        translationsMap.put("sleektivView.systemInfo.buildApplicationVersion.label",
                translationService.translate("sleektivView.systemInfo.buildApplicationVersion.label", locale));
        translationsMap.put("sleektivView.systemInfo.buildFrameworkVersion.label",
                translationService.translate("sleektivView.systemInfo.buildFrameworkVersion.label", locale));
        translationsMap.put("sleektivView.systemInfo.buildNumber.label",
                translationService.translate("sleektivView.systemInfo.buildNumber.label", locale));
        translationsMap.put("sleektivView.systemInfo.buildRevision.label",
                translationService.translate("sleektivView.systemInfo.buildRevision.label", locale));
        translationsMap.put("sleektivView.systemInfo.buildTime.label",
                translationService.translate("sleektivView.systemInfo.buildTime.label", locale));
        translationsMap.put("sleektivView.systemInfo.nextUpdateTime.label",
                translationService.translate("sleektivView.systemInfo.nextUpdateTime.label", locale));

        mav.addObject("translationsMap", translationsMap);

        mav.addObject("buildApplicationName", buildApplicationName);
        mav.addObject("buildApplicationVersion", buildVersionForUser);
        mav.addObject("buildFrameworkVersion", buildVersionForUser);
        mav.addObject("buildNumber", buildNumber);
        mav.addObject("buildTime", buildTime);
        mav.addObject("buildRevision", buildRevision);
        mav.addObject("nextUpdateTime", getNextUpdateTime());

        return mav;
    }

    private String getNextUpdateTime() {
        Date nextUpdateTime = systemInfoService.getSystemInfo().getDateField(SystemInfoFields.NEXT_UPDATE_TIME);

        return DateUtils.toDateTimeString(nextUpdateTime);
    }

}
