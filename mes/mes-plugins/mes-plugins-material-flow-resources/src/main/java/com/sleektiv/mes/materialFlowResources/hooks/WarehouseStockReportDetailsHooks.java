package com.sleektiv.mes.materialFlowResources.hooks;

import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.materialFlowResources.constants.StorageLocationMode;
import com.sleektiv.mes.materialFlowResources.constants.WarehouseStockReportFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class WarehouseStockReportDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent warehouseStockReportForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity warehouseStockReport = warehouseStockReportForm.getPersistedEntityWithIncludedFormValues();

        if (Objects.isNull(warehouseStockReport.getDateField(WarehouseStockReportFields.WAREHOUSE_STOCK_DATE))) {
            FieldComponent warehouseStockDateField = (FieldComponent) view
                    .getComponentByReference(WarehouseStockReportFields.WAREHOUSE_STOCK_DATE);

            warehouseStockDateField.setFieldValue(DateUtils.toDateString(new Date()));
            warehouseStockDateField.requestComponentUpdateState();
        }

        disableForm(view, warehouseStockReportForm, warehouseStockReport);

        setCriteriaModifierParameters(view, warehouseStockReport);
    }

    private void disableForm(final ViewDefinitionState view, final FormComponent warehouseStockReportForm, final Entity stocktaking) {
        GridComponent storageLocationsGrid = (GridComponent) view
                .getComponentByReference(WarehouseStockReportFields.STORAGE_LOCATIONS);

        if (stocktaking.getBooleanField(WarehouseStockReportFields.GENERATED)) {
            warehouseStockReportForm.setFormEnabled(false);
            storageLocationsGrid.setEnabled(false);
        } else {
            warehouseStockReportForm.setFormEnabled(true);
            changeStorageLocationsGridEnabled(view);
        }
    }

    private void changeStorageLocationsGridEnabled(final ViewDefinitionState view) {
        FormComponent warehouseStockReportForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        GridComponent storageLocationsGrid = (GridComponent) view
                .getComponentByReference(WarehouseStockReportFields.STORAGE_LOCATIONS);

        Entity warehouseStockReport = warehouseStockReportForm.getEntity();

        boolean enabled = StorageLocationMode.SELECTED.getStringValue().equals(
                warehouseStockReport.getStringField(WarehouseStockReportFields.STORAGE_LOCATION_MODE));

        storageLocationsGrid.setEnabled(enabled);
    }

    public void changeStorageLocationsGridEnabled(final ViewDefinitionState view, final ComponentState componentState,
                                                  final String[] args) {
        changeStorageLocationsGridEnabled(view);
    }

    private void setCriteriaModifierParameters(final ViewDefinitionState view, final Entity warehouseStockReport) {
        LookupComponent storageLocationLookup = (LookupComponent) view.getComponentByReference("storageLocationLookup");

        Entity location = warehouseStockReport.getBelongsToField(WarehouseStockReportFields.LOCATION);

        FilterValueHolder filterValueHolder = storageLocationLookup.getFilterValue();

        if (Objects.nonNull(location)) {
            filterValueHolder.put(WarehouseStockReportFields.LOCATION, location.getId());
        } else {
            filterValueHolder.put(WarehouseStockReportFields.LOCATION, 0L);
        }

        storageLocationLookup.setFilterValue(filterValueHolder);
    }

}
