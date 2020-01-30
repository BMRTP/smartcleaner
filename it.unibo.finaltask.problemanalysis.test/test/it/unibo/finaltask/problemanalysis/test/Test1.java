package it.unibo.finaltask.problemanalysis.test;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class Test1 {
	public static String BASE_URL = "http://localhost:8090";
	public static String CHROME_DRIVER_PATH = "C:\\chromedriver.exe";
	
	public static String VIRTUAL_ROBOT_SHORTCUT_PATH = "C:\\Users\\loren\\Desktop\\virtualRobot.lnk";
	public static String VIRTUAL_ROBOT_WINDOW_NAME = "virtualRobot";
	
	public static String PREFIX_WINDOW_NAME = "roomcleaner_";
	
	public static String ROBOT_WINDOW_NAME = "robot";
	public static String PLASTICBOX_WINDOW_NAME = "plasticbox";
	public static String WROOM_WINDOW_NAME = "wroom";
	public static String DETECTOR_WINDOW_NAME = "detector";
	
	public static String ROBOT_PROJECT_NAME = "it.unibo.finaltask.problemanalysis.robot";
	public static String PLASTICBOX_PROJECT_NAME = "it.unibo.finaltask.problemanalysis.plasticbox";
	public static String WROOM_PROJECT_NAME = "it.unibo.finaltask.problemanalysis.wroom";
	public static String DETECTOR_PROJECT_NAME = "it.unibo.finaltask.problemanalysis.detector";
	
	
	public static String ROBOT_START_CMD = "cmd /c start \"" + PREFIX_WINDOW_NAME + ROBOT_WINDOW_NAME + "\" /d \"../"+ ROBOT_PROJECT_NAME +"\" gradle run";
	public static String PLASTICBOX_START_CMD = "cmd /c start \"" + PREFIX_WINDOW_NAME + PLASTICBOX_WINDOW_NAME + "\" /d \"../"+ PLASTICBOX_PROJECT_NAME +"\" gradle run";
	public static String WROOM_START_CMD = "cmd /c start \"" + PREFIX_WINDOW_NAME + WROOM_WINDOW_NAME + "\" /d \"../"+ WROOM_PROJECT_NAME +"\" gradle run";
	public static String DETECTOR_START_CMD = "cmd /c start \"" + PREFIX_WINDOW_NAME + DETECTOR_WINDOW_NAME + "\" /d \"../"+ DETECTOR_PROJECT_NAME +"\" gradle run";

	private Thread coapServer;
	
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
		
		coapServer = new Thread(() -> it.unibo.finaltask.coapserver.MainKt.main());
		coapServer.start();
		
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
	}

	@Test
	public void test() throws Exception {
		Thread.sleep(10000);
	}
	
	@Test
	public void test2() throws Exception {
		Thread.sleep(10000);
	}
	
	@SuppressWarnings("deprecation")
	@After
	public void stopComponents() throws Exception {
		coapServer.stop();
		
		Runtime.getRuntime().exec("taskkill /FI \"WindowTitle eq "+ PREFIX_WINDOW_NAME +"*\" /T /F");
		
		driver.close();
		driver.quit();
		virtualRobot.destroy();
		Thread.sleep(2000);
	}
	
	@AfterClass
	public static void end() throws Exception {
		Runtime.getRuntime().exec("taskkill /FI \"WindowTitle eq "+ VIRTUAL_ROBOT_WINDOW_NAME +"*\" /T /F");
	}

}
