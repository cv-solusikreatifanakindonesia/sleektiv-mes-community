package com.sleektiv.mes.productFlowThruDivision.hooks;

import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackingOperationProductInComponentDetailsHooksPFTD {

    public void onBeforeRender(final ViewDefinitionState view) {

        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity topic = form.getEntity();
        Entity topicDb = topic.getDataDefinition().get(topic.getId());
        List<Entity> resourceReservations = topicDb.getHasManyField("resourceReservations");
        if (resourceReservations.isEmpty()) {
            ComponentState resourcesTab = view.getComponentByReference("resourcesTab");
            resourcesTab.setVisible(false);
        }
    }

}