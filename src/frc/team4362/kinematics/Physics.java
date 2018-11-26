package frc.team4362.kinematics;

import java.text.DecimalFormat;

public final class Physics {
	private Physics() { }

	public static double EPSILON = 1e-9d;

	public static final DecimalFormat dcf = new DecimalFormat("#0.000");

	public static boolean epsilonEquals(final double a, final double b) {
		return (a - EPSILON <= b) && (a + EPSILON >= b);
	}

	public static final double
			DRIVE_TRAIN_WIDTH = 26.75,
			ROBOT_LENGTH = 32.0,
			SPEED_HIGH_GEAR = 252.0, // inches per second
			SPEED_LOW_GEAR = 189.6; // inches per second
}
