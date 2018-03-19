package frc.team4362.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.hardwares.Hardware;
import frc.team4362.subsystems.Climber;

public class ClimbRetract extends Command {
	private boolean m_isFinished;

	public ClimbRetract() {
		m_isFinished = false;
	}

	@Override
	public void execute() {
		final Climber climber = Hardware.getInstance().getClimber();

		if (climber.getTalon().getSelectedSensorPosition(0) > Climber.SECOND_DISTANCE) {
			climber.getTalon().set(0);
			m_isFinished = true;
		} else {
			climber.getTalon().set(-1);
		}
	}

	@Override
	public boolean isFinished() {
		return m_isFinished;
	}
}
