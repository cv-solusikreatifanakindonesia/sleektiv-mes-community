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
package com.sleektiv.mes.masterOrders.hooks;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.sleektiv.mes.masterOrders.constants.MasterOrdersMaterialRequirementFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class MasterOrdersMaterialRequirementHooks {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	public void onSave(final DataDefinition masterOrdersMaterialRequirementDD, final Entity masterOrdersMaterialRequirement) {
		if (checkIfShouldInsertNumber(masterOrdersMaterialRequirement)) {
			masterOrdersMaterialRequirement.setField(MasterOrdersMaterialRequirementFields.NUMBER, setNumberFromSequence());
		}
	}

	private boolean checkIfShouldInsertNumber(final Entity masterOrdersMaterialRequirement) {
		if (Objects.nonNull(masterOrdersMaterialRequirement.getId())) {
			return false;
		}

		return StringUtils.isEmpty(masterOrdersMaterialRequirement.getStringField(MasterOrdersMaterialRequirementFields.NUMBER));
	}

	private String setNumberFromSequence() {
		return jdbcTemplate.queryForObject("SELECT generate_masterordersmaterialrequirement_number()", Maps.newHashMap(),
				String.class);
	}

}
