package com.celeghin.jvminfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class JvmInfoController {

	private final ApplicationArguments applicationArguments;
	private static final Logger logger = LoggerFactory.getLogger(JvmInfoController.class);

	@Autowired
	private GlobalStore globalStore;

	public JvmInfoController(ApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	@GetMapping(value = "/")
	String getHome(ModelMap model) {
		int numThreads = 5; // Number of threads

		logger.debug("This is a DEBUG log message");
		logger.info("This is an INFO log message");
		System.out.println("System.out.println message");

		// Start memory-intensive task in the main thread
		Thread[] memoryIntensiveTask = new Thread[numThreads];
		for (int i = 0; i < numThreads; i++) {
			memoryIntensiveTask[i] = new Thread(() -> {
				while (true) {

					globalStore.add();
					globalStore.print();
					logger.debug(String.valueOf(globalStore.get()));

					try {
						// Sleep for a short time to control memory allocation rate
						Thread.sleep(100);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			});
		}

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

		String xmx = null;
		String xms = null;
		String xmn = null;
		String maxRam = null;
		
		while (it.hasNext()) {
			String token = it.next();
			serverArgs.append(token).append(" ");
			
			if (token.contains("-Xms")) xms = token;
			if (token.contains("-Xmx")) xmx = token;
			if (token.contains("-Xmn")) xmn = token;
			if (token.contains("-XX:MaxRAMPercentage")) maxRam = token;			
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

		jvmInfoObj.country = String.format("%s / %s", request.getLocale().getCountry(),
				request.getLocale().getDisplayCountry());

		jvmInfoObj.displayLanguage = String.format("%s / %s", request.getLocale().getDisplayLanguage(),
				request.getLocale().getDisplayName());

		jvmInfoObj.localName = String.format("%s / %s", request.getLocalName(), request.getLocalAddr());

		jvmInfoObj.dateTime = agora.toString();
		jvmInfoObj.hostname = hostname;
		jvmInfoObj.ip = ip.toString();
		jvmInfoObj.sessionId = session.getId();

		jvmInfoObj.jvm = String.format("%s / %s", System.getProperty("java.vm.name"),
				System.getProperty("java.vm.version"));
		jvmInfoObj.os = String.format("%s / %s / %s", System.getProperty("os.name"), System.getProperty("os.version"),
				System.getProperty("os.arch"));
		jvmInfoObj.javaClassVersion = String.format("%s", System.getProperty("java.class.version"));

		jvmInfoObj.maxMem = String.valueOf(maxMem);
		jvmInfoObj.freeMem = String.valueOf(freeMem);
		jvmInfoObj.totalMem = String.valueOf(totalMem);
		jvmInfoObj.usedMem = String.valueOf(usedMem);

		jvmInfoObj.serverName = request.getServerName();
		jvmInfoObj.serverPort = String.valueOf(request.getServerPort());

		jvmInfoObj.localPort = String.valueOf(request.getLocalPort());

		jvmInfoObj.remoteHost = request.getRemoteHost();
		jvmInfoObj.remotePort = String.valueOf(request.getRemotePort());

		jvmInfoObj.serverArgs = serverArgs.toString();
		jvmInfoObj.serverInfo = "Tomcat";
		jvmInfoObj.timezone = calendar.getTimeZone().getDisplayName();
		jvmInfoObj.appArgs = appArgs.toString();

		model.addAttribute("jvmInfoObj", jvmInfoObj);
		session.invalidate();

		return "index";
	}

	@GetMapping(value = "/cpuIntensiveTask")
	String getCpuIntensiveTask(ModelMap model) {

		int numThreads = 2; // Number of threads

		// Start CPU-intensive task in a separate thread
		Thread[] cpuIntensiveTask = new Thread[numThreads];
		for (int i = 0; i < numThreads; i++) {
			cpuIntensiveTask[i] = new Thread(() -> {
				while (true) {
					// Simulate CPU-intensive computation
					double result = Math.sin(Math.random()) * Math.cos(Math.random());

					// Print result to avoid optimization
					System.out.println(Thread.currentThread().getName() + " : cpuIntensiveTask : " + result);
				}
			});
		}

		JvmInfoModel jvmInfoObj = new JvmInfoModel();
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();

		jvmInfoObj.dateTime = new Date(System.currentTimeMillis()).toString();
		jvmInfoObj.sessionId = session.getId();

		jvmInfoObj.jvm = String.format("%s / %s", System.getProperty("java.vm.name"),
				System.getProperty("java.vm.version"));
		jvmInfoObj.javaClassVersion = String.format("%s", System.getProperty("java.class.version"));

		long mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		long usedMem = ((runtime.totalMemory() - runtime.freeMemory()) / mb);
		long freeMem = (runtime.freeMemory() / mb);
		long totalMem = (runtime.totalMemory() / mb);
		long maxMem = (runtime.maxMemory() / mb);

		jvmInfoObj.maxMem = String.valueOf(maxMem);
		jvmInfoObj.freeMem = String.valueOf(freeMem);
		jvmInfoObj.totalMem = String.valueOf(totalMem);
		jvmInfoObj.usedMem = String.valueOf(usedMem);

		model.addAttribute("jvmInfoObj", jvmInfoObj);
		session.invalidate();

		return "cpuIntensiveTask";
	}

}
