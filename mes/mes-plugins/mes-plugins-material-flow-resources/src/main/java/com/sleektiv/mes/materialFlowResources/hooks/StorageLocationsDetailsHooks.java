package com.sleektiv.mes.materialFlowResources.hooks;

import com.sleektiv.mes.materialFlowResources.constants.StorageLocationFields;
import com.sleektiv.mes.materialFlowResources.criteriaModifiers.ProductsCriteriaModifiers;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.*;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class StorageLocationsDetailsHooks {

    public static final String L_PRODUCTS_LOOKUP = "productsLookup";

    public void onBeforeRender(final ViewDefinitionState view) {
        setFieldsEnabled(view);
        setFilterValueHolders(view);
    }

    private void setFieldsEnabled(final ViewDefinitionState view) {
        FormComponent storageLocationForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        CheckBoxComponent isPlaceCheckBox = (CheckBoxComponent) view.getComponentByReference(StorageLocationFields.PLACE_STORAGE_LOCATION);
        FieldComponent maximumNumberOfPalletsField = (FieldComponent) view.getComponentByReference(StorageLocationFields.MAXIMUM_NUMBER_OF_PALLETS);
        GridComponent productsGrid = (GridComponent) view.getComponentByReference(StorageLocationFields.PRODUCTS);

        boolean isSaved = Objects.nonNull(storageLocationForm.getEntityId());

        if (isPlaceCheckBox.isChecked()) {
            maximumNumberOfPalletsField.setEnabled(true);
        } else {
            maximumNumberOfPalletsField.setEnabled(false);
            maximumNumberOfPalletsField.setFieldValue(null);
        }

        maximumNumberOfPalletsField.requestComponentUpdateState();
        productsGrid.setEnabled(isSaved);
    }

    private void setFilterValueHolders(final ViewDefinitionState view) {
        LookupComponent locationLookup = (LookupComponent) view.getComponentByReference(StorageLocationFields.LOCATION);
        LookupComponent productsLookup = (LookupComponent) view.getComponentByReference(L_PRODUCTS_LOOKUP);

        Entity location = locationLookup.getEntity();

        FilterValueHolder filterValueHolder = productsLookup.getFilterValue();

        if (Objects.isNull(location)) {
            filterValueHolder.remove(ProductsCriteriaModifiers.L_LOCATION_ID);
        } else {
            Long locationId = location.getId();

            filterValueHolder.put(ProductsCriteriaModifiers.L_LOCATION_ID, locationId);
        }

        productsLookup.setFilterValue(filterValueHolder);
    }

}
