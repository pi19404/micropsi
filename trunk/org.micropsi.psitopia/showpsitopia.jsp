<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
int ox = Integer.parseInt(request.getParameter("ox"));
int oy = Integer.parseInt(request.getParameter("oy"));
double scale = Double.parseDouble(request.getParameter("scale"));
String watch = request.getParameter("watch");
%>

<html> 
<head>
<title>psitopia-cam</title>
<link rel="stylesheet" href="psitopia.css" type="text/css">
<script language="JavaScript">
window.setTimeout("refetch()", 10000);

function refetch() {
  location.reload();
}

function setScale(value) {
  
  watch = <%= watch %>;
  ox = <%= Integer.toString(ox) %>;
  oy = <%= Integer.toString(oy) %>;
  window.location.href = "showpsitopia.jsp?scale="+value+"&watch="+watch+"&ox="+ox+"&oy="+oy;
}

function setWatch(value) {

  scale = <%= Double.toString(scale) %>;
  ox = <%= Integer.toString(ox) %>;
  oy = <%= Integer.toString(oy) %>;
  window.location.href = "showpsitopia.jsp?scale="+scale+"&watch="+value+"&ox="+ox+"&oy="+oy;
}


</script>
</head>
<body>
<center>
<table>
<tr>
  <td></td>
  <td align='center'><a href="showpsitopia.jsp?scale=<%= Double.toString(scale)%>&ox=<%= Integer.toString(ox) %>&oy=<%= Integer.toString(oy+50) %>">&uarr;</a></td>
  <td></td>
</tr>
<tr>
  <td><a href="showpsitopia.jsp?scale=<%= Double.toString(scale)%>&ox=<%= Integer.toString(ox-50) %>&oy=<%= Integer.toString(oy) %>">&larr;</a></td>
  <td><img height='500' width='500' alt='psitopia-cam loading...' src="/org.micropsi.psitopia/image?scale=<%= Double.toString(scale)%>&ox=<%= Integer.toString(ox) %>&oy=<%= Integer.toString(oy) %>&watch=<%= watch %>"/></td>
  <td><a href="showpsitopia.jsp?scale=<%= Double.toString(scale)%>&ox=<%= Integer.toString(ox+50) %>&oy=<%= Integer.toString(oy) %>">&rarr;</a></td>
</tr>
<tr>
  <td></td>
  <td align='center'><a href="showpsitopia.jsp?scale=<%= Double.toString(scale)%>&ox=<%= Integer.toString(ox) %>&oy=<%= Integer.toString(oy-50) %>">&darr;</a></td>
  <td></td>
</tr>
</table>
<form name='scaleForm'>
  <select name="newscale" size="1" onchange='setScale(options[selectedIndex].value)' >
    <!--option value='0.01' <%= scale == 0.01 ? "selected='selected'" : "" %> >1%</option-->
    <!--option value='0.05' <%= scale == 0.05 ? "selected='selected'" : "" %> >5%</option-->
    <option value='0.1' <%= scale == 0.1 ? "selected='selected'" : "" %> >10%</option>
    <option value='0.25' <%= scale == 0.25 ? "selected='selected'" : "" %> >25%</option>
    <option value='0.5'  <%= scale == 0.5 ? "selected='selected'" : "" %>>50%</option>
    <option value='0.75' <%= scale == 0.75 ? "selected='selected'" : "" %>>75%</option>
    <option value='1.0' <%= scale == 1 ? "selected='selected'" : "" %>>100%</option>
    <option value='2.0' <%= scale == 2.0 ? "selected='selected'" : "" %>>200%</option>
  </select>
  
  <select name="watchagent" size="1" onchange='setWatch(options[selectedIndex].value)' >
        <option value='-1' <%= (watch == null) || "-1".equals(watch) ? "selected='selected'" : "" %>>---</option>
    <%
    	java.util.Map agentMap = org.micropsi.comp.console.web.WebConsole.getInstance().getAgentMap();
    	for(java.util.Iterator i=agentMap.keySet().iterator();i.hasNext();) {
    		String name = (String)i.next();
    		String id = (String)agentMap.get(name);
    		boolean selected = id.equals(request.getParameter("watch"));
    		out.write("<option value='"+id+"' "+(selected ? "selected='selected'" : "")+"'>"+name+"</option>");
    	}
    
    %>

  </select>
  
</form>
</center>
</body>
</html>