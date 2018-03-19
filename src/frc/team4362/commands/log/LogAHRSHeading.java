package frc.team4362.commands.log;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LogAHRSHeading extends Command {
	private final AHRS m_ahrs;

	public LogAHRSHeading(final AHRS device) {
		m_ahrs = device;
	}

	public void execute() {
		SmartDashboard.putNumber("new Heading", m_ahrs.getAngle());
	}

	public boolean isFinished() {
		return false;
	}
}
