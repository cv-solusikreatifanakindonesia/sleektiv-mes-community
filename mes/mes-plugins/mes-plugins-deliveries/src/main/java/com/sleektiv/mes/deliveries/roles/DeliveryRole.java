package com.sleektiv.mes.deliveries.roles;

import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

public enum DeliveryRole {

    ROLE_DELIVERIES_STATES_DECLINE {
        @Override
        public void processRole(final ViewDefinitionState view) {
            lockFromRibbonGroup(view, STATUS, "declineDelivery");
        }
    },

    ROLE_DELIVERIES_STATES_OTHER {
        @Override
        public void processRole(final ViewDefinitionState view) {
            lockFromRibbonGroup(view, STATUS, "accept");
        }
    },

    ROLE_DELIVERIES_STATES_APPROVE {
        @Override
        public void processRole(final ViewDefinitionState view) {
            lockFromRibbonGroup(view, STATUS, "approveDelivery");
        }
    },

    ROLE_DELIVERIES_STATES_ACCEPT {
        @Override
        public void processRole(final ViewDefinitionState view) {
            lockFromRibbonGroup(view, STATUS, "approveDelivery");
        }
    },

    ROLE_DELIVERIES_EDIT {
        @Override
        public void processRole(final ViewDefinitionState view) {
            lockFromRibbonGroup(view, "actions", "delete");
        }
    };

    public static final String STATUS = "status";

    public void processRole(final ViewDefinitionState view) {

    }

    protected void lockFromRibbonGroup(ViewDefinitionState view, String groupName, String... items) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        Ribbon ribbon = window.getRibbon();
        RibbonGroup ribbonGroup = ribbon.getGroupByName(groupName);
        if (ribbonGroup != null) {
            for (String item : items) {
                RibbonActionItem ribbonItem = ribbonGroup.getItemByName(item);
                if (ribbonItem != null) {
                    ribbonItem.setEnabled(false);
                    ribbonItem.requestUpdate(true);
                }
            }
        }
    }
}
