package com.sleektiv.mes.basic.controllers.dataProvider;

public abstract class AbstractDataProvider {

    protected abstract String prepareQuery();

    protected abstract String prepareQueryWithLimit(int limit);
}
