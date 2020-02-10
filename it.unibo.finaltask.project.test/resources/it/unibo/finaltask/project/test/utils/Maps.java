package it.unibo.finaltask.project.test.utils;

import itunibo.planner.model.RoomMap;

public class Maps {
	public final static String CLEAN_MAP_1 = RoomMap.mapFromString(String.join(
			   "\n", 
			   "X, X, X, X, X, X, X, X,   ",
			   "X, r, 1, 1, 1, 1, 1, 1, X,",
			   "X, 1, 1, 1, 1, 1, 1, 1, X,",
			   "X, 1, 1, 1, 1, X, X, 1, X,",
			   "X, 1, 1, 1, 1, X, X, 1, X,",
			   "X, 1, 1, 1, 1, 1, 1, 1, X,",
			   "X, X, X, X, X, X, X, X, X,")).toString();
	
	public final static String CLEAN_MAP_2 = RoomMap.mapFromString(String.join(
			   "\n", 
			   "X, X, X, X, X, X, X,   ",
			   "X, r, 1, 1, 1, 1, 1, X,",
			   "X, 1, 1, 1, 1, 1, 1, X,",
			   "X, 1, 1, 1, X, X, 1, X,",
			   "X, 1, 1, 1, X, X, 1, X,",
			   "X, 1, 1, 1, 1, 1, 1, X,",
			   "X, X, X, X, X, X, X, X,")).toString();
	
	public final static String CLEAN_MAP_3 = RoomMap.mapFromString(String.join(
			   "\n", 
			   "X, X, X, X, X, X, X, X,   ",
			   "X, r, 1, 1, 1, 1, 1, 1, X,",
			   "X, 1, 1, 1, 1, 1, 1, 1, X,",
			   "X, 1, 1, 1, X, X, 1, 1, X,",
			   "X, 1, 1, 1, X, X, 1, 1, X,",
			   "X, 1, 1, 1, 1, 1, 1, 1, X,",
			   "X, X, X, X, X, X, X, X, X,")).toString();
	
	
	public final static int MINLASTPOS_X = 5;
	public final static int MAXLASTPOS_X = 6;

	public final static int MINLASTPOS_Y = 1;
	public final static int MAXLASTPOS_Y = 1;	
	
}
