package it.unibo.finaltask.problemanalysis.test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class Test1 {
	public static String BASE_URL = "http://localhost:8090";
	public static String CHROME_DRIVER_PATH = "C:\\chromedriver.exe";
	public static String VIRTUAL_ROBOT_SHORTCUT_PATH = "C:\\Users\\loren\\Desktop\\virtualRobot.lnk";
	
	private Thread coapServer;
	private Thread robot;
	private Thread plasticBox;
	private Thread wroom;
	private Thread detector;

	
	
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
		
		robot = new Thread(() -> it.unibo.ctxRobot.MainCtxRobotKt.main());
		robot.start();
		
		plasticBox = new Thread(() -> it.unibo.ctxPlasticBox.MainCtxPlasticBoxKt.main());
		plasticBox.start();
		
		wroom = new Thread(() -> it.unibo.ctxWRoom.MainCtxWRoomKt.main());
		wroom.start();
		
		detector = new Thread(() -> it.unibo.ctxDetector.MainCtxDetectorKt.main());
		detector.start();
		
		
		
		driver = new ChromeDriver();
		driver.get(BASE_URL);
	}

	@Test
	public void test() throws Exception {
		Thread.sleep(60000);
		//fail("Not yet implemented");
	}
	
	@Test
	public void test2() throws Exception {
		Thread.sleep(10000);
		//fail("Not yet implemented");
	}
	
	@SuppressWarnings("deprecation")
	@After
	public void stopComponents() throws Exception {
		coapServer.stop();
		robot.stop();
		plasticBox.stop();
		wroom.stop();
		detector.stop();
		
		driver.close();
		driver.quit();
		virtualRobot.destroy();
		Thread.sleep(2000);
	}


}
