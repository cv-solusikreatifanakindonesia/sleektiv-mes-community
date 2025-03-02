package com.sleektiv.mes.productionPerShift.report.print;

import java.util.List;

import com.sleektiv.mes.productionPerShift.report.columns.ReportColumn;

public interface PPSReportColumnService {

    List<ReportColumn> getReportColumns();
}
