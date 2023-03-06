package com.celeghin.jvminfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class JvmInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public JvmInfo() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(true);
		Cookie myCookie = new Cookie("MY_COOKIE", session.getId());
		String path = request.getServletContext().getContextPath();
		path = path + "/jvminfo";
		myCookie.setPath(path);
		String domain = request.getServerName();
		myCookie.setDomain(domain);
		response.addCookie(myCookie);

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

		StringBuilder builder = new StringBuilder();
		builder.append(String.format("<table border='1'>"));
		builder.append(String.format("<tr><td width='150'> Hostname/IP </td>"));
		builder.append(String.format("<td width='150'> %s </td></tr>", ip));
		builder.append(String.format("<tr><td width='150'> %s </td>", myCookie.getName()));
		builder.append(String.format("<td width='150'> %s </td></tr>", myCookie.getValue()));
		builder.append(String.format("<tr><td width='150'> %s </td>", myCookie.getPath()));
		builder.append(String.format("<td width='150'> %s </td></tr>", myCookie.getDomain()));
		builder.append("</table>");
		response.getWriter().append(builder.toString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
