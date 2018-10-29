package frc.team4362.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.Hardware;
import frc.team4362.util.IntakeWheelSet;

/**
 * Runs the {@link IntakeWheelSet}s for a specified amount of time
 */
@SuppressWarnings("WeakerAccess")
public final class RunIntakes extends Command {
	private final long m_duration;
	private final IntakeWheelSet.SpeedPreset m_preset;

	private long m_startTime;

	/**
	 * @param preset The preset speed to the run the {@link IntakeWheelSet}s for
	 * @param duration The amount of ms to ensure the speed for
	 */
	public RunIntakes(final IntakeWheelSet.SpeedPreset preset, final long duration) {
		m_preset = preset;
		m_duration = duration;
	}

	@Override
	public void initialize() {
		m_startTime = System.currentTimeMillis();
	}

	@Override
	public void execute() {
		Hardware.getInstance().getIntakes().set(m_preset);
	}

	@Override
	public boolean isFinished() {
		return System.currentTimeMillis() > (m_startTime + m_duration);
	}

	@Override
	public void end() {
		Hardware.getInstance().getIntakes().set(IntakeWheelSet.SpeedPreset.NEUTRAL);
	}
}
