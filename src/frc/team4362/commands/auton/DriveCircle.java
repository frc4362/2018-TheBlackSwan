package frc.team4362.commands.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.Constants;
import frc.team4362.Hardware;

@SuppressWarnings("WeakerAccess")
public abstract class DriveCircle extends Command {
	protected static final double RAMP_TIME = 1000;
	protected static final double START_SPEED_RATIO = 0.5;

	protected final double m_radius, m_innerSpeed, m_outerSpeed;

	protected long m_endTime, m_startTime;
	protected boolean m_hasRampedUp;

	public DriveCircle(final double speed, final double radius) {
		m_radius = radius;
		m_outerSpeed = speed;
		// tyy jame s
		m_innerSpeed = (m_outerSpeed * Math.abs(radius)) / (Constants.ROBOT_WIDTH_INCHES + Math.abs(radius));
	}

	public void initialize() {
		m_startTime = System.currentTimeMillis();
		m_hasRampedUp = false;
	}

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

		Hardware.getInstance().getDriveTrain().drive(
				left * multiplier,
				right * multiplier
		);
	}

	protected long getRuntime() {
		return System.currentTimeMillis() - m_startTime;
	}
}
