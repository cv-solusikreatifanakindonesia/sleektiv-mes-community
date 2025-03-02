package com.sleektiv.mes.materialFlowResources.service;

import com.sleektiv.model.api.Entity;

public interface AfterDocumentAcceptListener {

    public void run(Entity document);
}
