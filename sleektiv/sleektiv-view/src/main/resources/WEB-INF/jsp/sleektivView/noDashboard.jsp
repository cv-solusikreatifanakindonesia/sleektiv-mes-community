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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

    <head>
        <c:choose>
            <c:when test="${useCompressedStaticResources}">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/sleektivView/public/sleektiv-min.css?ver=${buildNumber}" type="text/css" />
                <link rel="stylesheet" href="${pageContext.request.contextPath}/sleektivView/public/css/custom.css?ver=${buildNumber}" type="text/css" />
            </c:when>
            <c:otherwise>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/sleektivView/public/css/core/dashboard.css?ver=${buildNumber}" type="text/css" />
                <link rel="stylesheet" href="${pageContext.request.contextPath}/sleektivView/public/css/core/menu/style.css?ver=${buildNumber}" type="text/css" />
                <link rel="stylesheet" href="${pageContext.request.contextPath}/sleektivView/public/css/custom.css?ver=${buildNumber}" type="text/css" />
            </c:otherwise>
        </c:choose>
    </head>

    <body>
        <div id="windowContainer">
            <div id="windowContainerContentBody">
                <div id="contentWrapperMiddle">
                <div id="dashboardContentWrapper">
                    <div id="descriptionElement">
                        <div id="descriptionHeader">
                            ${translationsMap['sleektivView.noDashboardPage.header']}
                        </div>
                    </div>
                </div>
                </div>
            </div>
        </div>
    </body>

</html>