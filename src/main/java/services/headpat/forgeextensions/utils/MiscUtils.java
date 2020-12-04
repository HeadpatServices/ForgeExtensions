package services.headpat.forgeextensions.utils;

public class MiscUtils {
	/**
	 * @param chance Integer percentage.
	 * @return The chance result;
	 */
	public static boolean percentChance(int chance) {
		return Math.random() < (chance / 100.0d);
	}


	/**
	 * @param minutes Minutes
	 * @param seconds Seconds
	 * @return Time of both minutes and seconds in ticks.
	 */
	public static int timeToTicks(int minutes, int seconds) {
		return ((minutes * 60) + seconds) * 20;
	}
}
