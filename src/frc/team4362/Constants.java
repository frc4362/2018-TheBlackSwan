package frc.team4362;

// its all constants
@SuppressWarnings("WeakerAccess")
public final class Constants {
	private Constants() {}

	public static final double
			WHEEL_RADIUS_INCHES = 3,
			COUNTS_PER_ROTATION = 1024,
			WHEEL_DIAMETER_INCHES = 2 * WHEEL_RADIUS_INCHES,
			WHEEL_CIRCUMFERENCE_INCHES = Math.PI * WHEEL_DIAMETER_INCHES,
			COUNTS_PER_INCH = COUNTS_PER_ROTATION / WHEEL_CIRCUMFERENCE_INCHES;

	// its separated for semantics
	public static final double
			// 28.5 is normal
			ROBOT_WIDTH_INCHES = 25.25,
			ROBOT_RADIUS_INCHES = ROBOT_WIDTH_INCHES / 2.0,
			ROBOT_LENGTH_INCHES = 28; // used to be 11.5
}
