package frc.team4362.profiling;

/**
 * A simple struct to hold together values for our motion profiling control loop
 * and profile generation
 */
@SuppressWarnings("WeakerAccess")
public class PIDFVA {
	public final double kP;
	public final double kI;
	public final double kD;
	public final double kF;
	public final double kV;
	public final double kA;

	public PIDFVA(
			final double p,
			final double i,
			final double d,
			final double f,
			final double v,
			final double a
	) {
		kP = p;
		kI = i;
		kD = d;
		kF = f;
		kV = v;
		kA = a;
	}
}
