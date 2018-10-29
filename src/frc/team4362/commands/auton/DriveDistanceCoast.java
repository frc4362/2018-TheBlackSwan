package frc.team4362.commands.auton;

import java.util.List;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import frc.team4362.Constants;
import frc.team4362.Hardware;
import frc.team4362.subsystems.DifferentialDrive;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static java.lang.Math.signum;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DriveDistanceCoast extends Command {
	protected static final double THRESHOLD = Constants.COUNTS_PER_INCH * 1.5;

	protected final WPI_TalonSRX m_talonLeft, m_talonRight;
	protected final DifferentialDrive m_driveTrain;
	protected final double m_distance, m_speedLeft, m_speedRight;
	protected final long m_duration;
	protected long m_endTime;
	protected double m_goalLeft, m_goalRight;

	// this isn't absolutely optimized, but it was fucky in the past so w/e
    public DriveDistanceCoast(final double distanceInches, final double speed, final long duration) {
    	m_driveTrain = Hardware.getInstance().getDriveTrain();

    	final List<WPI_TalonSRX> talons = m_driveTrain.getTalons();
    	m_talonLeft = talons.get(0);
    	m_talonRight = talons.get(2);

    	// thank you, Quadrature
    	m_distance = distanceInches * Constants.COUNTS_PER_INCH * 4;
    	m_speedLeft = speed;
    	m_speedRight = speed;

    	m_duration = duration;
    }

    protected double getLeftError() {
    	return m_goalLeft - m_talonLeft.getSelectedSensorPosition(0);
    }

    protected double getRightError() {
    	return m_goalRight - m_talonRight.getSelectedSensorPosition(0);
    }

    protected void initialize() {
    	final double startPosLeft = m_talonLeft.getSelectedSensorPosition(0);
    	final double startPosRight = m_talonRight.getSelectedSensorPosition(0);

    	m_goalLeft = startPosLeft + m_distance;
    	m_goalRight = startPosRight + m_distance;

    	m_endTime = System.currentTimeMillis() + m_duration;
    }

    protected void execute() {
    	SmartDashboard.putNumber("left error", getLeftError());
    	SmartDashboard.putNumber("right error", getRightError());

    	m_driveTrain.drive(
    			m_speedLeft * signum(getLeftError()) * signum(m_speedLeft),
    			m_speedRight * signum(getRightError()) * signum(m_speedRight)
    	);
    }

    @Override
    protected boolean isFinished() {
    	final boolean doneLeft = (getLeftError() * signum(m_distance)) < THRESHOLD;
    	final boolean doneRight = (getRightError() * signum(m_distance)) < THRESHOLD;

        return isExpired() || (doneLeft && doneRight);
    }

    protected boolean isExpired() {
    	return System.currentTimeMillis() > m_endTime;
	}
}
