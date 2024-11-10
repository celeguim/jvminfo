package com.celeghin.jvminfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Controller
public class JvmInfoController {

	private final ApplicationArguments applicationArguments;

	public JvmInfoController(ApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	@GetMapping(value = "/")
	String getHome(ModelMap model) {

		long mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		long usedMem = ((runtime.totalMemory() - runtime.freeMemory()) / mb);
		long freeMem = (runtime.freeMemory() / mb);
		long totalMem = (runtime.totalMemory() / mb);
		long maxMem = (runtime.maxMemory() / mb);
		Calendar calendar = Calendar.getInstance();
		List<String> inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
		Iterator<String> it = inputArgs.iterator();
		Date agora = new Date(System.currentTimeMillis());
		InetAddress ip = null;
		String hostname = null;

		StringBuilder serverArgs = new StringBuilder();
		StringBuilder appArgs = new StringBuilder();

		while (it.hasNext()) {
			serverArgs.append(it.next()).append(" ");
		}

		// Get all non-option arguments
		for (String arg : applicationArguments.getSourceArgs()) {
			appArgs.append(arg).append(" ");
		}

		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		HttpServletRequest request = attr.getRequest();

		JvmInfoModel jvmInfoObj = new JvmInfoModel();
		jvmInfoObj.country = request.getLocale().getCountry();
		jvmInfoObj.dateTime = agora.toString();
		jvmInfoObj.displayCountry = request.getLocale().getDisplayCountry();
		jvmInfoObj.displayLanguage = request.getLocale().getDisplayLanguage();
		jvmInfoObj.displayName = request.getLocale().getDisplayName();
		jvmInfoObj.freeMem = String.valueOf(freeMem);
		jvmInfoObj.hostname = hostname;
		jvmInfoObj.sessionId = session.getId();
		jvmInfoObj.localAddress = request.getLocalAddr();
		jvmInfoObj.localName = request.getLocalName();
		jvmInfoObj.localPort = String.valueOf(request.getLocalPort());
		jvmInfoObj.maxMem = String.valueOf(maxMem);
		jvmInfoObj.remoteHost = request.getRemoteHost();
		jvmInfoObj.remotePort = String.valueOf(request.getRemotePort());
		jvmInfoObj.serverArgs = serverArgs.toString();
		jvmInfoObj.serverInfo = "Tomcat";
		jvmInfoObj.serverName = request.getServerName();
		jvmInfoObj.serverPort = String.valueOf(request.getServerPort());
		jvmInfoObj.timezone = calendar.getTimeZone().getDisplayName();
		jvmInfoObj.totalMem = String.valueOf(totalMem);
		jvmInfoObj.usedMem = String.valueOf(usedMem);
		jvmInfoObj.ip = ip.toString();
		jvmInfoObj.appArgs = appArgs.toString();

		model.addAttribute("jvmInfoObj", jvmInfoObj);
		session.invalidate();

		return "index";
	}

}
