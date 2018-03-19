package frc.team4362.commands.auton;

import frc.team4362.hardwares.Hardware;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static java.lang.Math.abs;

@SuppressWarnings({"unused", "WeakerAccess"})
public class TurnDegrees extends Command {
	private static final float END_THRESHOLD_DEGREES = 1;

	private static final long DEFAULT_DURATION = 100_000;

	private static final double
			DRIVE_SPEED = -0.5,
			MINIMUM_SPEED_RATIO = 0.25,
			MINIMUM_SPEED = DRIVE_SPEED * MINIMUM_SPEED_RATIO;

	protected final long m_duration;
	protected final double m_degrees;
	protected double m_destination;
	protected long m_endTime;

	public TurnDegrees(final double degrees, final long duration) {
    	m_degrees = degrees;
    	m_duration = duration;
    }

    public TurnDegrees(final double degrees) {
		this(degrees, DEFAULT_DURATION);
	}

    private double getHeading() {
    	return Hardware.getInstance().getMXP().getAngle();
    }

    protected void initialize() {
		m_endTime = System.currentTimeMillis() + m_duration;
    	m_destination = getHeading() + m_degrees;
    	
    	SmartDashboard.putNumber("TurnDegrees destination", m_destination);
    }

    private double getDegreesRemaining() {
    	return m_destination - getHeading();
    }

    protected void execute() {
    	final int direction = (int) Math.signum(getDegreesRemaining());

    	SmartDashboard.putNumber("Turn Error", getDegreesRemaining());

    	// always positive
    	double multiplier = abs(getDegreesRemaining()) / abs(m_degrees);

    	if (abs(multiplier) <= abs(MINIMUM_SPEED_RATIO)) {
    		multiplier = MINIMUM_SPEED_RATIO;
		}

		Hardware.getInstance().getDriveTrain().drive(
    			DRIVE_SPEED * multiplier * -direction,
    			DRIVE_SPEED * multiplier * direction
    	);
    }

    protected boolean isFinished() {
		return isExpired() || Math.abs(getDegreesRemaining()) < END_THRESHOLD_DEGREES;
    }

    protected boolean isExpired() {
		return System.currentTimeMillis() >= m_endTime;
	}

    protected void end() {
    	Hardware.getInstance().getDriveTrain().drive(0, 0);
    }
}
