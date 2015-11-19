<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<my:pagetemplate title="Authenticated user">
<jsp:attribute name="body">

<table class="table">
    <tr>
        <th>provider</th>
        <td><c:out value="${user.provider}"/></td>
    </tr>
    <tr>
        <th>user id</th>
        <td><c:out value="${user.id}"/></td>
    </tr>
    <tr>
        <th>email</th>
        <td><c:out value="${user.email}"/></td>
    </tr>
    <tr>
        <th>given name</th>
        <td><c:out value="${user.givenName}"/></td>
    </tr>
    <tr>
        <th>surname</th>
        <td><c:out value="${user.surname}"/></td>
    </tr>
    <tr>
        <th>full name</th>
        <td><c:out value="${user.fullname}"/></td>
    </tr>
    <tr>
        <th>picture</th>
        <td><img src="${user.pictureURL}" /></td>
    </tr>
</table>

</jsp:attribute>
</my:pagetemplate>
