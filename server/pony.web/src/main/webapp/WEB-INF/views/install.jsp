<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%--@elvariable id="error" type="java.lang.String"--%>

<!DOCTYPE html>
<html>
<head>

    <meta charset="utf-8">

    <title>Pony Installation</title>

    <link href="./css/pony.css" rel="stylesheet">

</head>
<body>

<div class="container">
    <div class="jumbotron installationContainer">

        <h1>Pony Installation</h1>

        <c:if test="${error != null}"><div class="alert alert-danger" role="alert">${error}</div></c:if>

        <p>Click "Install" button to start Pony installation!</p>
        <form role="form" method="post">
            <button type="submit" class="btn btn-primary btn-lg">Install</button>
        </form>

    </div>
</div>

</body>
</html>