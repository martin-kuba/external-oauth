<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" session="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="my" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<my:pagetemplate>
<jsp:attribute name="body">


    <div class="jumbotron">
        <h1>OAuth test</h1>
        <p class="lead">Here you can log in using OAuth</p>

        <p><my:a class="btn btn-lg btn-default" href="/google/login">Google</my:a></p>
        <p><my:a class="btn btn-lg btn-default" href="/facebook/login">Facebook</my:a></p>
        <p><my:a class="btn btn-lg btn-default" href="/linkedin/login">LinkedIn</my:a></p>


    </div>

</jsp:attribute>
</my:pagetemplate>