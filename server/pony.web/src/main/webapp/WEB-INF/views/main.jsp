<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>

    <meta charset="utf-8">

    <title><spring:message code="main.title" /></title>

    <link rel="icon" type="image/png" href="./img/favicon.png">

    <style>
        body {
            margin: 0;
            padding: 0;
        }
        #loadingContainer {
            text-align: center;
            padding-left: 20px;
            padding-right: 20px;
            margin-top: 10px;
            margin-bottom: 10px;
            line-height: 15px;
            color: #333;
            font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
            font-size: 14px;
            font-weight: 500;
        }
    </style>

    <script type="text/javascript" src="./js/lib/UnityShim.js"></script>

    <script type="text/javascript" src="./pony/pony.nocache.js"></script>

</head>
<body>

<div id="loadingContainer" class="container"><spring:message code="main.loading" /></div>

<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>

</body>
</html>