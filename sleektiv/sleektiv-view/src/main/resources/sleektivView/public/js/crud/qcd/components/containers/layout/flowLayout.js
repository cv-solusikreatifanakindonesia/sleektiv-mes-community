/*
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
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
var QCD = QCD || {};
QCD.components = QCD.components || {};
QCD.components.containers = QCD.components.containers || {};
QCD.components.containers.layout = QCD.components.containers.layout || {};

QCD.components.containers.layout.FlowLayout = function(_element, _mainController) {
	$.extend(this, new QCD.components.containers.layout.Layout(_element, _mainController));

	function constructor(_this) {
		_this.constructChildren(_this.getLayoutChildren());
	}
	
	this.getLayoutChildren = function() {
		return $("#"+this.elementSearchName+"_layoutComponents").children();
	}
	
	this.updateSize = function(_width, _height) {
		for (var i in this.components) {
			this.components[i].updateSize(_width, _height);
		}
	}
	
	constructor(this);
}