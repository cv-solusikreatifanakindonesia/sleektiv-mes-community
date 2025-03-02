package com.sleektiv.mes.technologies.listeners;

import org.springframework.stereotype.Service;

import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;

@Service
public class ProductsToProductGroupTechnologyListListeners {

    public void openProductsToProductGroupTechnologyImportPage(final ViewDefinitionState view, final ComponentState state, final String[] args) {

        view.openModal("../page/technologies/productsToProductGroupTechnologyImport.html");
    }
}
