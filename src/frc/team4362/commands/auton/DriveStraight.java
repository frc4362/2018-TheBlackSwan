package frc.team4362.commands.auton;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team4362.hardwares.Hardware;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public final class DriveStraight extends DriveDistanceRamp {
	private final static double MAXIMUM_TOLERANCE = 22.5;

	private final double m_speed;
	private final AHRS m_ahrs;

	private double m_startingHeading;

	public DriveStraight(final double distance, final double speed, final long duration) {
		super(distance, speed, duration);
		m_ahrs = Hardware.getInstance().getMXP();
		m_speed = speed;
	}

	@Override
	public void initialize() {
		super.initialize();
		m_startingHeading = m_ahrs.getAngle();
	}

	@Override
	public void execute() {
		SmartDashboard.putNumber("left error", getLeftError());
		SmartDashboard.putNumber("right error", getRightError());

		final double rotation = (m_startingHeading - m_ahrs.getAngle()) / MAXIMUM_TOLERANCE;

		double l, r;

		if (m_speed > 0) {
			// first quadrant, else second quadrant
			if (rotation >= 0) {
				l = m_speed;
				r = m_speed - rotation;
			} else {
				l = m_speed + rotation;
				r = m_speed;
			}
		} else {
			// Third quadrant, else fourth quadrant
			if (rotation >= 0) {
				l = m_speed + rotation;
				r = m_speed;
			} else {
				l = m_speed;
				r = m_speed - rotation;
			}
		}

		final double divisor = max(abs(l), abs(r));

		l /= divisor;
		r /= divisor;

		SmartDashboard.putNumber("adjusted left speed", l);
		SmartDashboard.putNumber("adjusted right speed", r);

		Hardware.getInstance().getDriveTrain().drive(
				l * abs(m_speed),
				r * abs(m_speed)
		);
	}
}
