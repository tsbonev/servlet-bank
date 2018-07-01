<%@include file="../tags/header.jsp"%>

<table class="table table-striped">

<thead>
<tr>
    <th>Id</th>
    <th>User ID</th>
    <th>Date</th>
    <th>Operation</th>
    <th>Amount</th>
</tr>
</thead>

<tbody>

<c:forEach var="transaction" items="${transactions}">


    <tr>
        <td><c:out value="${transaction.id}"></c:out></td>
        <td><c:out value="${transaction.userId}"></c:out></td>
        <td><c:out value="${transaction.date}"></c:out></td>
        <td><c:out value="${transaction.operation}"></c:out></td>
        <td><c:out value="${transaction.amountFormatted}"></c:out></td>
    </tr>


</c:forEach>

</tbody>

</table>

<%@include file="../tags/footer.jsp"%>

