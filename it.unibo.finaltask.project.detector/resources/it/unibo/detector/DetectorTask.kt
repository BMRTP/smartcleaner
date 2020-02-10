package it.unibo.detector

//exploring, goinghome, emptingBox, emptingBoxTerminating, goingBottle
enum class DetectorTask(val text: String) {
	IDLE("Idle"),
	EXPLORING("Exploring"),
	GOING_HOME("Going to detector home"),
	EMPTING_BOX("Empting detector box"),
	EMPTING_BOX_TERMINATING("Terminating work"),
	GOING_BOTTLE("Returning to last position");
	
	override fun toString(): String {
		return text
	}
}