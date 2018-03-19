package frc.team4362.commands.log;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team4362.subsystems.Lift;

public class LogLiftPositions extends Command {
	private final Lift m_lift;

	public LogLiftPositions(final Lift lift) {
		m_lift = lift;
	}

	@Override
	public void execute() {
		SmartDashboard.putNumber(
				"lift talon 1 position",
				m_lift.getTalonLeft().getSelectedSensorPosition(0)
		);

		SmartDashboard.putNumber(
				"lift talon 1 error",
				m_lift.getTalonLeft().getClosedLoopError(0)
		);

		SmartDashboard.putBoolean(
				"lift isAtSetpoint",
				m_lift.isAtSetpoint()
		);
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
