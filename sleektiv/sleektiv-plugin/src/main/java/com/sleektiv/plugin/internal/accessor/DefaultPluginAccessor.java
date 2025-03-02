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
package com.sleektiv.plugin.internal.accessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.sleektiv.model.beans.sleektivPlugin.SleektivPluginPlugin;
import com.sleektiv.plugin.api.Plugin;
import com.sleektiv.plugin.api.PluginState;
import com.sleektiv.plugin.api.Version;
import com.sleektiv.plugin.internal.PluginUtilsService;
import com.sleektiv.plugin.internal.api.InternalPlugin;
import com.sleektiv.plugin.internal.api.InternalPluginAccessor;
import com.sleektiv.plugin.internal.api.ModuleFactoryAccessor;
import com.sleektiv.plugin.internal.api.PluginDao;
import com.sleektiv.plugin.internal.api.PluginDependencyManager;
import com.sleektiv.plugin.internal.api.PluginDescriptorParser;
import com.sleektiv.plugin.internal.stateresolver.InternalPluginStateResolver;
import com.sleektiv.tenant.api.MultiTenantUtil;

@Service
public class DefaultPluginAccessor implements InternalPluginAccessor, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPluginAccessor.class);

    @Autowired
    private PluginDescriptorParser pluginDescriptorParser;

    @Autowired
    private PluginDao pluginDao;

    @Autowired
    private PluginDependencyManager pluginDependencyManager;

    @Autowired
    private ModuleFactoryAccessor moduleFactoryAccessor;

    @Autowired
    private InternalPluginStateResolver pluginStateResolver;

    @Autowired
    @SuppressWarnings("unused")
    private PluginUtilsService pluginUtilsService;

    @Autowired
    @SuppressWarnings("unused")
    private MultiTenantUtil multiTenantUtil;

    private final Map<String, Plugin> plugins = new HashMap<String, Plugin>();

    private volatile boolean alreadyInitialized;

    @Override
    public Plugin getEnabledPlugin(final String identifier) {
        Plugin plugin = plugins.get(identifier);

        if (plugin == null) {
            return null;
        }
        if (plugin.hasState(PluginState.ENABLED)) {
            return plugin;
        } else {
            return null;
        }
    }

    @Override
    public Collection<Plugin> getSystemPlugins() {
        Set<Plugin> systemPlugins = new HashSet<Plugin>();

        for (Plugin plugin : plugins.values()) {
            if (plugin.isSystemPlugin()) {
                systemPlugins.add(plugin);
            }
        }

        return systemPlugins;
    }

    @Override
    public Collection<Plugin> getEnabledPlugins() {
        Set<Plugin> enabledPlugins = new HashSet<Plugin>();

        for (Plugin plugin : plugins.values()) {
            if (plugin.hasState(PluginState.ENABLED)) {
                enabledPlugins.add(plugin);
            }
        }

        return enabledPlugins;
    }

    @Override
    public Plugin getPlugin(final String identifier) {
        return plugins.get(identifier);
    }

    @Override
    public Collection<Plugin> getPlugins() {
        return plugins.values();
    }

    public void init() {
        LOG.info("Plugin Framework initialization");

        long time = System.currentTimeMillis();

        pluginStateResolver.setPluginAccessor(this);

        Set<InternalPlugin> enabledPluginsFromDescriptor = pluginDescriptorParser.loadPlugins();
        Set<SleektivPluginPlugin> pluginsFromDatabase = pluginDao.list();

        for (InternalPlugin plugin : enabledPluginsFromDescriptor) {
            SleektivPluginPlugin existingPlugin = null;
            for (SleektivPluginPlugin databasePlugin : pluginsFromDatabase) {
                if (plugin.getIdentifier().equals(databasePlugin.getIdentifier())) {
                    existingPlugin = databasePlugin;
                    break;
                }
            }
            if (existingPlugin == null) {
                plugin.changeStateTo(PluginState.ENABLING);
            } else {
                plugin.changeStateTo(PluginState.valueOf(existingPlugin.getState()));
            }

            if (existingPlugin == null || plugin.compareVersion(new Version(existingPlugin.getVersion())) > 0) {
                pluginDao.save(plugin);
            } else if (plugin.compareVersion(new Version(existingPlugin.getVersion())) < 0) {
                throw new IllegalStateException("Unsupported operation: downgrade, for plugin: " + plugin.getIdentifier());
            }

            LOG.info("Registering plugin " + plugin);

            plugins.put(plugin.getIdentifier(), plugin);
        }
        for (SleektivPluginPlugin databasePlugin : pluginsFromDatabase) {
            if (databasePlugin.getState().equals(PluginState.TEMPORARY.toString())) {
                continue;
            }

            Plugin existingPlugin = null;
            for (Plugin plugin : enabledPluginsFromDescriptor) {
                if (databasePlugin.getIdentifier().equals(plugin.getIdentifier())) {
                    existingPlugin = plugin;
                    break;
                }
            }
            if (existingPlugin == null) {
                pluginDao.delete(databasePlugin);
            }
        }
        Set<InternalPlugin> temporaryPlugins = pluginDescriptorParser.getTemporaryPlugins();
        for (InternalPlugin plugin : temporaryPlugins) {
            plugins.put(plugin.getIdentifier(), plugin);
        }
        LOG.info("Plugin Framework initialized in " + (System.currentTimeMillis() - time) + "ms");
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (!alreadyInitialized) {
            synchronized (this) {
                if (!alreadyInitialized) {
                    init();
                    alreadyInitialized = true;
                }
            }
        }
        if (event != null && event.getSource() instanceof XmlWebApplicationContext) {
            XmlWebApplicationContext eventSource = (XmlWebApplicationContext) event.getSource();
            if (eventSource.getParent() != null) {
                return;
            }
        }

        long time = System.currentTimeMillis();

        List<Plugin> sortedPlugins = pluginDependencyManager.sortPluginsInDependencyOrder(plugins.values(), plugins);

        moduleFactoryAccessor.init(sortedPlugins);

        for (Plugin plugin : sortedPlugins) {
            if (plugin.hasState(PluginState.ENABLING)) {
                ((InternalPlugin) plugin).changeStateTo(PluginState.ENABLED);
                pluginDao.save(plugin);
            }
        }

        LOG.info("Plugin Framework started in " + (System.currentTimeMillis() - time) + "ms");
    }

    void setPluginDescriptorParser(final PluginDescriptorParser pluginDescriptorParser) {
        this.pluginDescriptorParser = pluginDescriptorParser;
    }

    void setPluginDao(final PluginDao pluginDao) {
        this.pluginDao = pluginDao;
    }

    void setPluginDependencyManager(final PluginDependencyManager pluginDependencyManager) {
        this.pluginDependencyManager = pluginDependencyManager;
    }

    void setModuleFactoryAccessor(final ModuleFactoryAccessor moduleFactoryAccessor) {
        this.moduleFactoryAccessor = moduleFactoryAccessor;
    }

    void setInternalPluginStateResolver(final InternalPluginStateResolver pluginStateResolver) {
        this.pluginStateResolver = pluginStateResolver;
    }

    @Override
    public void savePlugin(final Plugin plugin) {
        plugins.put(plugin.getIdentifier(), plugin);
    }

    @Override
    public void removePlugin(final Plugin plugin) {
        plugins.remove(plugin.getIdentifier());
    }

}
