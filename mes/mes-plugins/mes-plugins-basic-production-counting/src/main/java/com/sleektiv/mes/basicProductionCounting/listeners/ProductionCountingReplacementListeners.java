package com.sleektiv.mes.basicProductionCounting.listeners;

import com.google.common.collect.Lists;
import com.sleektiv.mes.basicProductionCounting.constants.BasicProductionCountingConstants;
import com.sleektiv.mes.basicProductionCounting.constants.ProductionCountingQuantityFields;
import com.sleektiv.mes.basicProductionCounting.constants.ProductionCountingQuantityRole;
import com.sleektiv.mes.basicProductionCounting.constants.SectionFieldsBPC;
import com.sleektiv.mes.technologies.constants.OperationProductInComponentFields;
import com.sleektiv.mes.technologies.constants.SectionFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.model.api.exception.EntityRuntimeException;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductionCountingReplacementListeners {


    private static final String WINDOW_MAIN_TAB_FORM_PCQ = "window.mainTab.form.productionCountingQuantity";

    private static final String L_WINDOW_MAIN_TAB_FORM_BASIC_PRODUCT = "window.mainTab.form.basicProduct";

    private static final String PRODUCT = "product";
    public static final String L_PLANNED_QUANTITY = "plannedQuantity";
    public static final String L_REPLACES_QUANTITY = "replacesQuantity";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NumberService numberService;

    public void onPlannedQuantityChange(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FieldComponent plannedQuantity = (FieldComponent) view.getComponentByReference(L_PLANNED_QUANTITY);
        FieldComponent replacesQuantity = (FieldComponent) view.getComponentByReference(L_REPLACES_QUANTITY);
        replacesQuantity.setFieldValue(plannedQuantity.getFieldValue());
    }

    public void addReplacement(final ViewDefinitionState view, final ComponentState state, final String[] args)
            throws JSONException {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        CheckBoxComponent generated = (CheckBoxComponent) view.getComponentByReference("generated");
        generated.setChecked(false);

        Entity entity = form.getPersistedEntityWithIncludedFormValues();
        entity = entity.getDataDefinition().validate(entity);
        if (!entity.isValid()) {
            form.setEntity(entity);
            return;
        }
        JSONObject context = view.getJsonContext();
        Long productionCountingQuantityId = context.getLong(WINDOW_MAIN_TAB_FORM_PCQ);
        Long basicProductId = context.getLong(L_WINDOW_MAIN_TAB_FORM_BASIC_PRODUCT);

        Entity productionCountingQuantity = dataDefinitionService.get(BasicProductionCountingConstants.PLUGIN_IDENTIFIER,
                BasicProductionCountingConstants.MODEL_PRODUCTION_COUNTING_QUANTITY).get(productionCountingQuantityId);

        try {
            createProductionCountingQuantity(view, entity, productionCountingQuantity, basicProductId);
            generated.setChecked(true);
        } catch (EntityRuntimeException ere) {
            ere.getGlobalErrors().forEach(errorMessage -> {
                view.addMessage(errorMessage.getMessage(), ComponentState.MessageType.FAILURE, errorMessage.getVars());
            });
            view.addMessage("productionCounting.useReplacement.error", ComponentState.MessageType.FAILURE);
        }
    }

    @Transactional
    private void createProductionCountingQuantity(ViewDefinitionState view, Entity entity,
                                                    Entity productionCountingQuantity, Long basicProductId) {
        BigDecimal plannedQuantity = entity.getDecimalField(L_PLANNED_QUANTITY);
        BigDecimal replacesQuantity = entity.getDecimalField(L_REPLACES_QUANTITY);

        BigDecimal pcqPlannedQuantity = productionCountingQuantity.getDecimalField(ProductionCountingQuantityFields.PLANNED_QUANTITY);
        String role = productionCountingQuantity.getStringField(ProductionCountingQuantityFields.ROLE);

        BigDecimal newPcqPlannedQuantity = pcqPlannedQuantity.subtract(replacesQuantity, numberService.getMathContext());
        if (BigDecimal.ZERO.compareTo(newPcqPlannedQuantity) >= 0) {
            entity.addGlobalError("basicProductionCounting.useReplacement.error.replacesQuantityToBig");
            throw new EntityRuntimeException(entity);
        }

        Entity in = productionCountingQuantity.copy();
        in.setId(null);
        in.setField(ProductionCountingQuantityFields.BATCHES, Lists.newArrayList());
        in.setField(ProductionCountingQuantityFields.USED_QUANTITY, BigDecimal.ZERO);
        in.setField(ProductionCountingQuantityFields.PRODUCTION_COUNTING_ATTRIBUTE_VALUES, Lists.newArrayList());
        in.setField(ProductionCountingQuantityFields.PRODUCT, entity.getBelongsToField(PRODUCT).getId());
        in.setField(ProductionCountingQuantityFields.REPLACEMENT_TO, productionCountingQuantity.getBelongsToField(ProductionCountingQuantityFields.PRODUCT).getId());
        in.setField(ProductionCountingQuantityFields.PLANNED_QUANTITY, plannedQuantity);
        if (ProductionCountingQuantityRole.USED.getStringValue().equals(role)) {
            List<Entity> sections = new ArrayList<>();
            DataDefinition dataDefinition = dataDefinitionService.get(BasicProductionCountingConstants.PLUGIN_IDENTIFIER,
                    BasicProductionCountingConstants.MODEL_SECTION);
            for (Entity originalSection : productionCountingQuantity.getHasManyField(ProductionCountingQuantityFields.SECTIONS)) {
                Entity pcqSection = dataDefinition.create();
                pcqSection.setField(SectionFieldsBPC.QUANTITY, originalSection.getIntegerField(SectionFieldsBPC.QUANTITY));
                pcqSection.setField(SectionFieldsBPC.LENGTH, originalSection.getField(SectionFieldsBPC.LENGTH));
                pcqSection.setField(SectionFieldsBPC.UNIT, originalSection.getField(SectionFieldsBPC.UNIT));
                sections.add(pcqSection);
            }
            in.setField(ProductionCountingQuantityFields.SECTIONS, sections);
        }
        in = in.getDataDefinition().save(in);
        if(!in.isValid()) {
            throw new EntityRuntimeException(in);
        }
        productionCountingQuantity.setField(ProductionCountingQuantityFields.PLANNED_QUANTITY, newPcqPlannedQuantity);
        productionCountingQuantity.getDataDefinition().save(productionCountingQuantity);
    }
}
