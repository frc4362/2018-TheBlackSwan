package frc.team4362.commands.auton;

public class TurnToAngle extends TurnDegrees {
	public TurnToAngle(final double endpoint, final long timeout) {
		super(endpoint, timeout);
	}

	@Override
	public void initialize() {
		m_endTime = System.currentTimeMillis() + m_duration;
		m_destination = m_degrees;
	}
}
