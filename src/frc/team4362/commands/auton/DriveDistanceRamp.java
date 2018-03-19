package frc.team4362.commands.auton;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DriveDistanceRamp extends DriveDistanceCoast {
	protected final static double MINIMUM_SPEED = 0.2;
	protected final static double RAMP_DOWN_DISTANCE = 0.35;

	public DriveDistanceRamp(
			final double distanceInches,
			final double speed,
			final long duration
	) {
		super(distanceInches, speed, duration);
	}

	protected boolean shouldRampDown() {
		return abs(getLeftError()) < (abs(m_distance) * RAMP_DOWN_DISTANCE);
	}

	protected double ensureMinimumSpeed(double speed) {
		if (abs(speed) < MINIMUM_SPEED) {
			return MINIMUM_SPEED * signum(speed);
		} else {
			return speed;
		}
	}

	@Override
	protected void execute() {
		final double leftMultiplier, rightMultiplier;

		if (shouldRampDown()) {
			leftMultiplier = abs(getLeftError()) / abs(m_distance);
			rightMultiplier = abs(getRightError()) / abs(m_distance);
		} else {
			leftMultiplier = 1.0;
			rightMultiplier = 1.0;
		}

		SmartDashboard.putNumber("left error", getLeftError());
		SmartDashboard.putNumber("right error", getRightError());

		final double speedCandidateLeft = m_speedLeft * signum(getLeftError())
					    * signum(m_speedLeft) * leftMultiplier,
					 speedCandidateRight = m_speedRight * signum(getRightError())
					    * signum(m_speedRight) * rightMultiplier;

		m_driveTrain.driveAuton(
				ensureMinimumSpeed(speedCandidateLeft),
				ensureMinimumSpeed(speedCandidateRight)
		);
	}

	@Override
	public void end() {
		m_driveTrain.drive(0, 0);
	}
}
