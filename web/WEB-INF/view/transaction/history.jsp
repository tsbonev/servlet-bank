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
        <td><c:out value="${transaction.username}"></c:out></td>
        <td><c:out value="${transaction.date}"></c:out></td>
        <td><c:out value="${transaction.operation}"></c:out></td>
        <td><c:out value="${transaction.amountFormatted}"></c:out></td>
    </tr>

</c:forEach>

</tbody>

    <tfoot align="center">

    <tr>

        <td><a class="btn btn-default"
               href="history?page=1<c:if test="${globalScope}">&scope=global</c:if>">
            First</a></td>

        <td><button class="btn btn-default" onclick="window.location.href='history?page=${currPage - 1}'"

               <c:if test="${currPage <= 1}">disabled=""</c:if>

        >Previous</button></td>

        <td>${currPage} / ${totalPage}</td>

        <td><button class="btn btn-default" onclick="window.location.href='history?page=${currPage + 1}'"

              <c:if test="${!hasNext}">disabled=""</c:if>

        >Next</button></td>

        <td><a class="btn btn-default"
               href="history?page=${totalPage}<c:if test="${globalScope}">&scope=global</c:if>">
            Last</a></td>

    </tr>

    </tfoot>

</table>

<%@include file="../tags/footer.jsp"%>

