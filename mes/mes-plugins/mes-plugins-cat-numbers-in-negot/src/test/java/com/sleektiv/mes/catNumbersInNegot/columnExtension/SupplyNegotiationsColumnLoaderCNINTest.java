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
package com.sleektiv.mes.catNumbersInNegot.columnExtension;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sleektiv.mes.supplyNegotiations.SupplyNegotiationsColumnLoaderService;

public class SupplyNegotiationsColumnLoaderCNINTest {

    private SupplyNegotiationsColumnLoaderCNIN supplyNegotiationsColumnLoaderCNIN;

    @Mock
    private SupplyNegotiationsColumnLoaderService supplyNegotiationsColumnLoaderService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        supplyNegotiationsColumnLoaderCNIN = new SupplyNegotiationsColumnLoaderCNIN();

        ReflectionTestUtils.setField(supplyNegotiationsColumnLoaderCNIN, "supplyNegotiationsColumnLoaderService",
                supplyNegotiationsColumnLoaderService);
    }

    @Test
    public void shouldAddColumnsForRequestsCNIN() {
        // given

        // when
        supplyNegotiationsColumnLoaderCNIN.addColumnsForRequestsCNIN();

        // then
        verify(supplyNegotiationsColumnLoaderService).fillColumnsForRequests(Mockito.anyString());
    }

    @Test
    public void shouldDeleteColumnsForRequestsCNIN() {
        // given

        // when
        supplyNegotiationsColumnLoaderCNIN.deleteColumnsForRequestsCNIN();

        // then
        verify(supplyNegotiationsColumnLoaderService).clearColumnsForRequests(Mockito.anyString());
    }

}
