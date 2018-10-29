package frc.team4362.profiling;

import frc.team4362.Hardware;
import frc.team4362.subsystems.DifferentialDrive;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * A helper class for constructing, loading, and tuning {@link ProfileFollower}s for
 * motion profiles in a streamlined and abstracted manner
 * @see "Builder Pattern"
 */
@SuppressWarnings({"unused", "WeakerAccess", "ResultOfMethodCallIgnored"})
public final class ProfileFollowerBuilder {
	private static final String SAVE_LOCATION = "/home/lvuser/paths/%s";

	// make everything null for clarity
	private ProfileFollowerBuilder(final String name) {
		m_pathLeft = null;
		m_pathRight = null;
		m_driveBase = null;
		m_vars = null;
		m_kTurn = null;

		m_reversed = false;

		m_name = name;
	}

	private final String m_name;

	private boolean m_reversed;
	private Trajectory m_pathLeft, m_pathRight;
	private DifferentialDrive m_driveBase;
	private PIDFVA m_vars;
	private Double m_kTurn;

	/**
	 * @param name A name to be used mostly for logging
	 * @return The bare builder for the {@link ProfileFollowerBuilder}
	 */
	public static ProfileFollowerBuilder builder(final String name) {
		return new ProfileFollowerBuilder(name);
	}

	/**
	 * Creates the base dir for all stored paths
	 * @param filename The name to embed in the path
	 * @return The beginning of the directory to store all saved paths
	 */
	public static String makeDir(final String filename) {
		return String.format(SAVE_LOCATION, filename);
	}


	/**
	 * Allows for theoretical portability to another {@link DifferentialDrive} in the future.
	 * @param driveBase The drive train to run the follower on
	 */
	public ProfileFollowerBuilder withDriveBase(final DifferentialDrive driveBase) {
		m_driveBase = driveBase;
		return this;
	}

	/**
	 * @param kTurn default: 0.01. Increase for more aggressive course correction.
	 */
	public ProfileFollowerBuilder withKTurn(final double kTurn) {
		m_kTurn = kTurn;
		return this;
	}

	/**
	 * Loads both sides of the profile and adds them to the builder.
	 * @param path Just the file name- splits it into left and right automatically.
	 */
	public ProfileFollowerBuilder fromSaved(final String path) {
		final String fullPath = makeDir(path);
		final File leftFile = new File(fullPath + "_left.csv"),
			rightFile = new File(fullPath + "_right.csv");

		m_pathLeft = Pathfinder.readFromCSV(leftFile);
		m_pathRight = Pathfinder.readFromCSV(rightFile);
		return this;
	}

	/**
	 * Use {@link Trajectory}s created in the code to be used in a {@link ProfileFollower} at runtime
	 * @param left The motion profile for the left side of the {@link DifferentialDrive}
	 * @param right The motion profile for the right side of the {@link DifferentialDrive}
	 * @param save Whether to save the trajectories with the name
	 *                specified by {@link ProfileFollowerBuilder#builder(String)}
	 */
	public ProfileFollowerBuilder fromTrajectories(
			final Trajectory left,
			final Trajectory right,
			final boolean save
	) {
		m_pathLeft = left;
		m_pathRight = right;

		if (save) {
			final String header = makeDir(m_name);
			final File
					outputLeft = new File(header + "_left.csv"),
					outputRight = new File(header + "_right.csv");

			try {
				outputLeft.createNewFile();
				outputRight.createNewFile();
			} catch (final IOException ioException) {
				throw new RuntimeException(ioException);
			}

			Pathfinder.writeToCSV(outputLeft, m_pathLeft);
			Pathfinder.writeToCSV(outputRight, m_pathRight);
		}

		return this;
	}

	/**
	 * Only useful when loading profiles that were saved after being made
	 * with {@link ProfileFollowerBuilder#fromTrajectories(Trajectory, Trajectory, boolean)}
	 * @param vars The control variables to be used in the {@link ProfileFollower}
	 */
	public ProfileFollowerBuilder withPIDVA(final PIDFVA vars) {
		m_vars = vars;
		return this;
	}

	/**
	 * Reverses the route on the {@link ProfileFollower}
	 * @param reversed true = run in reverse.
	 */
	public ProfileFollowerBuilder withReversed(final boolean reversed) {
		m_reversed = reversed;
		return this;
	}

	/**
	 * @return If the builder is ready to export the {@link ProfileFollower}
	 */
	public boolean isReady() {
		return Objects.nonNull(m_pathLeft) && Objects.nonNull(m_pathRight);
	}

	private void configureFollower(final EncoderFollower follower) {
		follower.configurePIDVA(m_vars.kP, m_vars.kI, m_vars.kD, m_vars.kV, m_vars.kD);
	}

	/**
	 * @return The built {@link ProfileFollower}, or null if unready for construction.
	 * It only requires the left and right {@link Trajectory}s so this won't happen often.
	 */
	public ProfileFollower build() {
		if (!isReady()) {
			System.err.println("Unready to build ProfileFollower for " + m_name);
			return null;
		}

		// assign a few defaults
		if (Objects.isNull(m_driveBase)) {
			m_driveBase = Hardware.getInstance().getDriveTrain();
		}

		if (Objects.isNull(m_vars)) {
			m_vars = Profiling.DEFAULT_VARS;
		}

		if (Objects.isNull(m_kTurn)) {
			m_kTurn = ProfileFollower.DEFAULT_K_TURN;
		}

		final EncoderFollower
				followerLeft = new EncoderFollower(m_pathLeft),
				followerRight = new EncoderFollower(m_pathRight);

		configureFollower(followerLeft);
		configureFollower(followerRight);

		return new ProfileFollower(
				m_driveBase.getTalons().get(0),
				m_driveBase.getTalons().get(2),
				followerLeft,
				followerRight,
				m_kTurn,
				m_reversed
		);
	}

	/**
	 * EZ load for use when well established and appropriately saved.
	 */
	public static ProfileFollower loadProfile(final String name, final boolean reversed) {
		return ProfileFollowerBuilder.builder(name)
				.fromSaved(name)
			    .withReversed(reversed)
				.build();
	}
}
