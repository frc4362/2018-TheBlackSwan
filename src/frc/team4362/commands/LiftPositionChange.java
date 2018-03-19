package frc.team4362.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.hardwares.Hardware;
import frc.team4362.subsystems.Lift;

public final class LiftPositionChange extends Command {
	private final Lift m_lift;
	private final Lift.Position m_position;

	private int m_cycles;
	private long m_startTime, m_duration;

	public LiftPositionChange(final Lift.Position position, final long duration) {
		m_lift = Hardware.getInstance().getLift();
		m_position = position;
		m_duration = duration;

		m_cycles = 0;
	}

	private boolean isExpired() {
		return System.currentTimeMillis() > (m_startTime + m_duration);
	}

	@Override
	public void initialize() {
		m_startTime = System.currentTimeMillis();
	}

	@Override
	public void execute() {
		m_lift.setLiftPreset(m_position);
		m_cycles++;
	}

	@Override
	public boolean isFinished() {
		return isExpired() || (m_cycles > 4 && m_lift.isAtSetpoint());
	}
}
