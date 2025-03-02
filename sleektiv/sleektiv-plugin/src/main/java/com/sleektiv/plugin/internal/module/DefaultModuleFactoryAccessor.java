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
package com.sleektiv.plugin.internal.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sleektiv.plugin.api.Module;
import com.sleektiv.plugin.api.ModuleFactory;
import com.sleektiv.plugin.api.Plugin;
import com.sleektiv.plugin.api.PluginState;
import com.sleektiv.plugin.api.PluginUtils;
import com.sleektiv.plugin.internal.api.InternalPlugin;
import com.sleektiv.plugin.internal.api.ModuleFactoryAccessor;
import com.sleektiv.tenant.api.MultiTenantCallback;
import com.sleektiv.tenant.api.MultiTenantUtil;

public class DefaultModuleFactoryAccessor implements ModuleFactoryAccessor {

    protected final Map<String, ModuleFactory<?>> moduleFactoryRegistry = new LinkedHashMap<>();

    @Override
    public void init(final List<Plugin> pluginsToInitialize) {
        for (ModuleFactory<?> moduleFactory : moduleFactoryRegistry.values()) {
            moduleFactory.preInit();

            for (Plugin plugin : pluginsToInitialize) {
                for (Module module : ((InternalPlugin) plugin).getModules(moduleFactory)) {
                    module.init();
                }
            }

            moduleFactory.postInit();
        }

        List<ModuleFactory<?>> factories = new ArrayList<ModuleFactory<?>>(moduleFactoryRegistry.values());
        List<Plugin> plugins = new ArrayList<Plugin>(pluginsToInitialize);

        for (ModuleFactory<?> moduleFactory : factories) {
            for (final Plugin plugin : plugins) {
                List<Module> modules = ((InternalPlugin) plugin).getModules(moduleFactory);

                for (final Module module : modules) {
                    if (plugin.hasState(PluginState.ENABLED)) {
                        module.enableOnStartup();

                        MultiTenantUtil.doInMultiTenantContext(new MultiTenantCallback() {

                            @Override
                            public void invoke() {
                                module.multiTenantEnableOnStartup();
                            }

                        });
                    }
                }
            }
        }

        Collections.reverse(factories);
        Collections.reverse(plugins);

        for (ModuleFactory<?> moduleFactory : factories) {
            for (final Plugin plugin : plugins) {

                List<Module> modules = ((InternalPlugin) plugin).getModules(moduleFactory);
                Collections.reverse(modules);

                for (final Module module : modules) {
                    if (!plugin.hasState(PluginState.ENABLED) && !plugin.hasState(PluginState.ENABLING)) {
                        module.disableOnStartup();

                        MultiTenantUtil.doInMultiTenantContext(new MultiTenantCallback() {

                            @Override
                            public void invoke() {
                                module.multiTenantDisableOnStartup();
                            }

                        });
                    }
                }
            }
        }
    }

    @Override
    public void multiTenantEnable(final int tenantId, final Plugin plugin) {
        List<ModuleFactory<?>> factories = new ArrayList<ModuleFactory<?>>(moduleFactoryRegistry.values());

        for (ModuleFactory<?> moduleFactory : factories) {
            List<Module> modules = ((InternalPlugin) plugin).getModules(moduleFactory);

            for (final Module module : modules) {
                MultiTenantUtil.doInMultiTenantContext(tenantId, new MultiTenantCallback() {

                    @Override
                    public void invoke() {
                        if (PluginUtils.isEnabled(plugin)) {
                            module.multiTenantEnable();
                        }
                    }

                });
            }
        }
    }

    @Override
    public void multiTenantDisable(final int tenantId, final Plugin plugin) {
        List<ModuleFactory<?>> factories = new ArrayList<ModuleFactory<?>>(moduleFactoryRegistry.values());
        Collections.reverse(factories);

        for (ModuleFactory<?> moduleFactory : factories) {
            List<Module> modules = ((InternalPlugin) plugin).getModules(moduleFactory);
            Collections.reverse(modules);

            for (final Module module : modules) {
                MultiTenantUtil.doInMultiTenantContext(tenantId, new MultiTenantCallback() {

                    @Override
                    public void invoke() {
                        module.multiTenantDisable();
                    }

                });
            }
        }
    }

    @Override
    public ModuleFactory<?> getModuleFactory(final String identifier) {
        if (!moduleFactoryRegistry.containsKey(identifier)) {
            throw new IllegalStateException("ModuleFactory " + identifier + " is not defined");
        }
        return moduleFactoryRegistry.get(identifier);
    }

    @Override
    public List<ModuleFactory<?>> getModuleFactories() {
        return new ArrayList<ModuleFactory<?>>(moduleFactoryRegistry.values());
    }

    public void setModuleFactories(final List<ModuleFactory<?>> moduleFactories) {
        for (ModuleFactory<?> moduleFactory : moduleFactories) {
            if (moduleFactoryRegistry.containsKey(moduleFactory.getIdentifier())) {
                throw new IllegalStateException("ModuleFactory " + moduleFactory.getClass().getCanonicalName()
                        + " try to overwrite existing module with identifier " + moduleFactory.getIdentifier());
            }
            moduleFactoryRegistry.put(moduleFactory.getIdentifier(), moduleFactory);
        }
    }

}
