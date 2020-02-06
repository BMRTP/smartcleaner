package it.unibo.finaltask.requirements.test;

import org.junit.Test;
import it.unibo.finaltask.requirements.utils.QakUtils;
import it.unibo.finaltask.requirements.model.WRoomMap;
import it.unibo.finaltask.requirements.utils.PropertyUtils;

public class RequirementsTests {
	/**
	 * 1. Impartito il comando explore, il detector avvii il task “Explore the room”, percorrendo tutta la superficie della stanza raggiungibile.
	 * @throws Exception
	 */
	@Test(timeout=3000)
	public void detectorExploreTheEntireRoom() {
		QakUtils.sendExploreToDetector();
		WRoomMap room = PropertyUtils.waitUntilRoomIsExplored();
		assert(room.toString().equals(String.join(
						   "\n", 
						   "X, X, X, X, X, X, X, X,   ",
						   "X, r, 1, 1, 1, 1, 1, 1, X,",
						   "X, 1, 1, 1, 1, 1, 1, 1, X,",
						   "X, 1, 1, 1, 1, 1, 1, 1, X,",
						   "X, 1, 1, 1, 1, 1, 1, 1, X,",
						   "X, 1, 1, 1, 1, 1, 1, 1, X,",
						   "X, X, X, X, X, X, X, X, X,")));
	}
	
	
	/**
	 * 2. Il detector è posizionato inizialmente nella detector home (in alto a sinistra).
	 */
	@Test(timeout=3000)
	public void detectorIsOnTopLeftCornerAtStart() {
		WRoomMap wroom = PropertyUtils.getInitialState();
		assert(wroom.toString().equals(String.join(
						   "\n", 
						   "X, X,",
						   "X, r,")));
	}
}
