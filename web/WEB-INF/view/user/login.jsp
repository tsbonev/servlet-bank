<%--
  Created by IntelliJ IDEA.
  User: tsvetozar
  Date: 30/06/18
  Time: 17:22
  To change this template use File | Settings | File Templates.
--%>
<%@include file="../tags/header.jsp"%>

    <form name="login" method="post">

        <label>Username: </label>
        <input type="text" required name="username">
        <br>
        <label>Password: </label>
        <input type="password" required name="password">
        <br>
        <input type="submit" value="Login">

    </form>

<%@include file="../tags/footer.jsp"%>
