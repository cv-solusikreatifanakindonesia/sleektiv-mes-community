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
package com.sleektiv.mes.technologies.tree.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.technologies.tree.builder.api.TechnologyTreeAdapter;
import com.sleektiv.mes.technologies.tree.builder.api.TechnologyTreeBuildService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.EntityTree;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.model.api.utils.EntityTreeUtilsService;

@Service
public class TechnologyTreeBuildServiceImpl implements TechnologyTreeBuildService {

    @Autowired
    private TechnologyTreeComponentsFactory componentsFactory;

    @Autowired
    private NumberService numberService;

    @Override
    public <T, P> EntityTree build(final T from, final TechnologyTreeAdapter<T, P> transformer) {
        TechnologyTreeBuilder<T, P> builder = new TechnologyTreeBuilder<T, P>(componentsFactory, transformer);
        Entity root = builder.build(from, numberService);
        return EntityTreeUtilsService.getDetachedEntityTree(Lists.newArrayList(root));
    }
}
