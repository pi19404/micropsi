<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.micropsi.comp.messages.*" %>
<%@ page import="java.util.*" %>

<%
if(session.getAttribute("environment") == null) {
	response.sendRedirect("webconsole;jsessionid="+session.getId()+"?action=refreshComponents");
	return;
}
%>

<html>
<head>
<title>MRS Web Console</title>
<!--link rel="stylesheet" href="micropsi.css" type="text/css"-->
<script language="JavaScript">
function refreshQuestions() {
	self.location.href='webconsole;jsessionid=<%= session.getId() %>?action=refreshQuestions&destination='+document.consoleform.destination.options[document.consoleform.destination.selectedIndex].text;
}
</script>
<link rel="stylesheet" href="psitopia.css" type="text/css">
</head>
<body bgcolor="white">
<h1> MRS Web Console </h1>
<br/><br/>
<a href="index.html">main page</a>
<br/><br/>
<%--<a href="webconsole;jsessionid=<%= session.getId() %>?action=invalidate"> invalidate </a>--%>
<form action='webconsole;jsessionid=<%= session.getId() %>' method='GET' name="consoleform">
<table>
<tr>
<td>Destination</td>
<td><select name="destination" size='1' onChange='refreshQuestions()'>
<%
	MTreeNode environment = (MTreeNode)((MAnswer)session.getAttribute("environment")).getContent();
	Iterator<MTreeNode> components = environment.children();
	while(components.hasNext())  {
		MTreeNode component = components.next();
		boolean selected = component.getValue().equals(session.getAttribute("destination")); 
		out.println("<option"+(selected ? " selected" : "")+">"+component.getValue()+"</option>");
	}
%>

</select></td>
</tr><tr>
<td>Question</td>
<td><select name="question" size='1'>
<%  
	MAnswer answer = (MAnswer)session.getAttribute("descriptor");
	if(answer.getContent() instanceof MTreeNode) {
		MTreeNode descriptor = (MTreeNode)answer.getContent();
		Iterator<MTreeNode> questions = descriptor.children();
		while(questions.hasNext()) {
			MTreeNode question = questions.next();
			if(question.getName().equals("qtype")) {
				out.println("<option>"+question.getValue()+"</option>");
			}
		}
	}
%>
</select></td>
</tr><tr>
<td>Parameters</td>
<td><input name='parameters'/></td>
</tr><tr>
<td>&nbsp;</td>
<td><input type='submit'/></td>
</tr>
</table>
</form>
<pre>
<% 

//String toDisplay = "";
answer = (MAnswer)session.getAttribute("answer"); 
if(answer != null) {
	out.println(answer.getContent());
}

%>
</pre>
</body>
</html>