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
package com.sleektiv.mes.supplyNegotiations;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sleektiv.mes.supplyNegotiations.columnExtension.DeliveriesColumnLoaderSN;
import com.sleektiv.mes.supplyNegotiations.columnExtension.SupplyNegotiationsColumnLoader;

public class SupplyNegotiationsOnStartupServiceTest {

    private SupplyNegotiationsOnStartupService supplyNegotiationsOnStartupService;

    @Mock
    private SupplyNegotiationsColumnLoader supplyNegotiationsColumnLoader;

    @Mock
    private DeliveriesColumnLoaderSN deliveriesColumnLoaderSN;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        supplyNegotiationsOnStartupService = new SupplyNegotiationsOnStartupService();

        ReflectionTestUtils.setField(supplyNegotiationsOnStartupService, "supplyNegotiationsColumnLoader",
                supplyNegotiationsColumnLoader);
        ReflectionTestUtils.setField(supplyNegotiationsOnStartupService, "deliveriesColumnLoaderSN", deliveriesColumnLoaderSN);
    }

    @Test
    public void shouldMultiTenantEnable() {
        // given

        // when
        supplyNegotiationsOnStartupService.multiTenantEnable();

        // then
        verify(supplyNegotiationsColumnLoader).addColumnsForRequests();
        verify(supplyNegotiationsColumnLoader).addColumnsForOffers();

        verify(deliveriesColumnLoaderSN).addColumnsForDeliveriesSN();
        verify(deliveriesColumnLoaderSN).addColumnsForOrdersSN();
    }

    @Test
    public void shouldMultiTenantDisable() {
        // given

        // when
        supplyNegotiationsOnStartupService.multiTenantDisable();

        // then
        verify(supplyNegotiationsColumnLoader).deleteColumnsForRequests();
        verify(supplyNegotiationsColumnLoader).deleteColumnsForOffers();

        verify(deliveriesColumnLoaderSN).deleteColumnsForDeliveriesSN();
        verify(deliveriesColumnLoaderSN).deleteColumnsForOrdersSN();
    }

}
