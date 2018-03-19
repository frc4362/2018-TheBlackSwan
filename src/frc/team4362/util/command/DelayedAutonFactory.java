package frc.team4362.util.command;

import edu.wpi.first.wpilibj.Preferences;

public class DelayedAutonFactory {
	private static final String AUTON_DELAY_AMOUNT_KEY = "DS";

	public static long getAppropriateDelay() {
		final int amount = Preferences.getInstance().getInt(AUTON_DELAY_AMOUNT_KEY, 0);
		return amount * 1000;
	}
}
