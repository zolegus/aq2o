<%@include file="header.jsp"%>
<h3>Instruments</h3>
<p>
	<i>Search will search through instrument key values. Use SQL like
		wild cards.</i>
</p>
<div class="input-append">
	<form>
		<input type="hidden" name="componentId" value="${id}" /> <input
			class="span2" style="width: 500px;" id="appendedInputButton"
			name="searchString" type="text" value="${searchString}" />
		<button type="submit" class="btn btn-primary">Search!</button>
	</form>
</div>


<c:forEach var="e" items="${entries}">
	<tr>
		<td><a href="">${e}</a></td>
	</tr>
</c:forEach>




<hr />
<%@include file="footer.jsp"%>