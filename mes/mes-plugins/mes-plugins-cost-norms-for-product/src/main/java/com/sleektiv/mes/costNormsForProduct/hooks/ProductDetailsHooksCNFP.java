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
package com.sleektiv.mes.costNormsForProduct.hooks;

import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.util.CurrencyService;
import com.sleektiv.mes.costNormsForProduct.constants.CostNormsForProductConstants;
import com.sleektiv.mes.costNormsForProduct.constants.ProductFieldsCNFP;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.sleektiv.mes.costNormsForProduct.constants.ProductFieldsCNFP.*;

@Service
public class ProductDetailsHooksCNFP {

    private static final String L_VIEW_DEFINITION_STATE_IS_NULL = "ViewDefinitionState is null";

    private static final String L_FIELD_COMPONENT_IS_NULL = "FieldComponent is null";

    private static final String L_COST_FOR_NUMBER_UNIT = "costForNumberUnit";

    private static final String L_EMPTY = "";

    private static final String L_COST_TAB = "costTab";

    private static final String L_ROLE_PRODUCT_COSTS = "ROLE_PRODUCT_COSTS";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CurrencyService currencyService;

    public void onBeforeRender(final ViewDefinitionState view) {
        fillUnitFieldInProduct(view);
        fillCurrencyFieldsInProduct(view);
        enableFieldForExternalID(view);
        setCostsTabVisible(view);
    }

    public void fillUnitFieldInProduct(final ViewDefinitionState view) {
        fillUnitField(view, L_COST_FOR_NUMBER_UNIT, true);
    }

    public void fillUnitField(final ViewDefinitionState view, final String fieldName, final boolean inProduct) {
        checkArgument(Objects.nonNull(view), L_VIEW_DEFINITION_STATE_IS_NULL);

        FormComponent productForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent unitField = (FieldComponent) view.getComponentByReference(fieldName);

        unitField.setFieldValue(L_EMPTY);
        unitField.setEnabled(false);

        if (Objects.isNull(productForm) || Objects.isNull(productForm.getEntityId())) {
            return;
        }

        Long productId;

        if (inProduct) {
            productId = (Long) productForm.getFieldValue();
        } else {
            FieldComponent productField = (FieldComponent) view.getComponentByReference("product");

            if (Objects.isNull(productField)) {
                return;
            }

            productId = (Long) productField.getFieldValue();
        }

        Entity product = getProductDD().get(productId);

        if (Objects.isNull(product)) {
            return;
        }

        String unit = product.getStringField(ProductFields.UNIT);

        fillField(unitField, unit);
    }

    public void fillCurrencyFieldsInProduct(final ViewDefinitionState view) {
        fillCurrencyFields(view, CostNormsForProductConstants.CURRENCY_FIELDS_PRODUCT);
    }

    public void fillCurrencyFields(final ViewDefinitionState view, final Set<String> fieldNames) {
        checkArgument(Objects.nonNull(view), L_VIEW_DEFINITION_STATE_IS_NULL);

        FormComponent productForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        clearFields(view, fieldNames);

        if (Objects.isNull(productForm)) {
            return;
        }

        if (Objects.isNull(productForm.getEntityId())) {
            Entity currency = currencyService.getCurrentCurrency();

            if (Objects.nonNull(currency)) {
                fillField((FieldComponent) view.getComponentByReference(ProductFieldsCNFP.NOMINAL_COST_CURRENCY),
                        currency.getId());
                fillField((FieldComponent) view
                        .getComponentByReference(ProductFieldsCNFP.LAST_PURCHASE_COST_CURRENCY), currency.getId());
                fillField((FieldComponent) view.getComponentByReference(ProductFieldsCNFP.AVERAGE_COST_CURRENCY),
                        currency.getId());
            }
        }

        String currencyAlphabeticCode = currencyService.getCurrencyAlphabeticCode();

        if (Objects.isNull(currencyAlphabeticCode)) {
            return;
        }

        for (String fieldName : fieldNames) {
            FieldComponent currencyField = (FieldComponent) view.getComponentByReference(fieldName);

            fillField(currencyField, currencyAlphabeticCode);
        }
    }

    private void clearFields(final ViewDefinitionState view, final Set<String> fieldNames) {
        for (String fieldName : fieldNames) {
            FieldComponent currencyField = (FieldComponent) view.getComponentByReference(fieldName);
            currencyField.setFieldValue(L_EMPTY);
        }
    }

    public void fillField(final FieldComponent fieldComponent, final Object fieldValue) {
        checkArgument(Objects.nonNull(fieldComponent), L_FIELD_COMPONENT_IS_NULL);

        fieldComponent.setFieldValue(fieldValue);
        fieldComponent.requestComponentUpdateState();
    }

    public void enableFieldForExternalID(final ViewDefinitionState view) {
        FormComponent productForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Long productId = productForm.getEntityId();

        if (Objects.isNull(productId)) {
            return;
        }

        Entity product = getProductDD().get(productId);

        if (Objects.isNull(product)) {
            return;
        }

        String externalNumber = product.getStringField(ProductFields.EXTERNAL_NUMBER);

        if (Objects.nonNull(externalNumber)) {
            for (String reference : Arrays.asList(NOMINAL_COST, NOMINAL_COST_CURRENCY, LAST_PURCHASE_COST, AVERAGE_COST,
                    LAST_OFFER_COST, AVERAGE_OFFER_COST)) {
                FieldComponent fieldComponent = (FieldComponent) view.getComponentByReference(reference);
                fieldComponent.setEnabled(true);
            }
        }
    }

    private void setCostsTabVisible(final ViewDefinitionState view) {
        ComponentState costTab = view.getComponentByReference(L_COST_TAB);

        boolean hasCurrentUserRole = securityService.hasCurrentUserRole(L_ROLE_PRODUCT_COSTS);

        costTab.setVisible(hasCurrentUserRole);
    }

    private DataDefinition getProductDD() {
        return dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT);
    }

}
