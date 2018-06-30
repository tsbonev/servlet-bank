<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <title>${title}</title>
    <link rel="stylesheet" href="css/content/bootstrap.min.css">
    <link rel="stylesheet" href="css/content/app.css">
</head>
<body>
<div class="navbar navbar-inverse">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/">Servlet bank</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="login">Login</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="container body-content">

    <div class="alert alert-success">${successMessage}</div>
    <div class="alert alert-info">${infoMessage}</div><br>
    <div class="alert alert-danger">${errorMessage}</div>
