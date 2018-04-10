package frc.team4362.subsystems;

import edu.wpi.first.wpilibj.Relay;

public class LEDs {
	private final Relay m_relay;

	public LEDs(final int channel) {
		m_relay = new Relay(channel);
	}

	public void turnOn() {
		m_relay.set(Relay.Value.kOn);
	}

	public void turnOff() {
		m_relay.set(Relay.Value.kOff);
	}

	// OwO
	public boolean getTurnedOn() {
		return m_relay.get() == Relay.Value.kOn;
	}
}
