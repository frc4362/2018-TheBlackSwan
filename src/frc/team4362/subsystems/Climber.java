package frc.team4362.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

@Deprecated
public final class Climber {
	public static final double
		STOP_THRESHOLD = 28500,
		FIRST_DISTANCE = 35500,
		SECOND_DISTANCE = 67000;

	private final WPI_TalonSRX m_talon;

	public Climber(final int port1) {
		m_talon = new WPI_TalonSRX(port1);
		m_talon.configVoltageCompSaturation(1.0, 0);
		m_talon.setSelectedSensorPosition(0, 0, 0);
	}

	public WPI_TalonSRX getTalon() {
		return m_talon;
	}
}
