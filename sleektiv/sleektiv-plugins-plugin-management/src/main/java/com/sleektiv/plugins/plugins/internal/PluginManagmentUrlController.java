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
package com.sleektiv.plugins.plugins.internal;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.plugin.api.Plugin;
import com.sleektiv.plugin.api.PluginAccessor;
import com.sleektiv.plugin.api.VersionOfDependency;
import com.sleektiv.plugin.api.artifact.InputStreamPluginArtifact;
import com.sleektiv.plugin.api.artifact.PluginArtifact;
import com.sleektiv.plugins.plugins.constants.SleektivPluginsConstants;
import com.sleektiv.view.api.crud.CrudService;
import com.sleektiv.view.api.utils.SecurityEscapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.*;

@Controller
public class PluginManagmentUrlController {

    private static final String L_TYPE = "type";

    private static final String L_HEADER_LABEL = "headerLabel";

    @Autowired
    private PluginManagmentPerformer pluginManagmentPerformer;

    @Autowired
    private CrudService crudController;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private SecurityEscapeService securityEscapeService;

    @Autowired
    private PluginAccessor pluginAccessor;

    @RequestMapping(value = "pluginPages/downloadPage", method = RequestMethod.GET)
    public ModelAndView getDownloadPageView(final Locale locale) {
        ModelAndView mav = getCrudPopupView(SleektivPluginsConstants.VIEW_PLUGIN_DOWNLOAD, locale);

        mav.addObject(L_HEADER_LABEL, translationService.translate("sleektivPlugins.downloadView.header", locale));
        mav.addObject("buttonLabel", translationService.translate("sleektivPlugins.downloadView.button", locale));
        mav.addObject("chooseFileLabel", translationService.translate("sleektivPlugins.downloadView.chooseFileLabel", locale));

        return mav;
    }

    @RequestMapping(value = "performDownload.html", method = RequestMethod.POST)
    @ResponseBody
    public String handleDownload(@RequestParam("file") final MultipartFile file, final Locale locale) {
        try {
            PluginArtifact artifact = new InputStreamPluginArtifact(file.getOriginalFilename(), file.getInputStream());
            return pluginManagmentPerformer.performInstall(artifact);
        } catch (IOException e) {
            throw new IllegalStateException("Error while reading file", e);
        }
    }

    @RequestMapping(value = "pluginPages/infoPage", method = RequestMethod.GET)
    public ModelAndView getInfoPageView(@RequestParam final Map<String, String> arguments, final Locale locale) {
        ModelAndView mav = getCrudPopupView(SleektivPluginsConstants.VIEW_PLUGIN_INFO, locale);

        if ("success".equals(arguments.get(L_TYPE))) {
            mav.addObject("headerClass", "successHeader");
            mav.addObject(L_HEADER_LABEL, translationService.translate("sleektivPlugins.pluginInfo.successHeader", locale));

        } else if ("error".equals(arguments.get(L_TYPE))) {
            mav.addObject("headerClass", "errorHeader");
            mav.addObject(L_HEADER_LABEL, translationService.translate("sleektivPlugins.pluginInfo.errorHeader", locale));

        } else if ("confirm".equals(arguments.get(L_TYPE))) {
            mav.addObject(L_HEADER_LABEL, translationService.translate("sleektivPlugins.pluginInfo.confirmHeader", locale));
            mav.addObject("isConfirm", true);
            mav.addObject("cancelButtonLabel",
                    translationService.translate("sleektivPlugins.pluginInfo.buttons." + securityEscapeService.encodeHtml(arguments.get("cancelLabel")), locale));
            mav.addObject("acceptButtonLabel",
                    translationService.translate("sleektivPlugins.pluginInfo.buttons." + securityEscapeService.encodeHtml(arguments.get("acceptLabel")), locale));
            mav.addObject("acceptRedirect", arguments.get("acceptRedirect"));

        } else {
            throw new IllegalStateException("Unsuported plugin info type: " + securityEscapeService.encodeHtml(arguments.get(L_TYPE)));
        }

        mav.addObject("content",
                translationService.translate("sleektivPlugins.pluginInfo.content." + securityEscapeService.encodeHtml(arguments.get("status")), locale));
        mav.addObject("deps", createDependenciesMap(arguments, locale));

        return mav;
    }

    @RequestMapping(value = "pluginPages/restartPage", method = RequestMethod.GET)
    public ModelAndView getRestartPageView(@RequestParam final Map<String, String> arguments, final Locale locale) {
        ModelAndView mav = getCrudPopupView(SleektivPluginsConstants.VIEW_RESTART_VIEW, locale);

        mav.addObject(L_HEADER_LABEL, translationService.translate("sleektivPlugins.restartView.header", locale));
        mav.addObject("restartMessage", translationService.translate("sleektivPlugins.restartView.message", locale));
        mav.addObject("restartErrorMessage", translationService.translate("sleektivPlugins.restartView.errorMessage", locale));
        mav.addObject("redirectPage", arguments.get("redirect"));

        return mav;
    }

    @RequestMapping(value = "pluginPages/performRestart", method = RequestMethod.POST)
    @ResponseBody
    public String handleRestart() {
        pluginManagmentPerformer.performRestart();
        return "ok";
    }

    @RequestMapping(value = "pluginPages/performEnablingMultiplePlugins", method = RequestMethod.GET)
    public String performEnablingMultiplePlugins(@RequestParam("plugin") final List<String> plugins, final Locale locale) {
        return "redirect:" + pluginManagmentPerformer.performEnable(plugins);
    }

    @RequestMapping(value = "pluginPages/performDisablingMultiplePlugins", method = RequestMethod.GET)
    public String performDisablingMultiplePlugins(@RequestParam("plugin") final List<String> plugins, final Locale locale) {
        return "redirect:" + pluginManagmentPerformer.performDisable(plugins);
    }

    @RequestMapping(value = "pluginPages/performUninstallingMultiplePlugins", method = RequestMethod.GET)
    public String performUnonstallMultiplePlugins(@RequestParam("plugin") final List<String> plugins, final Locale locale) {
        return "redirect:" + pluginManagmentPerformer.performRemove(plugins);
    }

    private ModelAndView getCrudPopupView(final String viewName, final Locale locale) {
        Map<String, String> crudArgs = new HashMap<String, String>();
        crudArgs.put("popup", "true");
        return crudController.prepareView(SleektivPluginsConstants.PLUGIN_IDENTIFIER, viewName, crudArgs, locale);
    }

    private Map<String, String> createDependenciesMap(final Map<String, String> arguments, final Locale locale) {
        Map<String, String> dependencies = new HashMap<String, String>();
        for (Map.Entry<String, String> arg : arguments.entrySet()) {
            if (arg.getKey().length() < 5) {
                continue;
            }
            if ("dep_".equals(arg.getKey().substring(0, 4))) {
                if ("none".equals(arg.getValue())) {
                    dependencies.put(arg.getKey().substring(4), null);
                } else {
                    dependencies.put(arg.getKey().substring(4), convertVersionString(arg.getValue(), locale));
                }
            }
        }
        if (dependencies.isEmpty()) {
            return null;
        } else {
            return addGroupsToPlugins(dependencies);

        }
    }

    private Map<String, String> addGroupsToPlugins(Map<String, String> dependencies) {
        Map<String, String> newDependencies = new HashMap<>();
        Iterator<Map.Entry<String, String>> it = dependencies.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            Plugin plugin = pluginAccessor.getPlugin(entry.getKey());
            String key = plugin.getPluginInformation().getGroup() + "." + plugin.getIdentifier();
            newDependencies.put(key, entry.getValue());
        }

        return newDependencies;
    }

    private String convertVersionString(final String versionStr, final Locale locale) {
        StringBuilder result = new StringBuilder();
        result.append(translationService.translate("sleektivPlugins.pluginInfo.inVersion", locale));
        result.append(" ");
        VersionOfDependency version = new VersionOfDependency(versionStr);
        if (version.getMinVersion() != null) {
            result.append(translationService.translate("sleektivPlugins.pluginInfo.versionFrom", locale));
            result.append(" ");
            result.append(version.getMinVersion());
        }
        if (version.getMinVersion() != null && version.getMaxVersion() != null) {
            result.append(" ");
            result.append(translationService.translate("sleektivPlugins.pluginInfo.versionAnd", locale));
            result.append(" ");
        }
        if (version.getMaxVersion() != null) {
            result.append(translationService.translate("sleektivPlugins.pluginInfo.versionTo", locale));
            result.append(" ");
            result.append(version.getMaxVersion());
        }
        return result.toString();
    }
}
