package com.sleektiv.mes.productionCounting.listeners;

import com.google.common.base.Strings;
import com.sleektiv.mes.productionCounting.constants.ProductionCountingConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.grid.GridComponentFilterSQLUtils;
import com.sleektiv.view.api.components.grid.GridComponentMultiSearchFilter;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class BeforeAdditionalActionsAnalysisGeneratorListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private NumberService numberService;

    public void calculateTotalQuantity(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        String query = buildQuery();

        Map<String, String> filter = grid.getFilters();
        GridComponentMultiSearchFilter multiSearchFilter = grid.getMultiSearchFilter();
        String filterQ;
        try {
            filterQ = GridComponentFilterSQLUtils.addFilters(filter, grid.getColumns(),
                    "productioncounting_beforeadditionalactionsanalysisentry", dataDefinitionService.get(
                            ProductionCountingConstants.PLUGIN_IDENTIFIER,
                            ProductionCountingConstants.MODEL_BEFORE_ADDITIONAL_ACTIONS_ANALYSIS_ENTRY));

            String multiFilterQ = GridComponentFilterSQLUtils.addMultiSearchFilter(multiSearchFilter, grid.getColumns(),
                    "productioncounting_beforeadditionalactionsanalysisentry", dataDefinitionService.get(
                            ProductionCountingConstants.PLUGIN_IDENTIFIER,
                            ProductionCountingConstants.MODEL_BEFORE_ADDITIONAL_ACTIONS_ANALYSIS_ENTRY));
            if (!Strings.isNullOrEmpty(multiFilterQ)) {
                if (!Strings.isNullOrEmpty(filterQ))
                    filterQ += " AND ";
                filterQ += multiFilterQ;
            }

        } catch (Exception e) {
            filterQ = "";
        }

        if (!Strings.isNullOrEmpty(filterQ)) {
            query = query + " WHERE " + filterQ;
        }
        Map<String, Object> values = jdbcTemplate.queryForMap(query, Collections.emptyMap());

        FieldComponent totalQuantity = (FieldComponent) view.getComponentByReference("totalQuantity");
        totalQuantity.setFieldValue(numberService.format(values.get("totalDoneQuantity")));
        totalQuantity.requestComponentUpdateState();

    }

    private String buildQuery() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT SUM(doneQuantity) AS totalDoneQuantity ");
        query.append("FROM productioncounting_beforeadditionalactionsanalysisentry ");
        return query.toString();
    }
}
