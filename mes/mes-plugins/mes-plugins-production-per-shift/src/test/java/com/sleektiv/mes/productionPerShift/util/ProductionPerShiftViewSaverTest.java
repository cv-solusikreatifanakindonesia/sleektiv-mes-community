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
package com.sleektiv.mes.productionPerShift.util;

import com.sleektiv.mes.productionPerShift.PpsDetailsViewAwareTest;
import com.sleektiv.mes.productionPerShift.constants.ProductionPerShiftFields;
import com.sleektiv.mes.productionPerShift.dataProvider.ProgressForDayDataProvider;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.components.AwesomeDynamicListComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionStatus;

import java.util.Collections;

import static com.sleektiv.testing.model.EntityTestUtils.mockEntity;
import static com.sleektiv.testing.model.EntityTestUtils.stubHasManyField;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

public class ProductionPerShiftViewSaverTest extends PpsDetailsViewAwareTest {

    private ProgressPerShiftViewSaver progressPerShiftViewSaver;

    @Mock
    private ProgressForDayDataProvider progressForDayDataProvider;

    @Mock
    private DataDefinition dataDefinition;

    private Entity ppsEntity;

    @Mock
    private AwesomeDynamicListComponent progressForDaysAdl;

    private FormComponent form;

    @Mock
    private TransactionStatus txStatus;

    @Before
    public void init() {
        super.init();

        progressPerShiftViewSaver = new ProgressPerShiftViewSaver();
        ReflectionTestUtils.setField(progressPerShiftViewSaver, "progressForDayDataProvider", progressForDayDataProvider);

        given(dataDefinition.save(any(Entity.class))).willAnswer(new Answer<Entity>() {

            @Override
            public Entity answer(final InvocationOnMock invocation) throws Throwable {
                return (Entity) invocation.getArguments()[0];
            }
        });

        ppsEntity = mockEntity(dataDefinition);
        stubHasManyField(ppsEntity, ProductionPerShiftFields.PLANNED_PROGRESS_CORRECTION_TYPES, Collections.<Entity> emptyList());

        form = mockForm(ppsEntity);

        stubViewComponent("progressForDays", progressForDaysAdl);
        stubViewComponent(SleektivViewConstants.L_FORM, form);

    }

}
