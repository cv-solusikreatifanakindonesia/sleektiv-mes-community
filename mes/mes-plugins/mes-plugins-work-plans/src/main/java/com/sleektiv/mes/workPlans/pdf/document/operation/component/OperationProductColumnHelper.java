package com.sleektiv.mes.workPlans.pdf.document.operation.component;

import com.sleektiv.mes.columnExtension.constants.ColumnAlignment;

public class OperationProductColumnHelper {

    private final ColumnAlignment columnAlignment;

    private final String value;

    private final String identifier;

    public OperationProductColumnHelper(final ColumnAlignment columnAlignment, final String value, final String identifier) {
        this.columnAlignment = columnAlignment;
        this.value = value;
        this.identifier = identifier;
    }

    public ColumnAlignment getColumnAlignment() {
        return columnAlignment;
    }

    public String getValue() {
        return value;
    }

    public String getIdentifier() {
        return identifier;
    }

}
