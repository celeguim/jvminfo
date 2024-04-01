package com.celeghin.jvminfo;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class JvmInfoController {

//	@Autowired
//	private ApplicationContext applicationContext;

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
		StringBuffer argumentos = new StringBuffer();
		Date agora = new Date(System.currentTimeMillis());
		InetAddress ip = null;
		String hostname = null;

		while (it.hasNext()) {
			argumentos.append(it.next() + "\n");
		}

		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
//			System.out.println("Your current IP address : " + ip);
//			System.out.println("Your current Hostname : " + hostname);
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
		jvmInfoObj.jessionId = session.getId();
		jvmInfoObj.localAddress = request.getLocalAddr();
		jvmInfoObj.localName = request.getLocalName();
		jvmInfoObj.localPort = String.valueOf(request.getLocalPort());
		jvmInfoObj.maxMem = String.valueOf(maxMem);
		jvmInfoObj.remoteHost = request.getRemoteHost();
		jvmInfoObj.remotePort = String.valueOf(request.getRemotePort());
		jvmInfoObj.serverArgs = argumentos.toString();
		jvmInfoObj.serverInfo = "Tomcat";
		jvmInfoObj.serverName = request.getServerName();
		jvmInfoObj.serverPort = String.valueOf(request.getServerPort());
		jvmInfoObj.timezone = calendar.getTimeZone().getDisplayName();
		jvmInfoObj.totalMem = String.valueOf(totalMem);
		jvmInfoObj.usedMem = String.valueOf(usedMem);
		jvmInfoObj.ip = ip.toString();
		jvmInfoObj.argumentos = argumentos.toString();

		model.addAttribute("jvmInfoObj", jvmInfoObj);
		session.invalidate();

		return "index";
	}

}
