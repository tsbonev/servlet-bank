<%@include file="tags/header.jsp"%>

<div class="container">

    <h1>Welcome!</h1>

    <% if(session.getAttribute("authorized") != null
    && ((LoginSession) session.getAttribute("authorized")).isAuthorized()){%>

    <a href="history?scope=global">View all transactions</a>

    <%}%>

</div>

<%@include file="tags/footer.jsp"%>
