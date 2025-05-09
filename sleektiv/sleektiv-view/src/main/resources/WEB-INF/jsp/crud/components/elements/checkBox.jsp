<%--

    ***************************************************************************
    Copyright (c) 2025 Sleektiv.
    Project: Sleektiv Framework
    Version: 1.4

    This file is part of Sleektiv.

    Sleektiv is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation; either version 3 of the License,
    or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty
    of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
    ***************************************************************************

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<tiles:useAttribute name="component" />

<tiles:insertTemplate template="formComponent.jsp">
	<tiles:putAttribute name="component" value="${component}" />
	<tiles:putAttribute name="componentType" value="checkbox" />
	<tiles:putAttribute name="componentAlign" value="${component['jspOptions']['align']}" />
	<tiles:putAttribute name="componentBody">
		<c:if test="${component['jspOptions']['textRepresentationOnDisabled']}">
			<c:set var="displayHiddenIfTextRepresentationOnDisabled" value="display: none" />
		</c:if>
		<input type="checkbox" id="${component['path']}_input" style="${displayHiddenIfTextRepresentationOnDisabled}" tabindex="${component['indexOrder']}"/>
		<c:if test="${component['jspOptions']['textRepresentationOnDisabled']}">
			<span id="${component['path']}_text" class="component_container_form_textRepresentation">&nbsp;</span>
		</c:if>
		<span id="${component['path']}_textHeight" style="display: inline-block; height: 100%; vertical-align: middle; width: 1px; margin-left: -1px;">&nbsp;</span>
	</tiles:putAttribute>
</tiles:insertTemplate>