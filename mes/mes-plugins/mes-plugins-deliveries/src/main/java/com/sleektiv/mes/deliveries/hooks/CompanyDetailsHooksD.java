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
package com.sleektiv.mes.deliveries.hooks;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.CompanyService;
import com.sleektiv.mes.basic.util.CurrencyService;
import com.sleektiv.mes.deliveries.constants.CompanyFieldsD;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class CompanyDetailsHooksD {

    private static final String L_VIEW_DEFINITION_STATE_IS_NULL = "viewDefinitionState is null";

    private static final String L_SUPPLIERS = "suppliers";

    private static final String L_REDIRECT_TO_FILTERED_DELIVERIES_LIST = "redirectToFilteredDeliveriesList";

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CurrencyService currencyService;

    public void disabledGridWhenCompanyIsOwner(final ViewDefinitionState view) {
        companyService.disabledGridWhenCompanyIsOwner(view,  "products");
    }

    public void disableBufferWhenCompanyIsOwner(final ViewDefinitionState view) {
        FormComponent companyForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent bufferField = (FieldComponent) view.getComponentByReference(CompanyFieldsD.BUFFER);

        Boolean isOwner = companyService.isCompanyOwner(companyForm.getEntity());

        bufferField.setEnabled(!isOwner);
    }

    public void updateRibbonState(final ViewDefinitionState view) {
        FormComponent companyForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonGroup suppliers = window.getRibbon().getGroupByName(L_SUPPLIERS);
        RibbonActionItem redirectToFilteredDeliveriesList = suppliers.getItemByName(L_REDIRECT_TO_FILTERED_DELIVERIES_LIST);

        Entity company = companyForm.getEntity();

        boolean isEnabled = Objects.nonNull(company.getId());

        updateButtonState(redirectToFilteredDeliveriesList, isEnabled);
    }

    private void updateButtonState(final RibbonActionItem ribbonActionItem, final boolean isEnabled) {
        ribbonActionItem.setEnabled(isEnabled);
        ribbonActionItem.requestUpdate(true);
    }

    public void fillCurrencyFieldInCompany(final ViewDefinitionState viewDefinitionState) {
        checkArgument(viewDefinitionState != null, L_VIEW_DEFINITION_STATE_IS_NULL);

        FormComponent form = (FormComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_FORM);
        if (form == null) {
            return;
        }

        if (form.getEntityId() == null) {
            Entity currency = currencyService.getCurrentCurrency();
            if (currency != null) {
                fillField((FieldComponent) viewDefinitionState.getComponentByReference(CompanyFieldsD.CURRENCY),
                        currency.getId());
            }
        }
    }

    public void fillField(final FieldComponent fieldComponent, final Object fieldValue) {
        checkArgument(fieldComponent != null, "fieldComponent is null");
        fieldComponent.setFieldValue(fieldValue);
        fieldComponent.requestComponentUpdateState();
    }

}
