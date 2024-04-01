package com.celeghin.jvminfo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JvmInfoController {

    @GetMapping(value="/")
	String getHome(ModelMap model) {
        model.addAttribute("message", "Hello World!");
		return "index";
	}
	
}
