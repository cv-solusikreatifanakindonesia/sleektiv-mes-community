package com.sleektiv.mes.cmmsMachineParts.hooks;

import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.basic.constants.UserFieldsB;
import com.sleektiv.mes.cmmsMachineParts.constants.CmmsMachinePartsConstants;
import com.sleektiv.mes.cmmsMachineParts.constants.StaffWorkTimeFields;
import com.sleektiv.mes.cmmsMachineParts.states.constants.MaintenanceEventStateChangeFields;
import com.sleektiv.mes.cmmsMachineParts.states.constants.MaintenanceEventStateStringValues;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchOrders;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.security.constants.SleektivSecurityConstants;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service public class StaffWorkTimeDetailsHooks {

    @Autowired private SecurityService securityService;

    @Autowired private DataDefinitionService dataDefinitionService;

    public void onBeforeRender(final ViewDefinitionState view) {
        fillDefaultValues(view);
    }

    private void fillDefaultValues(final ViewDefinitionState view) {
        Long loggedUser = securityService.getCurrentUserId();
        LookupComponent lookupComponent = (LookupComponent) view.getComponentByReference(StaffWorkTimeFields.WORKER);
        if (lookupComponent.getFieldValue() == null) {
            Entity user = dataDefinitionService.get(SleektivSecurityConstants.PLUGIN_IDENTIFIER, SleektivSecurityConstants.MODEL_USER)
                    .get(loggedUser);
            if (user.getBelongsToField(UserFieldsB.STAFF) != null) {
                lookupComponent.setFieldValue(user.getBelongsToField(UserFieldsB.STAFF).getId());
                lookupComponent.requestComponentUpdateState();
            }

        }
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity event = form.getPersistedEntityWithIncludedFormValues().getBelongsToField(StaffWorkTimeFields.MAINTENANCE_EVNET);

        Optional<Date> from = findDate(event, MaintenanceEventStateStringValues.IN_PROGRESS);
        Optional<Date> to = findDate(event, MaintenanceEventStateStringValues.EDITED);

        FieldComponent dateFrom = (FieldComponent) view
                .getComponentByReference(StaffWorkTimeFields.EFFECTIVE_EXECUTION_TIME_START);
        FieldComponent dateTo = (FieldComponent) view.getComponentByReference(StaffWorkTimeFields.EFFECTIVE_EXECUTION_TIME_END);

        if (from.isPresent() && dateFrom.getFieldValue() == null) {
            dateFrom.setFieldValue(DateUtils.toDateTimeString(from.get()));
            dateFrom.requestComponentUpdateState();
        }

        if (to.isPresent() && dateTo.getFieldValue() == null) {
            dateTo.setFieldValue(DateUtils.toDateTimeString(to.get()));
            dateTo.requestComponentUpdateState();
            FieldComponent laborTimeFieldComponent = (FieldComponent) view
                    .getComponentByReference(StaffWorkTimeFields.LABOR_TIME);
            calculateLaborTime(laborTimeFieldComponent, from, to);
        }

    }

    private void calculateLaborTime(FieldComponent laborTimeFieldComponent, Optional<Date> from, Optional<Date> to) {

        if (from.isPresent() && to.isPresent() && from.get().before(to.get())) {
            Seconds seconds = Seconds.secondsBetween(new DateTime(from.get()), new DateTime(to.get()));
            laborTimeFieldComponent.setFieldValue(Integer.valueOf(seconds.getSeconds()));
            laborTimeFieldComponent.requestComponentUpdateState();
        }
    }

    private Optional<Date> findDate(final Entity event, String state) {

        SearchCriteriaBuilder sb = dataDefinitionService
                .get(CmmsMachinePartsConstants.PLUGIN_IDENTIFIER, CmmsMachinePartsConstants.MODEL_MAINTENANCE_EVENT_STATE_CHANGE)
                .find();

        sb.add(SearchRestrictions
                .belongsTo(MaintenanceEventStateChangeFields.MAINTENANCE_EVENT, CmmsMachinePartsConstants.PLUGIN_IDENTIFIER,
                        CmmsMachinePartsConstants.MODEL_MAINTENANCE_EVENT, event.getId()));
        sb.add(SearchRestrictions.eq(MaintenanceEventStateChangeFields.TARGET_STATE, state));
        sb.add(SearchRestrictions.eq(MaintenanceEventStateChangeFields.STATUS, "03successful"));
        sb.addOrder(SearchOrders.asc(MaintenanceEventStateChangeFields.DATE_AND_TIME));
        sb.setMaxResults(1);
        Entity result = sb.uniqueResult();
        if (result == null) {
            return Optional.ofNullable(null);
        }

        return Optional.ofNullable(result.getDateField(MaintenanceEventStateChangeFields.DATE_AND_TIME));
    }

}