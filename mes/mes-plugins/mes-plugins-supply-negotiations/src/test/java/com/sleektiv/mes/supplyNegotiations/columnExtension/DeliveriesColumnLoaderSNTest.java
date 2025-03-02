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
package com.sleektiv.mes.supplyNegotiations.columnExtension;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sleektiv.mes.deliveries.DeliveriesColumnLoaderService;

public class DeliveriesColumnLoaderSNTest {

    private DeliveriesColumnLoaderSN deliveriesColumnLoaderSN;

    @Mock
    private DeliveriesColumnLoaderService deliveriesColumnLoaderService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        deliveriesColumnLoaderSN = new DeliveriesColumnLoaderSN();

        ReflectionTestUtils.setField(deliveriesColumnLoaderSN, "deliveriesColumnLoaderService", deliveriesColumnLoaderService);
    }

    @Test
    public void shouldAddColumnsForDeliveriesSN() {
        // given

        // when
        deliveriesColumnLoaderSN.addColumnsForDeliveriesSN();

        // then
        verify(deliveriesColumnLoaderService).fillColumnsForDeliveries(Mockito.anyString());
    }

    @Test
    public void shouldDeleteColumnsForDeliveriesSN() {
        // given

        // when
        deliveriesColumnLoaderSN.deleteColumnsForDeliveriesSN();

        // then
        verify(deliveriesColumnLoaderService).clearColumnsForDeliveries(Mockito.anyString());
    }

    @Test
    public void shouldAddColumnsForOrdersSN() {
        // given

        // when
        deliveriesColumnLoaderSN.addColumnsForOrdersSN();

        // then
        verify(deliveriesColumnLoaderService).fillColumnsForOrders(Mockito.anyString());
    }

    @Test
    public void shouldDeleteColumnsForOrdersSN() {
        // given

        // when
        deliveriesColumnLoaderSN.deleteColumnsForOrdersSN();

        // then
        verify(deliveriesColumnLoaderService).clearColumnsForOrders(Mockito.anyString());
    }

}
