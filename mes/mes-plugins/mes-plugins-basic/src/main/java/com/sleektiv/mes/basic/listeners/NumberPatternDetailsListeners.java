package com.sleektiv.mes.basic.listeners;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.constants.NumberPatternElement;
import com.sleektiv.mes.basic.constants.NumberPatternElementFields;
import com.sleektiv.mes.basic.constants.NumberPatternFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NumberPatternDetailsListeners {

    @Autowired
    private TranslationService translationService;

    public void generatePatternFromElements(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity numberPattern = form.getPersistedEntityWithIncludedFormValues();
        List<Entity> elements = numberPattern.getHasManyField(NumberPatternFields.NUMBER_PATTERN_ELEMENTS);
        StringBuilder stringBuilder = new StringBuilder();
        for (Entity element : elements) {
            if (NumberPatternElement.XX.getStringValue().equals(element.getStringField(NumberPatternElementFields.ELEMENT))) {
                stringBuilder.append(element.getStringField(NumberPatternElementFields.VALUE));
            } else {
                String value = translationService.translate(
                        "basic.numberPatternElement.element.value." + element.getStringField(NumberPatternElementFields.ELEMENT),
                        view.getLocale());
                stringBuilder.append(value.substring(0, value.indexOf(' ')));
            }
        }
        numberPattern.setField(NumberPatternFields.PATTERN, stringBuilder.toString());
        numberPattern = numberPattern.getDataDefinition().save(numberPattern);
        form.setEntity(numberPattern);
    }
}
