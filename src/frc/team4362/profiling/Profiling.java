package frc.team4362.profiling;

import frc.team4362.kinematics.Physics;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

/**
 * Utility class for generating trajectories for one-time use or saving
 */
@SuppressWarnings("WeakerAccess")
public enum Profiling {
	INSTANCE;

	// this is most of what will get us moving on the path
	private static final double CALCULATED_FEED_FORWARD =
			0.48714285714285716;

	public static final double MAX_VELOCITY =
			Physics.SPEED_LOW_GEAR * 1.0;

	private static final double VELOCITY_RATIO =
			1 / MAX_VELOCITY;

	private static final double
			kP = 4.5;
	private static final double kI = 0.0;
	private static final double kD = 0.1;
	private static final double kF = CALCULATED_FEED_FORWARD;
	private static double kV = VELOCITY_RATIO;
	private static final double kA = 0.1;

	// purely to be exported
	public static final PIDFVA DEFAULT_VARS =
			new PIDFVA(kP, kI, kD, kF, kD, kA);

	private static final Trajectory.Config DEFAULT_CONFIG = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_CUBIC,
			Trajectory.Config.SAMPLES_LOW,
			0.01,
			MAX_VELOCITY,
			0.6 * MAX_VELOCITY,
			90000
	);

	private static double limit(final double v, final double limit) {
		return Math.abs(v) > limit ? Math.copySign(v, limit) : v;
	}

	/**
	 * Create a path under the given name
	 * @param name Filename of the saved path
	 * @param points The {@link Waypoint}[] for which to fit splines
	 * @see ProfileFollowerBuilder#loadProfile(String, boolean)
	 */
	public void generatePath(final String name, final Waypoint[] points) {
		final Trajectory trajectory = Pathfinder.generate(points, DEFAULT_CONFIG);
		final TankModifier tankTrajectories = new TankModifier(trajectory)
			    .modify(Physics.DRIVE_TRAIN_WIDTH);
		final Trajectory leftTraj = tankTrajectories.getLeftTrajectory(),
				rightTraj = tankTrajectories.getRightTrajectory();

		ProfileFollowerBuilder.builder(name)
			   .fromTrajectories(leftTraj, rightTraj, true)
			   .build();
	}

	/**
	 * Used entirely for development. Not even called in competition.
	 */
	public void makeAutons() {
		final Waypoint[] points1 = {
				new Waypoint(0, 0, 0),
				new Waypoint(170, 0, 0),
				new Waypoint(240, 46, 0)
		};

		generatePath("scale_r_1", points1);
	}
}
