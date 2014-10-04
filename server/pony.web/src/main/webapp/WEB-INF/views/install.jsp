<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%--@elvariable id="error" type="java.lang.String"--%>

<!DOCTYPE html>
<html>
<head>

    <meta charset="utf-8">

    <title><spring:message code="install.title" /></title>

    <link href="./css/pony.css" rel="stylesheet">

</head>
<body>

<div class="container">
    <div class="jumbotron installationContainer">

        <h1><spring:message code="install.header" /></h1>

        <c:if test="${error != null}"><div class="alert alert-danger" role="alert">${error}</div></c:if>

        <p><spring:message code="install.description" /></p>
        <form role="form" method="post">
            <button type="submit" class="btn btn-primary btn-lg"><spring:message code="install.button" /></button>
        </form>

    </div>
</div>

</body>
</html>