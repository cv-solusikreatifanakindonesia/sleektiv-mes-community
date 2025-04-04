package com.sleektiv.mes.productionPerShift.report.columns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.productionPerShift.report.PPSReportXlsHelper;
import com.sleektiv.model.api.Entity;

@Component("productNameReportColumn")
public class ProductNameReportColumn extends AbstractReportColumn {

    private final PPSReportXlsHelper ppsReportXlsHelper;

    @Autowired
    public ProductNameReportColumn(final TranslationService translationService, final PPSReportXlsHelper ppsReportXlsHelper) {
        super(translationService);
        this.ppsReportXlsHelper = ppsReportXlsHelper;
    }

    @Override
    public String getIdentifier() {
        return "productName";
    }

    @Override
    public Object getValue(final Entity productionPerShift) {
        return ppsReportXlsHelper.getProduct(productionPerShift).getStringField(ProductFields.NAME);
    }

    @Override
    public Object getFirstRowValue(final Entity productionPerShift) {
        return getValue(productionPerShift);
    }

    @Override
    public int getColumnWidth() {
        return 10 * 256;
    }

}
