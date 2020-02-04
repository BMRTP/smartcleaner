package it.unibo.finaltask.requirements.utils;

public class QakUtils {
	public static void sendMessageToDetector(final String message) {
	}
	
	public static void sendExploreToDetector() {
		sendMessageToDetector("msg(explore, dispatch, gui, detector, explore(x), 1)");
	}
}
