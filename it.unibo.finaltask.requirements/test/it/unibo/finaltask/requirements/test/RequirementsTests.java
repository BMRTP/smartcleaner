package it.unibo.finaltask.requirements.test;

import org.junit.Test;
import it.unibo.finaltask.requirements.utils.QakUtils;
import it.unibo.finaltask.requirements.model.WRoom;
import it.unibo.finaltask.requirements.utils.PropertyUtils;

public class RequirementsTests {
	/**
	 * 1. Impartito il comando explore, il detector avvii il task “Explore the room”, percorrendo tutta la superficie della stanza raggiungibile.
	 * @throws Exception
	 */
	@Test(timeout=3000)
	public void detectorExploreTheEntireRoom() throws Exception {
		QakUtils.sendExploreToDetector();
		WRoom room = PropertyUtils.waitUntilRoomIsExplored();
		assert(room.isExplored());
	}
	
	
	/**
	 * 
	 */
}
