package com.celeguim.jvminfo;

import static java.lang.System.out;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

        String hostname = null;
        String hostaddress = null;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
            hostaddress = InetAddress.getLocalHost().getHostAddress();
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(JvminfoApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        session.invalidate();
        String server=session.getServletContext().getServerInfo();
        //System.out.println(session.getServletContext().getServerInfo());
        
        
        return "Session Id: " + sessionId + "</br>" + 
                "Server: " + server + "</br>" + 
                "Hostname: " + hostname + "</br>" + 
                "Hostaddress: " + hostaddress;
    }

    public static void main(String[] args) {
        SpringApplication.run(JvminfoApplication.class, args);
    }

}
