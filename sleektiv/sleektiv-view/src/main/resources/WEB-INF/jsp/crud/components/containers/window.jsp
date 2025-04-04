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

<div class="windowContainer">
	<c:if test="${component.jspOptions['hasRibbon']}">
		<div class="windowContainerRibbon tabWindowRibbon">
			<div id="${component['path']}_windowContainerRibbon">
			</div>
		</div>
	</c:if>
	<div class="windowContainerContentBody" id="${component['path']}_windowContainerContentBody">
		<div id="${component['path']}_windowContainerContentBodyWidthMarker" style=" z-index: 5000;"></div>
		
		<div class="windowContent<c:if test="${not empty component['helpPath']}"> displayingHelpPaths</c:if>" id="${component['path']}_windowContent">
			<c:if test="${not empty component['helpPath']}">
				<div class="contextualHelpPath">
					<span><c:out value="${component['helpPath']}" /></span>
				</div>
			</c:if>
			<c:if test="${component.jspOptions['header']}">
				<div class="tabWindowHeader <c:if test="${component.jspOptions['oneTab']}">noTabs</c:if>" id="${component['path']}_windowHeader"></div>
			</c:if>
			<c:if test="${! component.jspOptions['oneTab']}">
				<div id="${component['path']}_windowTabs" class="windowTabs">
					<div></div>
				</div>
			</c:if>
			<div class="tabWindowComponents" id="${component['path']}_windowComponents">
				<c:forEach items="${component['children']}" var="component">
					<tiles:insertTemplate template="../component.jsp">
						<tiles:putAttribute name="component" value="${component.value}" />
					</tiles:insertTemplate>
				</c:forEach>
			</div>
			<div style="clear: both;"></div>
		</div>
	</div>
</div>