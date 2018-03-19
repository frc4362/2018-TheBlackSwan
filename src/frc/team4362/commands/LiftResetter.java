package frc.team4362.commands;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Command;

public class LiftResetter extends Command {
	private final WPI_TalonSRX m_talon;
	private final DigitalInput m_switch1, m_switch2;

	private boolean m_lastState;

	public LiftResetter(
			final WPI_TalonSRX talon,
			final DigitalInput switch1,
			final DigitalInput switch2
	) {
		m_talon = talon;
		m_switch1 = switch1;
		m_switch2 = switch2;
	}

	private boolean isEitherSwitchHit() {
		return m_switch1.get() || m_switch2.get();
	}

	@Override
	public void initialize() {
		m_lastState = false;
	}

	@Override
	public void execute() {
		final boolean state = isEitherSwitchHit();

		if (state && !m_lastState) {
			m_talon.setSelectedSensorPosition(0,0, 0);
			m_talon.configPeakOutputReverse(0, 0);
		} else if (!state && m_lastState) {
			m_talon.configPeakOutputReverse(-0.7, 0);
		}

		m_lastState = state;
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
