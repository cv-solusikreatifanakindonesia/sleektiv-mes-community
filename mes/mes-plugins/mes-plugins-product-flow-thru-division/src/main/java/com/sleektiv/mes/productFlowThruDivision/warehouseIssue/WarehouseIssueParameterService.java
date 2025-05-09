package com.sleektiv.mes.productFlowThruDivision.warehouseIssue;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.productFlowThruDivision.constants.*;
import com.sleektiv.model.api.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WarehouseIssueParameterService {

    @Autowired
    private ParameterService parameterService;

    public boolean issueForOrder() {
        String productsSource = parameterService.getParameter().getStringField(
                ParameterFieldsPFTD.WAREHOUSE_ISSUE_PRODUCTS_SOURCE);

        if (productsSource != null) {
            WarehouseIssueProductsSource warehouseIssueProductsSource = WarehouseIssueProductsSource.parseString(productsSource);
            return WarehouseIssueProductsSource.ORDER.equals(warehouseIssueProductsSource);
        }
        return false;
    }

    public DrawnDocuments getDrawnDocument() {
        String drawnDocument = parameterService.getParameter().getStringField(ParameterFieldsPFTD.DRAWN_DOCUMENTS);
        return DrawnDocuments.parseString(drawnDocument);
    }

    public DocumentsStatus getDocumentStatusCreatedDocument() {
        String documentStatus = parameterService.getParameter().getStringField(ParameterFieldsPFTD.DOCUMENTS_STATUS);
        return DocumentsStatus.parseString(documentStatus);
    }

    public ProductsToIssue getProductsToIssue() {
        String value = parameterService.getParameter().getStringField(ParameterFieldsPFTD.PRODUCTS_TO_ISSUE);
        return ProductsToIssue.parseString(value);
    }

    public boolean isIssuedQuantityUpToNeed() {
        return parameterService.getParameter().getBooleanField(ParameterFieldsPFTD.ISSUED_QUANTITY_UP_TO_NEED);
    }

    public Optional<Entity> getIssuedWarehouse() {
        return Optional.ofNullable(parameterService.getParameter().getBelongsToField(ParameterFieldsPFTD.ISSUE_LOCATION));
    }

}
