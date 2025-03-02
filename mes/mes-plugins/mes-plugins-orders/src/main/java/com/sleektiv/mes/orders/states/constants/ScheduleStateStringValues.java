package com.sleektiv.mes.orders.states.constants;

import com.sleektiv.mes.states.aop.RunForStateTransitionAspect;

public final class ScheduleStateStringValues {

    private ScheduleStateStringValues() {

    }

    public static final String WILDCARD_STATE = RunForStateTransitionAspect.WILDCARD_STATE;

    public static final String DRAFT = "01draft";

    public static final String APPROVED = "02approved";

    public static final String REJECTED = "03rejected";

}
