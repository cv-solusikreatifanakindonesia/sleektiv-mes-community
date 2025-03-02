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
package com.sleektiv.model.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

public class BelongsToIntegrationTest extends IntegrationTest {

    @Test
    public void shouldSaveBelongsToField() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition machineDao = dataDefinitionService.get(PLUGIN_MACHINES_NAME, ENTITY_NAME_MACHINE);
        DataDefinition componentDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_COMPONENT);

        Entity machine = machineDao.save(createMachine("asd"));
        Entity product = productDao.save(createProduct("asd", "asd"));
        Entity component = createComponent("name", product, machine);

        // when
        component = componentDao.save(component);

        // then
        assertNotNull(component);

        assertEquals(product.getId(), ((Entity) component.getField("product")).getId());

        Map<String, Object> result = jdbcTemplate.queryForMap("select * from " + TABLE_NAME_COMPONENT);

        assertNotNull(result);
        assertEquals(product.getId(), result.get("product_id"));
    }

    @Test
    public void shouldSaveBelongsToFieldWithId() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition machineDao = dataDefinitionService.get(PLUGIN_MACHINES_NAME, ENTITY_NAME_MACHINE);
        DataDefinition componentDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_COMPONENT);

        Entity machine = machineDao.save(createMachine("asd"));
        Entity product = productDao.save(createProduct("asd", "asd"));
        Entity component = createComponent("name", product.getId(), machine.getId());

        // when
        component = componentDao.save(component);

        // then
        assertNotNull(component);

        assertEquals(product.getId(), ((Entity) component.getField("product")).getId());

        Map<String, Object> result = jdbcTemplate.queryForMap("select * from " + TABLE_NAME_COMPONENT);

        assertNotNull(result);
        assertEquals(product.getId(), result.get("product_id"));
    }

    @Test
    public void shouldGetEagerBelongsToField() throws Exception {
        // given
        DataDefinition productDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition machineDao = dataDefinitionService.get(PLUGIN_MACHINES_NAME, ENTITY_NAME_MACHINE);
        DataDefinition componentDao = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_COMPONENT);

        Entity machine = machineDao.save(createMachine("asd"));
        Entity product = productDao.save(createProduct("asd", "asd"));
        Entity component = componentDao.save(createComponent("name", product, machine));

        // when
        component = componentDao.get(component.getId());

        // then
        assertNotNull(component);
        assertEquals(product.getId(), ((Entity) component.getField("product")).getId());
    }

    @Test
    public void shouldGetLazyBelongsToField() throws Exception {
        // given
        DataDefinition productDataDefinition = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_PRODUCT);
        DataDefinition machineDataDefinition = dataDefinitionService.get(PLUGIN_MACHINES_NAME, ENTITY_NAME_MACHINE);
        DataDefinition componentDataDefinition = dataDefinitionService.get(PLUGIN_PRODUCTS_NAME, ENTITY_NAME_COMPONENT);

        Entity machine = machineDataDefinition.save(createMachine("asd"));
        Entity product = productDataDefinition.save(createProduct("asd", "asd"));
        Entity component = componentDataDefinition.save(createComponent("name", product, machine));

        // when
        component = componentDataDefinition.get(component.getId());

        // then
        assertNotNull(component);
        assertEquals(machine.getId(), ((Entity) component.getField("machine")).getId());
    }

}
