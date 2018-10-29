package frc.team4362.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.Hardware;
import frc.team4362.subsystems.Lift;

/**
 * A class to change the setpoint of the {@link Lift}
 */
public final class LiftPositionChange extends Command {
	private final Lift m_lift;
	private final Lift.Position m_position;

	private int m_cycles;
	private long m_startTime;
	private final long m_duration;

	/**
	 * @param position The {@link Lift.Position} setpoint to move to
	 * @param duration The amount of time allowed just for moving the {@link Lift}
	 */
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
