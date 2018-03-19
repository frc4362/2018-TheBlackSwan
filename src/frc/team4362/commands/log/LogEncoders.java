package frc.team4362.commands.log;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team4362.subsystems.DriveBase;

import java.util.List;
import java.util.stream.IntStream;

public final class LogEncoders extends Command {
	private final String[] talonNames = {
			"front left",
			"back left",
			"front right",
			"back right"
	};

	private final List<WPI_TalonSRX> m_talons;

	public LogEncoders(final DriveBase driveTrain) {
		m_talons = driveTrain.getTalons();
	}

	public void execute() {
		IntStream.range(0, 4).forEach(i -> {
			SmartDashboard.putNumber(
					talonNames[i] + " encoder speed",
					m_talons.get(i).getSelectedSensorVelocity(0)
			);

			SmartDashboard.putNumber(
					talonNames[i] + " closed loop error",
					m_talons.get(i).getClosedLoopError(0)
			);

			SmartDashboard.putNumber(
					talonNames[i] + " encoder position",
					m_talons.get(i).getSelectedSensorPosition(0)
			);
		});
	}

	public boolean isFinished() {
		return false;
	}
}
