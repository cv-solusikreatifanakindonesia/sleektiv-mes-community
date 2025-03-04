package com.sleektiv.mes.masterOrders;

import com.google.common.collect.Maps;
import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.constants.UnitConversionItemFieldsB;
import com.sleektiv.mes.masterOrders.constants.MasterOrderFields;
import com.sleektiv.mes.masterOrders.constants.MasterOrderProductFields;
import com.sleektiv.mes.materialFlowResources.constants.*;
import com.sleektiv.mes.materialFlowResources.exceptions.DocumentBuildException;
import com.sleektiv.mes.materialFlowResources.service.DocumentBuilder;
import com.sleektiv.mes.materialFlowResources.service.DocumentManagementService;
import com.sleektiv.model.api.BigDecimalUtils;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.model.api.units.PossibleUnitConversions;
import com.sleektiv.model.api.units.UnitConversionService;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.constants.SleektivSecurityConstants;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MasterOrderDocumentService {


    public static final String L_MASTER_ORDER_RELEASE_LOCATION = "masterOrderReleaseLocation";
    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private DocumentManagementService documentManagementService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private UnitConversionService unitConversionService;

    @Autowired
    private NumberService numberService;

    public void createReleaseDocument(List<Entity> masterOrderProducts, ViewDefinitionState view) {
        FormComponent masterOrderForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity masterOrderFormEntity = masterOrderForm.getEntity();

        Entity user = dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER)
                .get(securityService.getCurrentUserId());

        Entity masterOrderReleaseLocation = parameterService.getParameter().getBelongsToField(L_MASTER_ORDER_RELEASE_LOCATION);
        if (masterOrderReleaseLocation.getBooleanField(LocationFieldsMFR.DRAFT_MAKES_RESERVATION)) {
            for (Entity masterOrderProduct : masterOrderProducts) {
                Entity mo = masterOrderProduct.getDataDefinition().getMasterModelEntity(masterOrderProduct.getId());
                Entity product = mo.getBelongsToField(MasterOrderProductFields.PRODUCT);
                BigDecimal quantity = mo.getDecimalField(MasterOrderProductFields.MASTER_ORDER_QUANTITY);
                BigDecimal availableQuantity = getAvailableQuantityForProductAndLocation(product, masterOrderReleaseLocation);
                if (availableQuantity == null || quantity.compareTo(availableQuantity) > 0) {
                    view.addMessage("masterOrders.masterOrder.releaseDocument.quantity.notEnoughResources",
                            ComponentState.MessageType.FAILURE, product.getStringField(ProductFields.NUMBER));
                    view.addMessage("masterOrders.masterOrder.createReleaseDocument.error", ComponentState.MessageType.FAILURE);
                    return;
                }
            }
        }

        DocumentBuilder documentBuilder = documentManagementService.getDocumentBuilder(user);
        documentBuilder.release(masterOrderReleaseLocation);
        documentBuilder.setField(DocumentFields.DESCRIPTION,
                translationService.translate("masterOrders.masterOrder.releaseDocument.description",
                        LocaleContextHolder.getLocale(), masterOrderFormEntity.getStringField(MasterOrderFields.NUMBER)));
        documentBuilder.setField(DocumentFields.COMPANY, masterOrderFormEntity.getBelongsToField(MasterOrderFields.COMPANY));
        documentBuilder.setField(DocumentFields.ADDRESS, masterOrderFormEntity.getBelongsToField(MasterOrderFields.ADDRESS));

        for (Entity masterOrderProduct : masterOrderProducts) {
            Entity mo = masterOrderProduct.getDataDefinition().getMasterModelEntity(masterOrderProduct.getId());

            Entity position = dataDefinitionService
                    .get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER, MaterialFlowResourcesConstants.MODEL_POSITION)
                    .create();

            Entity product = mo.getBelongsToField(MasterOrderProductFields.PRODUCT);
            BigDecimal quantity = mo.getDecimalField(MasterOrderProductFields.MASTER_ORDER_QUANTITY);
            BigDecimal conversion = BigDecimal.ONE;
            String additionalUnit = product.getStringField(ProductFields.ADDITIONAL_UNIT);
            String unit = product.getStringField(ProductFields.UNIT);

            if (!StringUtils.isEmpty(additionalUnit)) {
                PossibleUnitConversions unitConversions = unitConversionService.getPossibleConversions(unit,
                        searchCriteriaBuilder -> searchCriteriaBuilder
                                .add(SearchRestrictions.belongsTo(UnitConversionItemFieldsB.PRODUCT, product)));

                if (unitConversions.isDefinedFor(additionalUnit)) {
                    BigDecimal convertedQuantity = unitConversions.convertTo(quantity, additionalUnit);

                    position.setField(PositionFields.GIVEN_QUANTITY, convertedQuantity);
                    position.setField(PositionFields.GIVEN_UNIT, additionalUnit);
                    position.setField(PositionFields.CONVERSION,
                            numberService.setScaleWithDefaultMathContext(getConversion(product, unit, additionalUnit)));
                }
            } else {
                position.setField(PositionFields.GIVEN_UNIT, unit);
                position.setField(PositionFields.GIVEN_QUANTITY, quantity);
                position.setField(PositionFields.CONVERSION, conversion);
            }

            position.setField(PositionFields.QUANTITY, quantity);
            position.setField(PositionFields.PRODUCT, product);
            position.setField(PositionFields.SELLING_PRICE, mo.getDecimalField(MasterOrderProductFields.PRICE));

            position.setField(PositionFields.DOCUMENT, documentBuilder.getDocument());

            documentBuilder.addPosition(position);

        }

        try {
            Entity document = documentBuilder.buildWithEntityRuntimeException();
            document = document.getDataDefinition().get(document.getId());
            redirectToCreatedDocument(document, view);
        } catch (DocumentBuildException exc) {
            exc.getGlobalErrors().forEach(errorMessage -> {
                if (!errorMessage.getMessage().equals("sleektivView.validate.global.error.custom")) {
                    view.addMessage(errorMessage.getMessage(), ComponentState.MessageType.FAILURE);
                }
            });
            view.addMessage("masterOrders.masterOrder.createReleaseDocument.error", ComponentState.MessageType.FAILURE);
        }

    }

    private BigDecimal getAvailableQuantityForProductAndLocation(Entity product, Entity location) {
        Entity resourceStockDto = dataDefinitionService
                .get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER, MaterialFlowResourcesConstants.MODEL_RESOURCE_STOCK_DTO)
                .find().add(SearchRestrictions.eq(ResourceStockDtoFields.PRODUCT_ID, product.getId().intValue()))
                .add(SearchRestrictions.eq(ResourceStockDtoFields.LOCATION_ID, location.getId().intValue())).setMaxResults(1)
                .uniqueResult();
        if (Objects.isNull(resourceStockDto)) {
            return BigDecimal.ZERO;
        }
        return BigDecimalUtils.convertNullToZero(resourceStockDto.getDecimalField(ResourceStockDtoFields.AVAILABLE_QUANTITY));
    }

    private void redirectToCreatedDocument(Entity document, ViewDefinitionState view) {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("form.id", document.getId());

        String url = "../page/materialFlowResources/documentDetails.html";
        view.redirectTo(url, false, true, parameters);
    }

    private BigDecimal getConversion(final Entity product, final String unit, final String additionalUnit) {
        PossibleUnitConversions unitConversions = unitConversionService.getPossibleConversions(unit,
                searchCriteriaBuilder -> searchCriteriaBuilder
                        .add(SearchRestrictions.belongsTo(UnitConversionItemFieldsB.PRODUCT, product)));

        if (unitConversions.isDefinedFor(additionalUnit)) {
            return unitConversions.asUnitToConversionMap().get(additionalUnit);
        } else {
            return BigDecimal.ZERO;
        }
    }
}
