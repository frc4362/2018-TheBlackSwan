package frc.team4362.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.team4362.util.IntakeWheelSet;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Intakes {
	private DoubleSolenoid m_mouth;
	private IntakeWheelSet m_intakeBottom, m_intakeInner;

	public Intakes(
			final DoubleSolenoid mouth,
			final IntakeWheelSet bottomWheels,
			final IntakeWheelSet innerWheels
	) {
		m_mouth = mouth;
		m_intakeBottom = bottomWheels;
		m_intakeInner = innerWheels;
	}

	public void set(final IntakeWheelSet.SpeedPreset speedPreset) {
		m_intakeInner.set(speedPreset);

		if (m_mouth.get() == DoubleSolenoid.Value.kReverse) {
			m_intakeBottom.set(speedPreset);
		} else {
			m_intakeBottom.set(IntakeWheelSet.SpeedPreset.NEUTRAL);
		}
	}

	public DoubleSolenoid getMouth() {
		return m_mouth;
	}

	public IntakeWheelSet getStage1() {
		return m_intakeBottom;
	}

	public IntakeWheelSet getStage2() {
		return m_intakeInner;
	}
}
