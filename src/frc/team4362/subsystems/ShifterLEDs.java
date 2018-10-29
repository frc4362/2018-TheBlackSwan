package frc.team4362.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay;

/**
 * Shifter ShifterLEDs
 */
@SuppressWarnings("WeakerAccess")
public final class ShifterLEDs {
	private final Relay m_relay;
	private final DoubleSolenoid m_shifter;

	public ShifterLEDs(final int channel, final DoubleSolenoid driveBase) {
		m_relay = new Relay(channel);
		m_shifter = driveBase;
	}

	/**
	 * May or may not light up based on {@link DifferentialDrive} gear
	 */
	public void update() {
		final DoubleSolenoid.Value gear = m_shifter.get();
		final boolean shiftedUp = gear == DoubleSolenoid.Value.kReverse;
		forceSet(shiftedUp);
	}

	/**
	 * Forces the ShifterLEDs into a certain state
	 * @param state true to turn the ShifterLEDs on, false to turn them off
	 */
	public void forceSet(final boolean state) {
		m_relay.set(state ? Relay.Value.kOn : Relay.Value.kOff);
	}

	// OwO
	public boolean getTurnedOn() {
		return m_relay.get() == Relay.Value.kOn;
	}
}
