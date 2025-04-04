var QCD = QCD || {};

QCD.orderTechnologicalProcessesAnalysis = (function() {
    let localStorageKey = 'sleektiv-orders-orderTechnologicalProcessesAnalysis';
    let grid;
    let dataView;
    let options = {
        enableCellNavigation: true,
        showHeaderRow: true,
        headerRowHeight: 30,
        explicitInitialization: true,
        autosizeColsMode: Slick.GridAutosizeColsMode.FitColsToViewport,
        enableTextSelectionOnCells: true,
        createFooterRow: true,
        showFooterRow: true,
        footerRowHeight: 21
    };
    let pagerOptions = {
        showAllText: QCD.translate('sleektivView.slickGrid.pager.showAllText'),
        showPageText: QCD.translate('sleektivView.slickGrid.pager.showPageText'),
        show: QCD.translate('sleektivView.slickGrid.pager.show'),
        all: QCD.translate('sleektivView.slickGrid.pager.all')
    };
    let columnFilters = {};

    function numberFormatter(row, cell, value, columnDef, dataContext) {
        if (value) {
            value = Math.round(value * 100) / 100;
            let parts = value.toString().split(".");
            parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, " ");

            return parts.join(",");
        } else {
            return value;
        }
    }

    function filter(item) {
        for (let columnId in columnFilters) {
            if (columnId !== undefined && columnFilters[columnId] !== "") {
                let c = grid.getColumns()[grid.getColumnIndex(columnId)];

                if (item[c.field] === undefined || item[c.field] === null ||
                    item[c.field].toString().toUpperCase().indexOf(columnFilters[columnId].toUpperCase()) < 0) {

                    return false;
                }
            }
        }

        return true;
    }

    /** Calculate the total of an existing field from the datagrid
     * @param object aggregator
     * @return mixed total
     */
    function CalculateTotalByAggregator(agg, dataview) {
        var constructorName = agg.constructor.name; // get constructor name, ex.: SumAggregator
        var type = constructorName.replace(/aggregator/gi, '').toLowerCase(); // remove the word Aggregator and make it lower case, ex.: SumAggregator -> sum

        var totals = new Slick.GroupTotals();
        var fn = compileAccumulatorLoop(agg);

        fn.call(agg, dataview.getFilteredItems());
        agg.storeResult(totals);

        return totals[type][agg.field_];
    }

    /** This function comes from SlickGrid DataView but was slightly adapted for our usage */
    function compileAccumulatorLoop(aggregator) {
        aggregator.init();

        var accumulatorInfo = getFunctionInfo(aggregator.accumulate);
        var fn = new Function(
            "_items",
            " for (var " + accumulatorInfo.params[0] + ", _i=0, _il=_items.length; _i<_il; _i++) {" +
            accumulatorInfo.params[0] + " = _items[_i]; " +
            accumulatorInfo.body +
            "}"
        );

        fn.displayName = "compiledAccumulatorLoop";

        return fn;
    }

    /** This function comes from Slick DataView, but since it's a private function, you will need to copy it in your own code */
    function getFunctionInfo(fn) {
        var fnRegex = /^function[^(]*\(([^)]*)\)\s*{([\s\S]*)}$/;
        var matches = fn.toString().match(fnRegex);

        return {
            params: matches[1].split(","),
            body: matches[2]
        };
    }

    function updateAllTotals(grid, dataView) {
        let workTimeInSecondsSum = CalculateTotalByAggregator(new Slick.Data.Aggregators.Sum("workTimeInSeconds"), dataView);
        let valueSum = CalculateTotalByAggregator(new Slick.Data.Aggregators.Sum("value"), dataView);

        updateTotalRowValue(grid, "workTime", formatTime(workTimeInSecondsSum));
        updateTotalRowValue(grid, "value", numberFormatter(null, null, valueSum));
    }

    function formatTime(seconds) {
        const h = Math.floor(seconds / 3600);
        const m = Math.floor((seconds % 3600) / 60);
        const s = Math.round(seconds % 60);

        return [
            h,
            m > 9 ? m : (h ? '0' + m : m || '0'),
            s > 9 ? s : '0' + s
        ].filter(Boolean).join(':');
    }

    function updateTotalRowValue(grid, columnId, total) {
        let columnElement = grid.getFooterRowColumn(columnId);

        $(columnElement).html(total);
    }

    function init() {
        let params = {
            dateFrom: document.getElementById("window.mainTab.form.gridLayout.dateFrom_input").value,
            dateTo: document.getElementById("window.mainTab.form.gridLayout.dateTo_input").value
        };

        QCD.components.elements.utils.LoadingIndicator.blockElement($('body'));

        $.get("/rest/orderTechnologicalProcessesAnalysis/validate", params, function(message) {
            if (message) {
                QCD.components.elements.utils.LoadingIndicator.unblockElement($('body'));

                new QCD.MessagesController().addMessage({
                    type: 'failure',
                    title: QCD.translate('sleektivView.notification.failure'),
                    content: QCD.translate(message),
                    autoClose: false,
                    extraLarge: false
                });
            } else {
                mainController.getComponentByReferenceName('window').setActiveTab('dataTab');

                $.get("/rest/orderTechnologicalProcessesAnalysis/columns", function(columns) {
                    $('#orderTechnologicalProcessesAnalysisGrid').height($('#window_windowContent').height() - 80);
                    $('#orderTechnologicalProcessesAnalysisGrid').width($('#window_windowContent').width() - 20);

                    for (let i = 0; i < columns.length; i++) {
                        columns[i].field = columns[i].id;
                        columns[i].toolTip = columns[i].name;
                        columns[i].sortable = true;
                        columns[i].autoSize = {
                            ignoreHeaderText: true
                        };

                        if (columns[i].dataType === '02numeric') {
                            columns[i].cssClass = 'right-align';
                            columns[i].formatter = numberFormatter;
                        }

                        if(columns[i].id === 'tj' || columns[i].id === 'workTime'){
                            columns[i].cssClass = 'right-align';
                        }
                    }

                    let dataView = new Slick.Data.DataView();

                    grid = new Slick.Grid("#orderTechnologicalProcessesAnalysisGrid", dataView, columns, options);

                    new Slick.Controls.Pager(dataView, grid, $("#pager"), pagerOptions);

                    dataView.onRowCountChanged.subscribe(function(e, args) {
                        grid.updateRowCount();
                        grid.render();
                    });

                    dataView.onRowsChanged.subscribe(function(e, args) {
                        grid.invalidateRows(args.rows);
                        grid.render();

                        updateAllTotals(grid, dataView);
                    });

                    dataView.onPagingInfoChanged.subscribe(function(e, pagingInfo) {
                        grid.updatePagingStatusFromView(pagingInfo);
                    });

                    $(grid.getHeaderRow()).on("change keyup", ":input", function(e) {
                        let columnId = $(this).data("columnId");

                        if (columnId != null) {
                            columnFilters[columnId] = $.trim($(this).val());

                            dataView.refresh();
                        }
                    });

                    grid.onHeaderRowCellRendered.subscribe(function(e, args) {
                        $(args.node).empty();

                        $("<input type='text'>")
                            .data("columnId", args.column.id)
                            .val(columnFilters[args.column.id])
                            .appendTo(args.node);
                    });

                    grid.onSort.subscribe(function(e, args) {
                        let comparator = function(a, b) {
                            if (a[args.sortCol.field] === b[args.sortCol.field]) {
                                return 0;
                            } else if (a[args.sortCol.field] === undefined || a[args.sortCol.field] === null) {
                                return 1;
                            } else if (b[args.sortCol.field] === undefined || b[args.sortCol.field] === null) {
                                return -1;
                            } else {
                                return a[args.sortCol.field] < b[args.sortCol.field] ? -1 : 1;
                            }
                        };

                        dataView.sort(comparator, args.sortAsc);
                    });

                    grid.onColumnsReordered.subscribe(function(e, args) {
                        updateAllTotals(grid, dataView);
                    });

                    $.get("/rest/orderTechnologicalProcessesAnalysis/records", params, function(records) {
                        grid.init();
                        grid.autosizeColumns();

                        dataView.beginUpdate();
                        dataView.setItems(records);
                        dataView.setFilter(filter);
                        dataView.endUpdate();

                        updateAllTotals(grid, dataView);

                        $('.slick-header-columns').children().eq(0).trigger('click');

                        QCD.components.elements.utils.LoadingIndicator.unblockElement($('body'));
                    }, 'json');
                }, 'json');
            }
        }, 'text');
    }

    function exportToCsv() {
        let params = {
            dateFrom: document.getElementById("window.mainTab.form.gridLayout.dateFrom_input").value,
            dateTo: document.getElementById("window.mainTab.form.gridLayout.dateTo_input").value,
            columns: grid.getColumns(),
            sort: grid.getSortColumns(),
            filters: columnFilters
        };

        $.ajax({
            url: "/rest/orderTechnologicalProcessesAnalysis/exportToCsv",
            type: 'POST',
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(params),
            success: function(response) {
                let contextPath = window.location.protocol + "//" + window.location.host;
                let redirectUrl = response.url.replace(/\$\{root\}/, contextPath);

                window.open(redirectUrl, "_blank", "status=0");
            }
        });
    }

    return {
        loadData: init,
        exportToCsv: exportToCsv
    }

})();
