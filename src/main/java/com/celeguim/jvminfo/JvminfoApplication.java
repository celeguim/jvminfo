package com.celeguim.jvminfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.servlet.http.HttpSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class JvminfoApplication {

	@RequestMapping("/")
	String root(HttpSession session) {
		String sessionId = session.getId();
		String hostname = shell_exec("hostname");
		hostname = hostname.replace("%", "");
		return "Session Id: " + sessionId + "\n</br>" + "hostname: " + hostname + "\n";
	}

	public static void main(String[] args) {
		SpringApplication.run(JvminfoApplication.class, args);
	}

	public String shell_exec(String cmd) {
		String o = null;
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String r;
			while ((r = b.readLine()) != null)
				if (o == null)
					o = r;
				else
					o += r;
		} catch (Exception e) {
			o = "error";
		}
		return o;
	}

}
