package frc.team4362.util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

@SuppressWarnings({"unused", "WeakerAccess"})
public class IntakeWheelSet {
	public enum SpeedPreset {
		NEUTRAL(0.0f),
		INTAKING(0.8f), // used to be 0.5f
		SLOWLY_OUTTAKING(-0.25f),
		OUTTAKING(-0.5f);

		private final float speed;

		SpeedPreset(final float s) {
			speed = s;
		}
	}

	private final WPI_TalonSRX m_talon1, m_talon2;

	public IntakeWheelSet(final int port1, final int port2) {
		m_talon1 = new WPI_TalonSRX(port1);
		m_talon1.configPeakOutputForward(1, 0);
		m_talon1.configPeakOutputReverse(-1, 0);
		m_talon2 = new WPI_TalonSRX(port2);
		m_talon2.configPeakOutputForward(1, 0);
		m_talon2.configPeakOutputReverse(-1, 0);
	}

	public void set(final float speed) {
		m_talon1.set(ControlMode.PercentOutput, speed);
		m_talon2.set(ControlMode.PercentOutput, -speed);
	}

	public void set(final SpeedPreset preset) {
		set(preset.speed);
	}
}
