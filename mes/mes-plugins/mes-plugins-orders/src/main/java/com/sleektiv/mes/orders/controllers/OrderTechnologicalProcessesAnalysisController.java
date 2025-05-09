package com.sleektiv.mes.orders.controllers;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sleektiv.mes.basic.controllers.dataProvider.dto.ColumnDTO;
import com.sleektiv.mes.basic.services.ExportToCsvServiceB;
import com.sleektiv.mes.orders.controllers.dataProvider.OrderTechnologicalProcessesAnalysisDataProvider;

@Controller
@RequestMapping("/orderTechnologicalProcessesAnalysis")
public class OrderTechnologicalProcessesAnalysisController {

    @Autowired
    private OrderTechnologicalProcessesAnalysisDataProvider orderTechnologicalProcessesAnalysisDataProvider;

    @Autowired
    private ExportToCsvServiceB exportToCsvServiceB;

    @ResponseBody
    @RequestMapping(value = "/columns", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ColumnDTO> getColumns(final Locale locale) {
        return orderTechnologicalProcessesAnalysisDataProvider.getColumns(locale);
    }

    @ResponseBody
    @RequestMapping(value = "/validate", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String validate(@RequestParam String dateFrom, @RequestParam String dateTo) throws ParseException {
        return orderTechnologicalProcessesAnalysisDataProvider.validate(dateFrom, dateTo);
    }

    @ResponseBody
    @RequestMapping(value = "/records", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getRecords(@RequestParam String dateFrom, @RequestParam String dateTo) {
        try {
            return orderTechnologicalProcessesAnalysisDataProvider.getRecords(dateFrom, dateTo, new JSONObject(), "",
                    false);
        } catch (JSONException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/exportToCsv", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object exportToCsv(@RequestBody final JSONObject data, final Locale locale) {
        return exportToCsvServiceB.prepareJsonForCsv(orderTechnologicalProcessesAnalysisDataProvider, "orderTechnologicalProcessesAnalysis", data, locale);
    }

}
