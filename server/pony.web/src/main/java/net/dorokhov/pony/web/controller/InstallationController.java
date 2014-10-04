package net.dorokhov.pony.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class InstallationController {

	@RequestMapping("/install")
	public String install() {
		return "install";
	}

}
