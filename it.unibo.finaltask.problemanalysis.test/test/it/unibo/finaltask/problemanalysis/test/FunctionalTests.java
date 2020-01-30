package it.unibo.finaltask.problemanalysis.test;


import org.eclipse.californium.core.CoapClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import it.unibo.finaltask.problemanalysis.test.utils.QActorInterface;
import itunibo.planner.model.RoomMap;


public class FunctionalTests {
	public static final String BASE_URL = "http://localhost:8090";
	public static final String CHROME_DRIVER_PATH = "C:\\chromedriver.exe";
	
	public static final String VIRTUAL_ROBOT_SHORTCUT_PATH = "C:\\Users\\loren\\Desktop\\virtualRobot.lnk";
	public static final String VIRTUAL_ROBOT_WINDOW_NAME = "virtualRobot";
	
	public static final String PREFIX_WINDOW_NAME = "roomcleaner_";
	
	
	public static final String COAPSERVER_WINDOW_NAME = "coapserver";
	public static final String ROBOT_WINDOW_NAME = "robot";
	public static final String PLASTICBOX_WINDOW_NAME = "plasticbox";
	public static final String WROOM_WINDOW_NAME = "wroom";
	public static final String DETECTOR_WINDOW_NAME = "detector";
	
	public static final String COAPSERVER_PROJECT_NAME = "it.unibo.finaltask.coapserver";
	public static final String ROBOT_PROJECT_NAME = "it.unibo.finaltask.problemanalysis.robot";
	public static final String PLASTICBOX_PROJECT_NAME = "it.unibo.finaltask.problemanalysis.plasticbox";
	public static final String WROOM_PROJECT_NAME = "it.unibo.finaltask.problemanalysis.wroom";
	public static final String DETECTOR_PROJECT_NAME = "it.unibo.finaltask.problemanalysis.detector";
	
	
	public static final String COAPSERVER_START_CMD = "cmd /c start \"" + PREFIX_WINDOW_NAME + COAPSERVER_WINDOW_NAME + "\" /d \"../"+ COAPSERVER_PROJECT_NAME +"\" gradle run";
	public static final String ROBOT_START_CMD = "cmd /c start \"" + PREFIX_WINDOW_NAME + ROBOT_WINDOW_NAME + "\" /d \"../"+ ROBOT_PROJECT_NAME +"\" gradle run";
	public static final String PLASTICBOX_START_CMD = "cmd /c start \"" + PREFIX_WINDOW_NAME + PLASTICBOX_WINDOW_NAME + "\" /d \"../"+ PLASTICBOX_PROJECT_NAME +"\" gradle run";
	public static final String WROOM_START_CMD = "cmd /c start \"" + PREFIX_WINDOW_NAME + WROOM_WINDOW_NAME + "\" /d \"../"+ WROOM_PROJECT_NAME +"\" gradle run";
	public static final String DETECTOR_START_CMD = "cmd /c start \"" + PREFIX_WINDOW_NAME + DETECTOR_WINDOW_NAME + "\" /d \"../"+ DETECTOR_PROJECT_NAME +"\" gradle run";
	
	private static WebDriver driver;
	
	private static Process virtualRobot;
	
	
	@BeforeClass
	public static void initialSetup() throws Exception {
		System.setProperty("webdriver.chrome.driver",CHROME_DRIVER_PATH);
		virtualRobot = Runtime.getRuntime().exec("cmd /c start " + VIRTUAL_ROBOT_SHORTCUT_PATH);
		Thread.sleep(2000);
	}
	
	@Before
	public void startComponents() throws Exception {
		
		Runtime.getRuntime().exec(COAPSERVER_START_CMD);
		
		Thread.sleep(2000);
		
		Runtime.getRuntime().exec(ROBOT_START_CMD);
		
		Thread.sleep(2000);
		
		Runtime.getRuntime().exec(PLASTICBOX_START_CMD);
		
		Thread.sleep(2000);
		
		Runtime.getRuntime().exec(WROOM_START_CMD);
		
		Thread.sleep(2000);
		
		Runtime.getRuntime().exec(DETECTOR_START_CMD);
		
		Thread.sleep(2000);
		
		
		driver = new ChromeDriver();
		driver.get(BASE_URL);
		Thread.sleep(10000);
	}

	@Test
	public void DetectorCanGrabABottle() throws Exception {
		QActorInterface sender = new QActorInterface("127.0.0.1", 8022);
		
		CoapClient client = new CoapClient("coap://localhost:5683/detector/SpaceAvailable");
		client.setTimeout(1000L);
		
		String sa = client.get().getResponseText();
		while(sa.isEmpty()) {
			sa = client.get().getResponseText();
			Thread.sleep(500);
		}
				
		int sa1 = Integer.parseInt(sa);
		
		sender.sendMessage("msg(explore, dispatch, gui, detector, explore(x), 1)");
		sender.close();
		
		Thread.sleep(20000);
		
		int sa2 = Integer.parseInt(client.get().getResponseText());
		
		assert(sa1 > sa2);
	}
	
	@Test
	public void DetectorCanReturnHome() throws Exception {
		QActorInterface sender = new QActorInterface("127.0.0.1", 8022);
		sender.sendMessage("msg(explore, dispatch, gui, detector, explore(x), 1)");
		Thread.sleep(8000);
		sender.sendMessage("msg(terminate, dispatch, gui, detector, terminate(x), 1)");
		sender.close();
		Thread.sleep(15000);
		
		CoapClient client = new CoapClient("coap://localhost:5683/detector/RoomMap");
		client.setTimeout(1000L);
		RoomMap map1 = RoomMap.mapFromString(client.get().getResponseText());
		
		assert(map1.isRobot(1,1));
	}
	
	@SuppressWarnings("deprecation")
	@After
	public void stopComponents() throws Exception {
		Runtime.getRuntime().exec("taskkill /FI \"WindowTitle eq "+ PREFIX_WINDOW_NAME +"*\" /T /F");
		
		driver.close();
		driver.quit();
		virtualRobot.destroy();
		Thread.sleep(10000);
	}
	
	@AfterClass
	public static void end() throws Exception {
		Runtime.getRuntime().exec("taskkill /FI \"WindowTitle eq "+ VIRTUAL_ROBOT_WINDOW_NAME +"*\" /T /F");
	}

}
