package frc.team4362.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.hardwares.Hardware;
import frc.team4362.subsystems.Climber;

@Deprecated
@SuppressWarnings("unused")
public class ClimbExtend extends Command {
	private boolean m_isFinished;

	public ClimbExtend() {
		m_isFinished = false;
	}

	@Override
	public void execute() {
		final Climber climber = Hardware.getInstance().getClimber();

		if (climber.getTalon().getSelectedSensorPosition(0) > Climber.FIRST_DISTANCE) {
			climber.getTalon().set(0);
			m_isFinished = true;
		} else if (climber.getTalon().getSelectedSensorPosition(0) > Climber.STOP_THRESHOLD){
			climber.getTalon().set(-0.4);
		} else {
			climber.getTalon().set(-1.0);
		}
	}

	@Override
	public boolean isFinished() {
		return m_isFinished;
	}
}
