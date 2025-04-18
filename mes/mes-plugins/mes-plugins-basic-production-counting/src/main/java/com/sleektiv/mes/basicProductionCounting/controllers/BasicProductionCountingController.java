/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
 * Version: 1.4
 *
 * This file is part of Sleektiv.
 *
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.basicProductionCounting.controllers;

import com.sleektiv.mes.basicProductionCounting.constants.BasicProductionCountingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BasicProductionCountingController {

    @RequestMapping(value = BasicProductionCountingConstants.PLUGIN_IDENTIFIER + "/manifestoReport.pdf", method = RequestMethod.GET)
    public final ModelAndView manifestoReportPdf(@RequestParam("id") final String id, @RequestParam("fromOrder") final String fromOrder) {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("manifestoReportPdf");
        mav.addObject("id", id);
        mav.addObject("fromOrder", fromOrder);

        return mav;
    }

}
