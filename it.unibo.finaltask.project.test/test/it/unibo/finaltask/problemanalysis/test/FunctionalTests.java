package it.unibo.finaltask.problemanalysis.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import it.unibo.finaltask.problemanalysis.test.utils.CoapUtils;
import it.unibo.finaltask.problemanalysis.test.utils.Maps;
import it.unibo.finaltask.problemanalysis.test.utils.QActorInterface;
import it.unibo.finaltask.problemanalysis.test.utils.Utils;
import itunibo.planner.model.RoomMap;

public class FunctionalTests {
	public static final String BASE_URL = "http://localhost:8090";
	public static final String CHROME_DRIVER_PATH = "C:\\chromedriver.exe";
	
	public static final String VIRTUAL_ROBOT_SHORTCUT_PATH = "C:\\Users\\emanu\\OneDrive\\Desktop\\virtualRobot.lnk";
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
		Runtime.getRuntime().exec(PLASTICBOX_START_CMD);
		Runtime.getRuntime().exec(WROOM_START_CMD);
		Runtime.getRuntime().exec(DETECTOR_START_CMD);		
		
		driver = new ChromeDriver();
		driver.get(BASE_URL);
		Thread.sleep(15000);
	}

	/**
	 * 1. Impartito il comando explore, il robot avvii il task “Explore the room”, percorrendo tutta la superficie della stanza raggiungibile.
	 * @throws Exception
	 */
	@Test(timeout=300000)
	public void detectorExploreTheEntireRoom() throws Exception {
		QActorInterface detector = new QActorInterface("127.0.0.1", 8022);
		detector.sendMessage("msg(explore, dispatch, gui, detector, explore(x), 1)");
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/RoomMap", map -> {
			String curMap = RoomMap.mapFromString(map).toString();
			return curMap.equals(Maps.CLEAN_MAP_1) || curMap.equals(Maps.CLEAN_MAP_2) || curMap.equals(Maps.CLEAN_MAP_3);
		});
	}
	
	/**
	 * 2. Impartito il comando suspend, il robot avvii il task “Go to home”, ritornando a discoveryHome.
	 */
	@Test(timeout=180000)
	public void detectorCanReturnHome() throws Exception {
		QActorInterface sender = new QActorInterface("127.0.0.1", 8022);
		sender.sendMessage("msg(explore, dispatch, test, detector, explore(x), 1)");
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/RoomMap", map -> Utils.robotIsInSubArea(1, 5, 3, 5, RoomMap.mapFromString(map)));
		sender.sendMessage("msg(suspend, dispatch, test, detector, suspend(x), 1)");
		sender.close();
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/RoomMap", map -> Utils.robotIsAtDiscoveryHome(RoomMap.mapFromString(map)));
	}
	
	/**
	 * 3. Impartito il comando terminate, il robot avvii il task “Terminate the work”, ritornando a discoveryHome con il detectorBox vuoto.
	 */
	@Test(timeout=180000)
	public void detectorCanTerminateWork() throws Exception {
		final int spaceAvailable = 1;
		QActorInterface sender = new QActorInterface("127.0.0.1", 8022);
		sender.sendMessage("msg(init, dispatch, test, detector, init("+spaceAvailable+", false), 1)");
		sender.sendMessage("msg(explore, dispatch, test, detector, explore(x), 1)");
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/RoomMap", map -> Utils.robotIsInSubArea(2, 5, 3, 5, RoomMap.mapFromString(map)));
		
		sender.sendMessage("msg(terminate, dispatch, test, detector, terminate(x), 1)");
		sender.close();
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/RoomMap", map -> Utils.robotIsAtDiscoveryHome(RoomMap.mapFromString(map)));
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/SpaceAvailable", space -> Integer.parseInt(space) == spaceAvailable);
	}
	
	/**
	 * 4. Supponendo che il robot sia in fase di esplorazione, dopo l’esecuzione del task “Empty the detectorBox”, deve ritornare all’ultima posizione raggiunta in fase di esplorazione e continuarla.
	 */
	@Test(timeout=180000)
	public void detectorCanContinueExplorationFromLastPosition() throws Exception {
		final int detectorSpaceAvailable = 1;
		final int plasticBoxSpaceAvailable = 1;
		
		QActorInterface plasticBox = new QActorInterface("127.0.0.1", 8016);
		plasticBox.sendMessage("msg(init, dispatch, test, plasticbox, init("+plasticBoxSpaceAvailable+"), 1)");
		plasticBox.close();
		
		QActorInterface detector = new QActorInterface("127.0.0.1", 8022);
		detector.sendMessage("msg(init, dispatch, test, detector, init("+detectorSpaceAvailable+", false), 1)");
		detector.sendMessage("msg(explore, dispatch, test, detector, explore(x), 1)");
		detector.close();
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/currentTask", task -> task.equals("Exploring"));

		CoapUtils.pollResourceValue("coap://localhost:5683/detector/SpaceAvailable", space -> Integer.parseInt(space) == 0);

		CoapUtils.pollResourceValue("coap://localhost:5683/detector/currentTask", task -> !task.equals("Exploring"));

		CoapUtils.pollResourceValue("coap://localhost:5683/plasticbox/SpaceAvailable", space -> Integer.parseInt(space) == 0);

		CoapUtils.pollResourceValue("coap://localhost:5683/detector/currentTask", task -> !task.equals("Exploring"));
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/RoomMap", map -> Utils.robotIsInSubArea(Maps.MINLASTPOS_X, Maps.MAXLASTPOS_X, Maps.MINLASTPOS_Y, Maps.MAXLASTPOS_Y, RoomMap.mapFromString(map)));
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/currentTask", task -> task.equals("Exploring"));
	}
	/**
	 * 5. Il detectorBox contenga esattamente una bottiglia a seguito della raccolta da parte del robot (supponendo che non ne abbia raccolta alcuna in precedenza).
	 */
	@Test(timeout=180000)
	public void detectorCanGrabABottle() throws Exception {
		QActorInterface sender = new QActorInterface("127.0.0.1", 8022);
				
		int sa1 = Integer.parseInt(CoapUtils.pollResourceValue("coap://localhost:5683/detector/SpaceAvailable", x -> !x.isEmpty()));
		
		sender.sendMessage("msg(explore, dispatch, gui, detector, explore(x), 1)");
		sender.close();
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/SpaceAvailable", space -> Integer.parseInt(space) == sa1 - 1);
	}
	
	/**
	 * 6. Il robot sia in grado di gettare una bottiglia, che ha nel detectorBox, all’interno del plasticBox.
	 */
	@Test(timeout=180000)
	public void detectorCanThrowABottleInsidePlasticBox() throws Exception {
		final int detectorSpaceAvailable = 1;
		final int plasticBoxSpaceAvailable = 1;
		
		QActorInterface plasticBox = new QActorInterface("127.0.0.1", 8016);
		plasticBox.sendMessage("msg(init, dispatch, test, plasticbox, init("+plasticBoxSpaceAvailable+"), 1)");
		plasticBox.close();
		
		QActorInterface sender = new QActorInterface("127.0.0.1", 8022);
		sender.sendMessage("msg(init, dispatch, test, detector, init("+detectorSpaceAvailable+", false), 1)");
		sender.sendMessage("msg(explore, dispatch, test, detector, explore(x), 1)");
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/SpaceAvailable", space -> Integer.parseInt(space) == 0);
		
		sender.sendMessage("msg(terminate, dispatch, test, detector, terminate(x), 1)");
		sender.close();
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/SpaceAvailable", space -> Integer.parseInt(space) == detectorSpaceAvailable);
		
		CoapUtils.pollResourceValue("coap://localhost:5683/plasticbox/SpaceAvailable", plasticboxspace -> Integer.parseInt(plasticboxspace) == 0);
	}
	
	/**
	 * 7. Il robot non getti una bottiglia nel plasticBox se questo è pieno e invii un messaggio al supervisore in attesa di un comando.
	 */
	@Test(timeout=180000)
	public void detectorSendNotificationWhenPlasticBoxIsFull() throws Exception {
		final int detectorSpaceAvailable = 1;
		final int plasticBoxSpaceAvailable = 0;
		
		QActorInterface plasticBox = new QActorInterface("127.0.0.1", 8016);
		plasticBox.sendMessage("msg(init, dispatch, test, plasticbox, init("+plasticBoxSpaceAvailable+"), 1)");
		plasticBox.close();
		
		QActorInterface sender = new QActorInterface("127.0.0.1", 8022);
		sender.sendMessage("msg(init, dispatch, test, detector, init("+detectorSpaceAvailable+", false), 1)");
		sender.sendMessage("msg(explore, dispatch, test, detector, explore(x), 1)");
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/SpaceAvailable", space -> Integer.parseInt(space) == detectorSpaceAvailable - 1);
		
		sender.sendMessage("msg(terminate, dispatch, test, detector, terminate(x), 1)");
		sender.close();
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/waitingForSupervisor", waiting -> Boolean.valueOf(waiting));
	}
	
	/**
	 * 8. Il robot sospenda la propria attività a fronte della ricezione del comando suspend da parte dell’agente della stanza nel caso in cui il livello di particolato sia troppo elevato.
	 */
	@Test(timeout=180000)
	public void roomAgentsSuspendsDetector() throws Exception {
		QActorInterface sender = new QActorInterface("127.0.0.1", 8022);
		QActorInterface room = new QActorInterface("127.0.0.1", 8020);
		sender.sendMessage("msg(explore, dispatch, test, detector, explore(x), 1)");
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/RoomMap", map -> Utils.robotIsInSubArea(1, 5, 3, 5, RoomMap.mapFromString(map)));
		
		room.sendMessage("msg(set, dispatch, test, tvocadapter, set(100.0), 1)");
		Thread.sleep(1000);
		room.sendMessage("msg(set, dispatch, test, tvocadapter, set(0.0), 1)");

		sender.close();
		room.close();
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/RoomMap", map -> Utils.robotIsAtDiscoveryHome(RoomMap.mapFromString(map)));
	}
	
	/**
	 * 9. Il robot sia in grado di tornare a discoveryHome (senza incontrare ulteriori ostacoli).
	 */
	@Test(timeout=100000)
	public void detectorCanReturnToDiscoveryHomeAvoidingObstacles() throws Exception {
		QActorInterface detector = new QActorInterface("127.0.0.1", 8022);
		detector.sendMessage("msg(explore, dispatch, test, detector, explore(x), 1)");
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/RoomMap", map -> !map.isEmpty());
		
		CoapUtils.pollResourceValue("coap://localhost:5683/detector/RoomMap", map -> Utils.robotIsInSubArea(3, 5, 3, 5, RoomMap.mapFromString(map)));
		detector.sendMessage("msg(suspend, dispatch, test, detector, suspend(x), 1)");
		detector.close();
		
		final String initialMap = CoapUtils.getResourceValue("coap://localhost:5683/detector/RoomMap");
		final int discovered = (int) initialMap.chars().filter(c -> c == '1').count();
		
		String finalMap = CoapUtils.pollResourceValue("coap://localhost:5683/detector/RoomMap", map -> RoomMap.mapFromString(map).isRobot(1, 1));
		
		int cells = (int) finalMap.chars().filter(c -> c == '1').count();
		assert(cells == discovered);

	}
	
	@After
	public void stopComponents() throws Exception {
		Runtime.getRuntime().exec("taskkill /FI \"WindowTitle eq "+ PREFIX_WINDOW_NAME +"*\" /T /F");
		
		driver.close();
		driver.quit();
		virtualRobot.destroy();
		Thread.sleep(1000);
	}
	
	@AfterClass
	public static void end() throws Exception {
		Runtime.getRuntime().exec("taskkill /FI \"WindowTitle eq "+ VIRTUAL_ROBOT_WINDOW_NAME +"*\" /T /F");
	}

}
