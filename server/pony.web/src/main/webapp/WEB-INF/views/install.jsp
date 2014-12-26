<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>

    <meta charset="utf-8">

    <title><spring:message code="install.title" /></title>

    <script src="./js/lib/jquery.js"></script>

    <link rel="icon" type="image/png" href="./img/favicon.png">
    <link href="./css/pony.css" rel="stylesheet">

    <script>
        (function() {

            var lastFolderId = <c:out value="${fn:length(installCommand.libraryFolders)}" />;
            var folderPathPlaceholder = "<spring:message code="install.folderPathPlaceholder" />";

            function addLibraryFolder() {

                var folderId = 'libraryFolder_' + lastFolderId;

                var $folderInput = $('<div class="input-group">' +
                        '<input type="text" class="form-control">' +
                        '<span class="input-group-btn">' +
                        '<button type="button" class="btn btn-default add">' +
                        '<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>' +
                        '</button>' +
                        '<button type="button" class="btn btn-default remove">' +
                        '<span class="glyphicon glyphicon-minus" aria-hidden="true"></span>' +
                        '</button>' +
                        '</span>' +
                        '</div>');

                $folderInput.attr('id', folderId);
                $folderInput.find('input').attr('placeholder', folderPathPlaceholder);

                $folderInput.find('button.add').click(function() {
                    addLibraryFolder();
                });
                $folderInput.find('button.remove').click(function() {
                    removeLibraryFolder(folderId);
                });

                $('#libraryFolderContainer').append($folderInput);

                enableDisableLibraryFolderRemoval();
                initInputNames();

                lastFolderId++;
            }

            function removeLibraryFolder(folderId) {

                $('#' + folderId).remove();

                enableDisableLibraryFolderRemoval();
                initInputNames();
            }

            function enableDisableLibraryFolderRemoval() {

                var $container = $('#libraryFolderContainer');

                if ($container.children().length > 1) {
                    $container.find('button.remove').removeAttr('disabled');
                } else {
                    $container.find('button.remove').attr('disabled', '');
                }
            }

            function initInputNames() {
                $('#libraryFolderContainer').children().each(function(index) {
                    $(this).find('input').attr('name', 'libraryFolders[' + index + ']')
                });
            }

            $(document).ready(function() {

                var $folders = $('#libraryFolderContainer').children();

                if ($folders.length == 0) {
                    addLibraryFolder();
                } else {
                    $folders.each(function() {

                        var $folderInput = $(this);

                        $folderInput.find('button.add').click(function() {
                            addLibraryFolder();
                        });
                        $folderInput.find('button.remove').click(function() {
                            removeLibraryFolder($folderInput.attr('id'));
                        });
                    });
                }
            });
        })();
    </script>

</head>
<body>

<spring:message code="install.namePlaceholder" var="namePlaceholder" />
<spring:message code="install.emailPlaceholder" var="emailPlaceholder"/>
<spring:message code="install.passwordPlaceholder" var="passwordPlaceholder" />
<spring:message code="install.folderPathPlaceholder" var="folderPathPlaceholder" />

<div class="container">
    <div class="jumbotron installationContainer">

        <h1><spring:message code="install.header" /></h1>

        <p><spring:message code="install.description" /></p>

        <form:form role="form" method="post" commandName="installCommand">

            <form:errors cssClass="alert alert-danger" role="alert" element="div" />

            <spring:bind path="libraryFolders">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <label class="control-label"><spring:message code="install.libraryFolders" /></label>
                    <div id="libraryFolderContainer">
                        <c:forEach items="${installCommand.libraryFolders}" var="folder" varStatus="status">
                            <div id="libraryFolders_${status.index}" class="input-group">
                                <form:input path="libraryFolders[${status.index}]" type="text" class="form-control" placeholder="${folderPathPlaceholder}" />
                                <span class="input-group-btn">
                                    <button type="button" class="btn btn-default add">
                                        <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                                    </button>
                                    <button type="button" class="btn btn-default remove">
                                        <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
                                    </button>
                                </span>
                                <form:errors path="libraryFolders[${status.index}]" cssClass="help-block" />
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </spring:bind>
            <spring:bind path="userName">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <label class="control-label" for="userName"><spring:message code="install.name" /></label>
                    <form:input id="userName" path="userName" type="text" class="form-control" placeholder="${namePlaceholder}" />
                    <form:errors path="userName" cssClass="help-block" />
                </div>
            </spring:bind>
            <spring:bind path="userEmail">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <label class="control-label" for="userEmail"><spring:message code="install.email" /></label>
                    <form:input id="userEmail" path="userEmail" type="text" class="form-control" placeholder="${emailPlaceholder}" />
                    <form:errors path="userEmail" cssClass="help-block" />
                </div>
            </spring:bind>
            <spring:bind path="userPassword">
                <div class="form-group ${status.error ? 'has-error' : ''}">
                    <label class="control-label" for="userPassword"><spring:message code="install.password" /></label>
                    <form:input id="userPassword" path="userPassword" type="password" class="form-control" placeholder="${passwordPlaceholder}" />
                    <form:errors path="userPassword" cssClass="help-block" />
                </div>
            </spring:bind>

            <button type="submit" class="btn btn-primary btn-lg"><spring:message code="install.button" /></button>

        </form:form>

    </div>
</div>

</body>
</html>