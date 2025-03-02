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
package com.sleektiv.mes.costCalculation.print;

import com.sleektiv.mes.basic.constants.CurrencyFields;
import com.sleektiv.mes.basic.util.CurrencyService;
import com.sleektiv.mes.costCalculation.constants.CostCalculationFields;
import com.sleektiv.mes.costNormsForMaterials.constants.ProductsCostFields;
import com.sleektiv.mes.costNormsForProduct.constants.ProductFieldsCNFP;
import com.sleektiv.mes.supplyNegotiations.SupplyNegotiationsService;
import com.sleektiv.mes.technologies.constants.OperationProductInComponentFields;
import com.sleektiv.mes.technologies.constants.ProductBySizeGroupFields;
import com.sleektiv.mes.technologies.constants.TechnologyInputProductTypeFields;
import com.sleektiv.model.api.BigDecimalUtils;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductsCostCalculationServiceImpl implements ProductsCostCalculationService {

    @Autowired
    private NumberService numberService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private SupplyNegotiationsService supplyNegotiationsService;

    @Override
    public BigDecimal calculateOperationProductCostPerUnit(Entity costCalculation, Entity product,
                                                           Entity operationProductComponent) {
        Entity offer = costCalculation.getBelongsToField(CostCalculationFields.OFFER);
        BigDecimal costPerUnit;
        if (operationProductComponent.getBooleanField(OperationProductInComponentFields.DIFFERENT_PRODUCTS_IN_DIFFERENT_SIZES)) {
            List<Entity> productBySizeGroups = operationProductComponent
                    .getHasManyField(OperationProductInComponentFields.PRODUCT_BY_SIZE_GROUPS);
            if (!productBySizeGroups.isEmpty()) {
                BigDecimal productBySizeGroupsCost = BigDecimal.ZERO;
                for (Entity productBySizeGroup : productBySizeGroups) {
                    productBySizeGroupsCost = productBySizeGroupsCost.add(
                            calculateProductCostPerUnit(productBySizeGroup.getBelongsToField(ProductBySizeGroupFields.PRODUCT),
                                    costCalculation.getStringField(CostCalculationFields.MATERIAL_COSTS_USED),
                                    costCalculation.getBooleanField(CostCalculationFields.USE_NOMINAL_COST_PRICE_NOT_SPECIFIED),
                                    offer),
                            numberService.getMathContext());
                }
                costPerUnit = productBySizeGroupsCost.divide(new BigDecimal(productBySizeGroups.size()),
                        numberService.getMathContext());
            } else {
                costPerUnit = BigDecimal.ZERO;
            }
        } else if (product != null) {
            costPerUnit = calculateProductCostPerUnit(product,
                    costCalculation.getStringField(CostCalculationFields.MATERIAL_COSTS_USED),
                    costCalculation.getBooleanField(CostCalculationFields.USE_NOMINAL_COST_PRICE_NOT_SPECIFIED),
                    offer);
        } else {
            costPerUnit = BigDecimalUtils.convertNullToZero(
                    operationProductComponent.getBelongsToField(OperationProductInComponentFields.TECHNOLOGY_INPUT_PRODUCT_TYPE)
                            .getDecimalField(TechnologyInputProductTypeFields.AVERAGE_PRICE));
        }
        return costPerUnit;
    }

    @Override
    public BigDecimal calculateProductCostPerUnit(final Entity product, final String materialCostsUsed,
                                                  final boolean useNominalCostPriceNotSpecified, final Entity offer) {
        if (offer != null) {
            BigDecimal offerProductPricePerUnit = supplyNegotiationsService.getPricePerUnit(offer, product);
            if(offerProductPricePerUnit != null){
                return offerProductPricePerUnit;
            }
        }
        Entity materialCurrency = null;
        BigDecimal cost = BigDecimalUtils
                .convertNullToZero(product.getField(ProductsCostFields.forMode(materialCostsUsed).getStrValue()));
        if (useNominalCostPriceNotSpecified && BigDecimalUtils.valueEquals(cost, BigDecimal.ZERO)) {
            cost = BigDecimalUtils.convertNullToZero(product.getField(ProductsCostFields.NOMINAL.getStrValue()));
            materialCurrency = product.getBelongsToField(ProductFieldsCNFP.NOMINAL_COST_CURRENCY);
        } else if (ProductsCostFields.NOMINAL.getMode().equals(materialCostsUsed)) {
            materialCurrency = product.getBelongsToField(ProductFieldsCNFP.NOMINAL_COST_CURRENCY);
        } else if (ProductsCostFields.LAST_PURCHASE.getMode().equals(materialCostsUsed)) {
            materialCurrency = product.getBelongsToField(ProductFieldsCNFP.LAST_PURCHASE_COST_CURRENCY);
        }
        if (materialCurrency == null) {
            materialCurrency = currencyService.getCurrentCurrency();
        }

        String currency = currencyService.getCurrencyAlphabeticCode();
        if (!currency.isEmpty() && materialCurrency != null && !currency.equals(materialCurrency.getStringField(CurrencyFields.ALPHABETIC_CODE))) {
            if (CurrencyService.PLN.equals(currency)) {
                cost = currencyService.getConvertedValue(cost, materialCurrency);
            } else {
                cost = BigDecimal.ZERO;
            }
        }

        BigDecimal costForNumber = BigDecimalUtils.convertNullToOne(product.getDecimalField("costForNumber"));
        if (BigDecimalUtils.valueEquals(costForNumber, BigDecimal.ZERO)) {
            costForNumber = BigDecimal.ONE;
        }

        return cost.divide(costForNumber, numberService.getMathContext());
    }
}
