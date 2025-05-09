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
package com.sleektiv.mes.deliveries.controllers;

import com.google.common.io.Files;
import com.sleektiv.mes.basic.MultiUploadHelper;
import com.sleektiv.mes.deliveries.constants.DeliveriesConstants;
import com.sleektiv.mes.deliveries.constants.DeliveryAttachmentFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.model.api.file.FileService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;

@Controller
@RequestMapping("/deliveries")
public class DeliveriesMultiUploadController {

    private static final Logger logger = LoggerFactory.getLogger(DeliveriesMultiUploadController.class);

    @Autowired private FileService fileService;

    @Autowired private DataDefinitionService dataDefinitionService;

    @Autowired private NumberService numberService;

    private static final Integer L_SCALE = 2;

    @ResponseBody @RequestMapping(value = "/multiUploadFiles", method = RequestMethod.POST) public void upload(
            MultipartHttpServletRequest request, HttpServletResponse response) {
        Long deliveryId = Long.parseLong(request.getParameter("techId"));
        Entity delivery = dataDefinitionService.get(DeliveriesConstants.PLUGIN_IDENTIFIER, DeliveriesConstants.MODEL_DELIVERY)
                .get(deliveryId);
        DataDefinition attachmentDD = dataDefinitionService
                .get(DeliveriesConstants.PLUGIN_IDENTIFIER, DeliveriesConstants.MODEL_DELIVERY_ATTACHMENT);

        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;

        while (itr.hasNext()) {

            mpf = request.getFile(itr.next());

            String path = "";
            try {
                path = fileService.upload(mpf);
            } catch (IOException e) {
                logger.error("Unable to upload attachment.", e);
            }
            if (MultiUploadHelper.EXTS.contains(Files.getFileExtension(path).toUpperCase())) {
                Entity atchment = attachmentDD.create();
                atchment.setField(DeliveryAttachmentFields.ATTACHMENT, path);
                atchment.setField(DeliveryAttachmentFields.NAME, mpf.getOriginalFilename());
                atchment.setField(DeliveryAttachmentFields.DELIVERY, delivery);
                atchment.setField(DeliveryAttachmentFields.EXT, Files.getFileExtension(path));
                BigDecimal fileSize = new BigDecimal(mpf.getSize(), numberService.getMathContext());
                BigDecimal divider = new BigDecimal(1024, numberService.getMathContext());
                BigDecimal size = fileSize.divide(divider, L_SCALE, BigDecimal.ROUND_HALF_UP);
                atchment.setField(DeliveryAttachmentFields.SIZE, size);
                attachmentDD.save(atchment);
            }
        }
    }

    @RequestMapping(value = "/getAttachment.html", method = RequestMethod.GET) public final void getAttachment(
            @RequestParam("id") final Long[] ids, HttpServletResponse response) {
        DataDefinition attachmentDD = dataDefinitionService
                .get(DeliveriesConstants.PLUGIN_IDENTIFIER, DeliveriesConstants.MODEL_DELIVERY_ATTACHMENT);
        Entity attachment = attachmentDD.get(ids[0]);
        InputStream is = fileService.getInputStream(attachment.getStringField(DeliveryAttachmentFields.ATTACHMENT));

        try {
            if (is == null) {
                response.sendRedirect("/error.html?code=404");
            }

            response.setHeader("Content-disposition",
                    "inline; filename=" + attachment.getStringField(DeliveryAttachmentFields.NAME));
            response.setContentType(
                    fileService.getContentType(attachment.getStringField(DeliveryAttachmentFields.ATTACHMENT)));

            int bytes = IOUtils.copy(is, response.getOutputStream());
            response.setContentLength(bytes);

            response.flushBuffer();

        } catch (IOException e) {
            logger.error("Unable to copy attachment file to response stream.", e);
        }
    }
}
