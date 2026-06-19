package com.celeghin.jvminfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.slf4j.Logger;
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
    private final JvmInfoHealthDepStatus dependencies;
    private static final Logger log = LoggerFactory.getLogger(JvmInfoController.class);

    public JvmInfoController(ApplicationArguments applicationArguments, JvmInfoHealthDepStatus dependencies) {
        this.applicationArguments = applicationArguments;
        this.dependencies = dependencies;
    }

    @GetMapping("/load-test")
    public ResponseEntity<String> loadTest(@RequestParam String rnd, @RequestParam String requestCount) {
        log.info("Recebi: {}", requestCount);
        log.info("Request count: {} | RND: {}", requestCount, rnd);

        return ResponseEntity.ok("ok");
    }

    @GetMapping("/")
    String getHome(ModelMap model) {
        log.info("log.info GET / - Home page accessed");

        boolean dbUp = dependencies.isDbUp();
        boolean rabbitUp = dependencies.isRabbitUp();
        model.addAttribute("jvmInfoObj", getModel(dbUp, rabbitUp));
        return "index";
    }

    @PostMapping("/")
    String refreshHome(ModelMap model,
            @RequestParam(name = "dbState", required = false) boolean dbState,
            @RequestParam(name = "rabbitState", required = false) boolean rabbitState) {
        dependencies.setDbUp(dbState);
        dependencies.setRabbitUp(rabbitState);
        model.addAttribute("jvmInfoObj", getModel(dbState, rabbitState));
        return "index";
    }

    JvmInfoModel getModel(boolean dbUp, boolean rabbitUp) {
        long mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();

        // long freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        // long totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);

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
            System.err.println(e.getMessage());
        }

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        HttpServletRequest request = attr.getRequest();

        JvmInfoModel jvmInfoObj = new JvmInfoModel();

        jvmInfoObj.country = String.format("%s / %s",
                request.getLocale().getCountry(), request.getLocale().getDisplayCountry());

        jvmInfoObj.displayLanguage = String.format("%s / %s",
                request.getLocale().getDisplayLanguage(), request.getLocale().getDisplayName());

        jvmInfoObj.localName = String.format("%s / %s",
                request.getLocalName(), request.getLocalAddr());

        jvmInfoObj.dateTime = agora.toString();
        jvmInfoObj.hostname = hostname;
        jvmInfoObj.ip = ip.toString();
        jvmInfoObj.sessionId = session.getId();

        jvmInfoObj.jvm = String.format("%s / %s",
                System.getProperty("java.vm.name"), System.getProperty("java.vm.version"));
        jvmInfoObj.os = String.format("%s / %s / %s",
                System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"));
        jvmInfoObj.javaClassVersion = String.format("%s",
                System.getProperty("java.class.version"));

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

        jvmInfoObj.dbState = dbUp;
        jvmInfoObj.rabbitState = rabbitUp;

        session.invalidate();

        return jvmInfoObj;
    }
}
