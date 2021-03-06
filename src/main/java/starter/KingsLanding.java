package starter;

import interfaces.IAnalyticsEngine;
import interfaces.IUserEngine;
import io.searchbox.client.JestClient;
import io.searchbox.indices.CreateIndex;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mannyobjects.Address;
import mannyobjects.Location;
import mannyobjects.LogObject;
import mannyobjects.UserProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import analytics.AnalyticsEngine;
import authentications.ApplicationElasticConnector;
import authentications.UserEngine;

/*
 * This Java source file was auto generated by running 'gradle buildInit --type java-library'
 * by 'chiragparmar' at '24/05/16 10:40 AM' with Gradle 2.9
 *
 * @author chiragparmar, @date 24/05/16 10:40 AM
 */
@Controller
public class KingsLanding implements ErrorController {

	private IUserEngine userEngine = null;
	private IAnalyticsEngine analyticsEngine = null;
	private JestClient elasticClient = null;
	private LogObject logger = null;

	@Autowired(required = true)
	public KingsLanding(AnalyticsEngine analyticsEngine, UserEngine userEngine,ApplicationElasticConnector elasticConnector) {
		super();
		this.userEngine = userEngine;
		this.analyticsEngine = analyticsEngine;
		this.elasticClient = elasticConnector.getObject();
		this.logger = new LogObject(this);
	}

	@RequestMapping("/error")
	public String getErrorPage(Model model) {
		return "error";
	}

	@RequestMapping("/home")
	public String getApplicationPage(Model model) {
		return "bootstrap/index";
	}
 
	@RequestMapping("/profile")
	public String getUsreProfile(Model model) {
		return "bootstrap/user";
	}

	@RequestMapping("/")
	public String getLandingPage(Model model) throws Exception {
		this.elasticClient.execute(new CreateIndex.Builder("user").build());
		this.elasticClient.execute(new CreateIndex.Builder("location").build());
		logger.logInfo("Indexes cretaed for user and locations");
		return "landing";
	}

	@RequestMapping("/predict")
	public String getPrerict(Model model) {
		return "bootstrap/predict";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody
	String goLogin(WebRequest request) {

		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String imageUrl = request.getParameter("imageUrl");
		logger.logInfo("Logged In User Details",name,email,imageUrl);
		return "Login Success for " +name+ request.getParameter("name");
	}

	@RequestMapping("/logout")
	public @ResponseBody
	String goLogout(WebRequest request) {
		return "Logout success";
	}

	/* User Locations Prediction Service */
	@RequestMapping(value = "/predict", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> proivdePredictionAboutUserLocation(WebRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String uuidString = request.getParameter("userId");
			UUID uuid = UUID.fromString(uuidString);
			UserProfile userProfile = userEngine.getUser(uuid);
			Location locaiton = analyticsEngine.getCurrentLocation(userProfile);
			data.put("current_location", locaiton);
			data.put("most_recent_location", userProfile.getCurrentLocation());
			data.put("message", userProfile.getMessage());
		} catch (Exception e) {
			data.put("locatoin", "");
			data.put("message",
					"Error while providing user location. Please try later");
		}
		return data;
	}

	/* User Management */
	@RequestMapping(value = "/getuser", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> getUser(WebRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data.put("most_recent_location", "");
		} catch (Exception e) {
			data.put("location", "");
			data.put("message",
					"Error while providing user location. Please try later");
		}
		return data;
	}

	@RequestMapping(value = "/createuser", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> createUser(WebRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {

			String userName = request.getParameter("userName");
			Address address = new Address();
			userEngine.createUser(userName, address);

			data.put("most_recent_location", "");
		} catch (Exception e) {
			data.put("locatoin", "");
			data.put("message",
					"Error while providing user location. Please try later");
		}
		return data;
	}

	@RequestMapping(value = "/updateuser", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> updateUser(WebRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {

			String userId = request.getParameter("userId");
			UserProfile user = userEngine.getUser(UUID.fromString(userId));
			userEngine.updateUser(user, UUID.fromString(userId));

			data.put("most_recent_location", "");
		} catch (Exception e) {
			data.put("locatoin", "");
			data.put("message",
					"Error while providing user location. Please try later");
		}
		return data;
	}

	@RequestMapping(value = "/deleteuser", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> deleteUser(WebRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {

			String userId = request.getParameter("userId");
			userEngine.deleteUser(UUID.fromString(userId));

			data.put("most_recent_location", "");
		} catch (Exception e) {
			data.put("locatoin", "");
			data.put("message",
					"Error while providing user location. Please try later");
		}
		return data;
	}

	@RequestMapping(value = "/broadcast", method = RequestMethod.POST)
	public @ResponseBody
	String broadcastMyLocation(WebRequest request) {

		String lat = request.getParameter("lat");
		String lon = request.getParameter("lon");

		// update your location to everyone else
		return "Broadcast successful.";
	}

	public String getErrorPath() {
		return "/error";
	}

}
