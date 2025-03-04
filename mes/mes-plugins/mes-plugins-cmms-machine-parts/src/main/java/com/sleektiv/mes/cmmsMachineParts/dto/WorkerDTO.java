package com.sleektiv.mes.cmmsMachineParts.dto;

import com.sleektiv.mes.basic.controllers.dataProvider.dto.AbstractDTO;

public class WorkerDTO implements AbstractDTO {

    private Long id;

    private String code;

    public WorkerDTO() {
    }

    public WorkerDTO(Long id, String code) {
        this.id = id;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        WorkerDTO actionDTO = (WorkerDTO) o;

        return code != null ? code.equals(actionDTO.code) : actionDTO.code == null;

    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
