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
package com.sleektiv.plugin.integration;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.File;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.sleektiv.plugin.api.PluginAccessor;
import com.sleektiv.plugin.api.PluginManager;
import com.sleektiv.plugin.api.PluginOperationResult;
import com.sleektiv.plugin.api.PluginOperationStatus;
import com.sleektiv.plugin.api.PluginState;
import com.sleektiv.plugin.api.PluginStateResolver;
import com.sleektiv.plugin.api.Version;
import com.sleektiv.plugin.api.artifact.JarPluginArtifact;
import com.sleektiv.plugin.internal.PluginUtilsService;
import com.sleektiv.plugin.internal.api.InternalPlugin;
import com.sleektiv.plugin.internal.api.InternalPluginAccessor;
import com.sleektiv.tenant.api.MultiTenantUtil;
import com.sleektiv.tenant.internal.DefaultMultiTenantService;

public class PluginIntegrationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private PluginAccessor pluginAccessor;

    private PluginManager pluginManager;

    private ClassPathXmlApplicationContext applicationContext;

    private SessionFactory sessionFactory;

    @Before
    public void init() throws Exception {
        MultiTenantUtil multiTenantUtil = new MultiTenantUtil();
        ReflectionTestUtils.setField(multiTenantUtil, "multiTenantService", new DefaultMultiTenantService());
        multiTenantUtil.init();

        PluginStateResolver mockPluginStateResolver = mock(PluginStateResolver.class);
        given(mockPluginStateResolver.isEnabled(Mockito.anyString())).willReturn(true);

        PluginUtilsService pluginUtil = new PluginUtilsService(mockPluginStateResolver);
        pluginUtil.init();

        new File("target/plugins").mkdir();
        new File("target/tmpPlugins").mkdir();

        applicationContext = new ClassPathXmlApplicationContext();
        applicationContext.setConfigLocation("com/sleektiv/plugin/integration/spring.xml");
        applicationContext.registerShutdownHook();
        applicationContext.refresh();

        pluginAccessor = applicationContext.getBean(InternalPluginAccessor.class);
        pluginManager = applicationContext.getBean(PluginManager.class);
        sessionFactory = applicationContext.getBean(SessionFactory.class);
    }

    @After
    @Transactional("plugin")
    public void destroy() throws Exception {
        sessionFactory.openSession().createSQLQuery("delete from sleektivplugin_plugin").executeUpdate();
        pluginManager = null;
        sessionFactory.close();
        sessionFactory = null;
        applicationContext.close();
        deleteDirectory(new File("target/plugins"));
        deleteDirectory(new File("target/tmpPlugins"));
    }

    @Test
    public void shouldHavePlugins() throws Exception {
        // then
        assertEquals(3, pluginAccessor.getPlugins().size());
        assertEquals(3, pluginAccessor.getEnabledPlugins().size());
        assertNotNull(pluginAccessor.getPlugin("plugin1"));
        assertNotNull(pluginAccessor.getPlugin("plugin2"));
        assertNotNull(pluginAccessor.getPlugin("plugin3"));
        assertNotNull(pluginAccessor.getEnabledPlugin("plugin1"));
        assertNotNull(pluginAccessor.getEnabledPlugin("plugin2"));
        assertNotNull(pluginAccessor.getEnabledPlugin("plugin3"));
    }

    @Test
    public void shouldEnablePlugin() throws Exception {
        // given
        pluginManager.disablePlugin("plugin1", "plugin2");

        // when
        PluginOperationResult result = pluginManager.enablePlugin("plugin1");

        // then
        assertTrue(result.isSuccess());
        assertFalse(result.isRestartNeccessary());
        assertEquals(2, pluginAccessor.getEnabledPlugins().size());
        assertNotNull(pluginAccessor.getEnabledPlugin("plugin1"));
        assertNull(pluginAccessor.getEnabledPlugin("plugin2"));
    }

    @Test
    public void shouldNotEnablePluginWithDisabledDependencies() throws Exception {
        // given
        pluginManager.disablePlugin("plugin1", "plugin2");

        // when
        PluginOperationResult result = pluginManager.enablePlugin("plugin2");

        // then
        assertFalse(result.isSuccess());
        assertFalse(result.isRestartNeccessary());
        assertEquals(1, result.getPluginDependencyResult().getDependenciesToEnable().size());
        assertEquals("plugin1", result.getPluginDependencyResult().getDependenciesToEnable().iterator().next().getIdentifier());
        assertEquals(PluginOperationStatus.DEPENDENCIES_TO_ENABLE, result.getStatus());
    }

    @Test
    public void shouldEnablePluginWithDependencies() throws Exception {
        // given
        pluginManager.disablePlugin("plugin2");

        // when
        PluginOperationResult result = pluginManager.enablePlugin("plugin2");

        // then
        assertTrue(result.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS, result.getStatus());
    }

    @Test
    public void shouldEnablePluginAndDependency() throws Exception {
        // given
        pluginManager.disablePlugin("plugin1", "plugin2");

        // when
        PluginOperationResult result = pluginManager.enablePlugin("plugin1", "plugin2");

        // then
        assertTrue(result.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS, result.getStatus());
    }

    @Test
    public void shouldDisablePlugin() throws Exception {
        // when
        PluginOperationResult result = pluginManager.disablePlugin("plugin2");

        // then
        assertTrue(result.isSuccess());
        assertEquals(PluginOperationStatus.SUCCESS, result.getStatus());
    }

    @Test
    public void shouldNotDisablePluginWithEnabledDependency() throws Exception {
        // given
        pluginManager.enablePlugin("plugin1", "plugin2");

        // when
        PluginOperationResult result = pluginManager.disablePlugin("plugin1");

        // then
        assertFalse(result.isSuccess());
        assertEquals(1, result.getPluginDependencyResult().getDependenciesToDisable().size());
        assertEquals(PluginOperationStatus.DEPENDENCIES_TO_DISABLE, result.getStatus());
    }

    @Test
    public void shouldNotDisableSystemPlugin() throws Exception {
        // given
        pluginManager.enablePlugin("plugin3");

        // when
        PluginOperationResult result = pluginManager.disablePlugin("plugin3");

        // then
        assertFalse(result.isSuccess());
        assertEquals(PluginOperationStatus.SYSTEM_PLUGIN_DISABLING, result.getStatus());
    }

    @Test
    public void shouldInstallPlugin() throws Exception {
        // given
        JarPluginArtifact artifact = new JarPluginArtifact(new File(
                "src/test/resources/com/sleektiv/plugin/integration/plugin4.jar"));

        // when
        PluginOperationResult result = pluginManager.installPlugin(artifact);

        // then
        assertTrue(result.isSuccess());
        assertFalse(result.isRestartNeccessary());
        assertNotNull(pluginAccessor.getPlugin("plugin4"));
        assertTrue(pluginAccessor.getPlugin("plugin4").hasState(PluginState.TEMPORARY));
    }

    @Test
    public void shouldEnableInstalledPlugin() throws Exception {
        // given
        JarPluginArtifact artifact = new JarPluginArtifact(new File(
                "src/test/resources/com/sleektiv/plugin/integration/plugin4.jar"));
        pluginManager.installPlugin(artifact);

        // when
        PluginOperationResult result = pluginManager.enablePlugin("plugin4");

        // then
        assertTrue(result.isSuccess());
        assertTrue(result.isRestartNeccessary());
        assertNotNull(pluginAccessor.getPlugin("plugin4"));
        assertTrue(pluginAccessor.getPlugin("plugin4").hasState(PluginState.ENABLING));
    }

    @Test
    public void shouldUninstallPlugin() throws Exception {
        // given
        JarPluginArtifact artifact = new JarPluginArtifact(new File(
                "src/test/resources/com/sleektiv/plugin/integration/plugin4.jar"));
        pluginManager.installPlugin(artifact);
        pluginManager.enablePlugin("plugin4");

        // when
        PluginOperationResult result = pluginManager.uninstallPlugin("plugin4");

        // then
        assertTrue(result.isSuccess());
        assertTrue(result.isRestartNeccessary());
        assertNull(pluginAccessor.getPlugin("plugin4"));
    }

    @Test
    public void shouldUpdateEnabledPlugin() throws Exception {
        // given
        JarPluginArtifact artifact = new JarPluginArtifact(new File(
                "src/test/resources/com/sleektiv/plugin/integration/plugin4.jar"));
        JarPluginArtifact artifact2 = new JarPluginArtifact(new File(
                "src/test/resources/com/sleektiv/plugin/integration/plugin4.1.jar"));

        pluginManager.installPlugin(artifact);
        pluginManager.enablePlugin("plugin4");
        ((InternalPlugin) pluginAccessor.getPlugin("plugin4")).changeStateTo(PluginState.ENABLED);

        // when
        PluginOperationResult result = pluginManager.installPlugin(artifact2);

        // then
        assertTrue(result.isSuccess());
        assertTrue(result.isRestartNeccessary());
        assertNotNull(pluginAccessor.getPlugin("plugin4"));
        assertTrue(pluginAccessor.getPlugin("plugin4").hasState(PluginState.ENABLING));
        assertEquals(new Version("1.2.4"), pluginAccessor.getPlugin("plugin4").getVersion());
    }

    @Test
    public void shouldUpdateTemporaryPlugin() throws Exception {
        // given
        JarPluginArtifact artifact = new JarPluginArtifact(new File(
                "src/test/resources/com/sleektiv/plugin/integration/plugin4.jar"));
        JarPluginArtifact artifact2 = new JarPluginArtifact(new File(
                "src/test/resources/com/sleektiv/plugin/integration/plugin4.1.jar"));

        pluginManager.installPlugin(artifact);

        // when
        PluginOperationResult result = pluginManager.installPlugin(artifact2);

        // then
        assertTrue(result.isSuccess());
        assertFalse(result.isRestartNeccessary());
        assertNotNull(pluginAccessor.getPlugin("plugin4"));
        assertTrue(pluginAccessor.getPlugin("plugin4").hasState(PluginState.TEMPORARY));
        assertEquals(new Version("1.2.4"), pluginAccessor.getPlugin("plugin4").getVersion());
    }

    @Test
    public void shouldNotDisableTemporaryPlugin() throws Exception {
        // given
        JarPluginArtifact artifact = new JarPluginArtifact(new File(
                "src/test/resources/com/sleektiv/plugin/integration/plugin4.jar"));

        pluginManager.installPlugin(artifact);

        // when
        PluginOperationResult result = pluginManager.disablePlugin("plugin4");

        // then
        assertTrue(result.isSuccess());
        assertNotNull(pluginAccessor.getPlugin("plugin4"));
        assertTrue(pluginAccessor.getPlugin("plugin4").hasState(PluginState.TEMPORARY));
    }

    @Test
    public void shouldNotDowngradePlugin() throws Exception {
        // given
        JarPluginArtifact artifact = new JarPluginArtifact(new File(
                "src/test/resources/com/sleektiv/plugin/integration/plugin4.jar"));
        JarPluginArtifact artifact2 = new JarPluginArtifact(new File(
                "src/test/resources/com/sleektiv/plugin/integration/plugin4.1.jar"));

        pluginManager.installPlugin(artifact2);

        // when
        PluginOperationResult result = pluginManager.installPlugin(artifact);

        // then
        assertFalse(result.isSuccess());
        assertEquals(PluginOperationStatus.CANNOT_DOWNGRADE_PLUGIN, result.getStatus());
        assertNotNull(pluginAccessor.getPlugin("plugin4"));
        assertTrue(pluginAccessor.getPlugin("plugin4").hasState(PluginState.TEMPORARY));
        assertEquals(new Version("1.2.4"), pluginAccessor.getPlugin("plugin4").getVersion());
    }

}