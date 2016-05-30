package starter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

/*
 * This Java source file was auto generated by running 'gradle buildInit --type java-library'
 * by 'chiragparmar' at '24/05/16 10:40 AM' with Gradle 2.9
 *
 * @author chiragparmar, @date 24/05/16 10:40 AM
 */
@Controller
public class KingsLanding {
	
	@RequestMapping("/home")
	public String getApplicationPage(Model model) {
		return "bootstrap/index";
	}
	
	@RequestMapping("/")
	public String getLandingPage(Model model){
		return "landing";
	}
	
	@RequestMapping(value="/login",method = RequestMethod.POST)
	public ModelAndView goLogin(WebRequest request){
		return new ModelAndView("bootstrap/index");
	}
	
	@RequestMapping("/logout")
	public ModelAndView goLogout(WebRequest request){
		return new ModelAndView("landing");
	}
	
	
}
