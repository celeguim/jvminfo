<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%@ page import="java.lang.management.ManagementFactory"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.net.InetAddress"%>
<%@ page import="java.net.UnknownHostException"%>

<html>
<head>
<title>JVM Info by Celeguim</title>
</head>
<body bgcolor="white">
<h3>Welcome!</h3>

<%
	session.setAttribute("dummyData", new java.util.Date());

	long mb = 1024 * 1024;
	Runtime runtime = Runtime.getRuntime();
	long usedMem = ((runtime.totalMemory() - runtime.freeMemory()) / mb);
	long freeMem = (runtime.freeMemory() / mb);
	long totalMem = (runtime.totalMemory() / mb);
	long maxMem = (runtime.maxMemory() / mb);
	Calendar calendar = Calendar.getInstance();
	int dst = (calendar.getTimeZone().getDSTSavings() / 1000 / 60 / 60);
	List<String> inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
	Iterator<String> it = inputArgs.iterator();
	StringBuffer argumentos = new StringBuffer();

	while (it.hasNext()) {
		argumentos.append(it.next() + "\n");
	}

	Date agora = new Date(System.currentTimeMillis());
		
	InetAddress ip = null;
	String hostname;
	try {
	    ip = InetAddress.getLocalHost();
	    hostname = ip.getHostName();
	    System.out.println("Your current IP address : " + ip);
	    System.out.println("Your current Hostname : " + hostname);
	} catch (UnknownHostException e) {
	    e.printStackTrace();
	}
%>

<table border="1">
	<tr>
		<td width="150">JSESSIONID</td>
		<td><%=session.getId()%></td>
	</tr>
	<tr>
		<td width="150">InetAddress</td>
		<td><%=ip%></td>
	</tr>
	<tr>
		<td width="150">Hora da JVM</td>
		<td><%=agora%></td>
	</tr>
	<tr>
		<td width="150">Country</td>
		<td><%=request.getLocale().getCountry()%></td>
	</tr>
	<tr>
		<td width="150">Display Country</td>
		<td><%=request.getLocale().getDisplayCountry()%></td>
	</tr>
	<tr>
		<td width="150">Display Language</td>
		<td><%=request.getLocale().getDisplayLanguage()%></td>
	</tr>
	<tr>
		<td width="150">Display Name</td>
		<td><%=request.getLocale().getDisplayName()%></td>
	</tr>
	<tr>
		<td width="150">Local Address</td>
		<td><%=request.getLocalAddr()%></td>
	</tr>
	<tr>
		<td width="150">Local Name</td>
		<td><%=request.getLocalName()%></td>
	</tr>
	<tr>
		<td width="150">Local Port</td>
		<td><%=request.getLocalPort()%></td>
	</tr>
	<tr>
		<td width="150">Remote Host</td>
		<td><%=request.getRemoteHost()%></td>
	</tr>
	<tr>
		<td width="150">Remote Port</td>
		<td><%=request.getRemotePort()%></td>
	</tr>
	<tr>
		<td width="150">Server Name</td>
		<td><%=request.getServerName()%></td>
	</tr>
	<tr>
		<td width="150">Server Port</td>
		<td><%=request.getServerPort()%></td>
	</tr>

	<tr>
		<td width="150">Total Memory</td>
		<td><%=totalMem%></td>
	</tr>
	<tr>
		<td width="150">Used Memory</td>
		<td><%=usedMem%></td>
	</tr>
	<tr>
		<td width="150">Free Memory</td>
		<td><%=freeMem%></td>
	</tr>
	<tr>
		<td width="150">Max Memory</td>
		<td><%=maxMem%></td>
	</tr>

	<tr>
		<td width="150">Timezone</td>
		<td><%=calendar.getTimeZone().getDisplayName()%></td>
	</tr>
	<tr>
		<td width="150">Daylight saving time</td>
		<td><%=dst + "h"%></td>
	</tr>
	<tr>
		<td width="150">JVM Arguments</td>
		<td><%=argumentos.toString()%></td>
	</tr>
	<tr>
		<td width="150">Server Info</td>
		<td><%=application.getServerInfo()%></td>
	</tr>
</table>

<%
	session.invalidate();
%>

<p>
</body>
</html>
