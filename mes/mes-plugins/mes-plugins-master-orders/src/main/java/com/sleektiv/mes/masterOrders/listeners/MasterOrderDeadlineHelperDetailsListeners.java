package com.sleektiv.mes.masterOrders.listeners;

import com.google.common.collect.Lists;
import com.sleektiv.mes.masterOrders.constants.MasterOrderFields;
import com.sleektiv.mes.masterOrders.constants.MasterOrdersConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MasterOrderDeadlineHelperDetailsListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void setDeadline(final ViewDefinitionState view, final ComponentState state, final String[] args)
            throws JSONException {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity base = form.getPersistedEntityWithIncludedFormValues();
        Date deadline = base.getDateField(MasterOrderFields.DEADLINE);
        if (deadline == null) {
            view.addMessage("masterOrders.deadlineHelperDetails.message.deadlineMissing", ComponentState.MessageType.FAILURE);
            return;
        }
        String masterOrderIds = view.getJsonContext().get("window.mainTab.form.masterOrderIds").toString();
        List<Long> ids = Lists.newArrayList(masterOrderIds.split(",")).stream().map(Long::valueOf)
                .collect(Collectors.toList());
        DataDefinition masterOrdersDD = dataDefinitionService.get(MasterOrdersConstants.PLUGIN_IDENTIFIER,
                MasterOrdersConstants.MODEL_MASTER_ORDER);
        List<Entity> masterOrders = ids.stream().map(masterOrdersDD::get).collect(Collectors.toList());

        setDeadlinesInMasterOrders(view, masterOrders, deadline);
    }

    @Transactional
    private boolean setDeadlinesInMasterOrders(final ViewDefinitionState view, final List<Entity> masterOrders,
                                               final Date deadline) {
        List<String> wrongMasterOrders = Lists.newArrayList();
        for (Entity masterOrder : masterOrders) {
            masterOrder.setField(MasterOrderFields.DEADLINE, deadline);
            masterOrder = masterOrder.getDataDefinition().save(masterOrder);
            if (!masterOrder.isValid()) {
                wrongMasterOrders.add(masterOrder.getStringField(MasterOrderFields.NUMBER));
            }
        }

        if (!wrongMasterOrders.isEmpty()) {
            view.addMessage("masterOrders.deadlineHelperDetails.message.deadlineCantBeChanged",
                    ComponentState.MessageType.FAILURE, false, String.join(", ", wrongMasterOrders));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        view.addMessage("masterOrders.deadlineHelperDetails.message.success", ComponentState.MessageType.SUCCESS);
        return true;
    }
}
