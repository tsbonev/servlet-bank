<%@include file="tags/header.jsp"%>

<div class="container">

    <h1>Welcome to the bank!</h1>

    <% if(session.getAttribute("authorized") != null
    && ((LoginSessionImpl) session.getAttribute("authorized")).isAuthorized()){%>

    <a class="btn btn-default" href="history?scope=global">View all transactions</a>

    <%}%>

</div>

<%@include file="tags/footer.jsp"%>
