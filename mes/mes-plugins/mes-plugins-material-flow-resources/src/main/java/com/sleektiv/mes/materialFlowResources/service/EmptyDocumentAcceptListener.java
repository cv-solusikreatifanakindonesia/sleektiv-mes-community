package com.sleektiv.mes.materialFlowResources.service;

import com.sleektiv.model.api.Entity;
import org.springframework.stereotype.Service;

@Service
public class EmptyDocumentAcceptListener implements AfterDocumentAcceptListener {

    public void run(Entity document) {

    }
}
