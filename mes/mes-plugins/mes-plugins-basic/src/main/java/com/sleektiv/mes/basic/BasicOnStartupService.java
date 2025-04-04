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
package com.sleektiv.mes.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sleektiv.mes.basic.loaders.AddressTypeLoader;
import com.sleektiv.mes.basic.loaders.ColorLoader;
import com.sleektiv.mes.basic.loaders.CountryLoader;
import com.sleektiv.mes.basic.loaders.CurrencyLoader;
import com.sleektiv.mes.basic.loaders.DefaultFaultTypesLoader;
import com.sleektiv.mes.basic.loaders.ReportColumnWidthLoader;
import com.sleektiv.mes.basic.loaders.TypeOfPalletLoader;
import com.sleektiv.plugin.api.Module;

@Component
public class BasicOnStartupService extends Module {

    @Autowired
    private CountryLoader countryLoader;

    @Autowired
    private CurrencyLoader currencyLoader;

    @Autowired
    private ReportColumnWidthLoader reportColumnWidthLoader;

    @Autowired
    private TypeOfPalletLoader typeOfPalletLoader;

    @Autowired
    private DefaultFaultTypesLoader defaultFaultTypesLoader;

    @Autowired
    private AddressTypeLoader addressTypeLoader;

    @Autowired
    private ColorLoader colorLoader;

    @Autowired
    private ExchangeRatesUpdateService exchangeRatesUpdateService;

    @Override
    @Transactional
    public void multiTenantEnable() {
        countryLoader.loadCountries();
        currencyLoader.loadCurrencies();
        reportColumnWidthLoader.loadReportColumnWidths();
        typeOfPalletLoader.loadTypeOfPallets();
        defaultFaultTypesLoader.loadDefaultFaultTypes();
        addressTypeLoader.loadAddressTypes();
        colorLoader.loadColors();

        exchangeRatesUpdateService.update();
    }

}
