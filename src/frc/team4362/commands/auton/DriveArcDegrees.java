package frc.team4362.commands.auton;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team4362.hardwares.Hardware;

@SuppressWarnings("WeakerAccess")
public class DriveArcDegrees extends DriveCircleDuration {
	private static final double THRESHOLD_DEGREES = 1.5;

	private static final double RAMP_DOWN_THRESHOLD = 20;

	private final double m_headingChange;
	private double m_endHeading;

	/**
	 * @param speed The speed of the outer wheel
	 * @param radius The radius of the circle it is rotating
	 * @param duration The timeout on the movement
	 * @param headingChange The change in heading the program should aim for- currently always negative
	 */
	public DriveArcDegrees(
			final double speed,
			final double radius,
			final long duration,
			final double headingChange
	) {
		super(speed, radius, duration);
		m_headingChange = headingChange;
	}

	@Override
	public void initialize() {
		super.initialize();
		m_endHeading = getHeading() + m_headingChange;
	}

	@Override
	public void execute() {
		double multiplier;

		// this is the final decision about whether or not multiplier will be 1.0
		if (m_hasRampedUp) {
			multiplier = 1.0;
		} else if (getRuntime() > RAMP_TIME) {
			m_hasRampedUp = true;
			multiplier = 1.0;
		} else {
			// this scales it from START_SPEED_RATIO to 1
			// f(x) = 0.5 + (0.5 * (x / 4000))
			multiplier = START_SPEED_RATIO + ((1 - START_SPEED_RATIO) * (getRuntime() / RAMP_TIME));
		}

		if (Math.abs(getDegreesRemaining()) <= (RAMP_DOWN_THRESHOLD - 1)) {
			multiplier *= Math.abs(getDegreesRemaining()) / RAMP_DOWN_THRESHOLD;
			multiplier += Math.max(0.1, 1.0 / RAMP_DOWN_THRESHOLD);
		}

		final double left, right;

		if (m_radius > 0) {
			left = m_outerSpeed;
			right = m_innerSpeed;
		} else if (m_radius < 0) {
			left = m_innerSpeed;
			right = m_outerSpeed;
		} else {
			// fuk
			left = 0;
			right = 0;
		}

		Hardware.getInstance().getDriveTrain().driveAuton(
				left * multiplier,
				right * multiplier
		);
	}

	@Override
	public boolean isFinished() {
		SmartDashboard.putNumber("turn distance remaining", getDegreesRemaining());
		return super.isFinished() || Math.abs(getDegreesRemaining()) < THRESHOLD_DEGREES;
	}

	@Override
	public void end() {
		Hardware.getInstance().getDriveTrain().drive(0, 0);
	}

	private static double getHeading() {
		return Hardware.getInstance().getMXP().getAngle();
	}

	private double getDegreesRemaining() {
		return m_endHeading - getHeading();
	}
}
