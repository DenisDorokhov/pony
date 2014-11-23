<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%--@elvariable id="error" type="java.lang.String"--%>

<!DOCTYPE html>
<html>
<head>

    <meta charset="utf-8">
    <title><spring:message code="install.title" /></title>
    <script src="./js/lib/jquery.js"></script>
    <link href="./css/pony.css" rel="stylesheet">

    <script>

        var lastFolderId = 0;
        var folderPathPlaceholder = "<spring:message code="install.folderPathPlaceholder" />";

        function addLibraryFolder() {

            lastFolderId++;

            var folderId = 'libraryFolder_' + lastFolderId;

            var $folderInput = $('<div class="input-group">' +
                    '<input name="libraryFolder[]" type="text" class="form-control">' +
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
            $folderInput.find('.form-control').attr('placeholder', folderPathPlaceholder);

            $folderInput.find('button.add').click(function() {
                addLibraryFolder();
            });
            $folderInput.find('button.remove').click(function() {
                removeLibraryFolder(folderId);
            });

            $('#libraryFolderContainer').append($folderInput);

            enableDisableLibraryFolderRemoval();
        }

        function removeLibraryFolder(folderId) {

            $('#' + folderId).remove();

            enableDisableLibraryFolderRemoval();
        }

        function enableDisableLibraryFolderRemoval() {

            var $container = $('#libraryFolderContainer');

            if ($container.children().length > 1) {
                $container.find('button.remove').removeAttr('disabled');
            } else {
                $container.find('button.remove').attr('disabled', '');
            }
        }

        $(document).ready(function() {
            addLibraryFolder();
        });

    </script>

</head>
<body>

<div class="container">
    <div class="jumbotron installationContainer">

        <h1><spring:message code="install.header" /></h1>

        <c:if test="${error != null}"><div class="alert alert-danger" role="alert">${error}</div></c:if>

        <p><spring:message code="install.description" /></p>

        <form role="form" method="post">
            <div class="form-group">
                <div class="form-group">
                    <label><spring:message code="install.libraryFolders" /></label>
                    <div id="libraryFolderContainer"></div>
                </div>
                <div class="form-group">
                    <label for="adminLogin"><spring:message code="install.adminLogin" /></label>
                    <input id="adminLogin" name="adminLogin" type="text" class="form-control" placeholder="<spring:message code="install.adminLoginPlaceholder" />">
                </div>
                <div class="form-group">
                    <label for="adminPassword"><spring:message code="install.adminPassword" /></label>
                    <input id="adminPassword" name="adminPassword" type="password" class="form-control" placeholder="<spring:message code="install.adminPasswordPlaceholder" />">
                </div>
            </div>
            <button type="submit" class="btn btn-primary btn-lg"><spring:message code="install.button" /></button>
        </form>

    </div>
</div>

</body>
</html>