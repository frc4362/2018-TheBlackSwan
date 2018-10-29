package frc.team4362.commands;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.team4362.subsystems.Lift;
import frc.team4362.util.command.ToggleableCommand;

/**
 * Opens and closes the mouth based on the position of the lift
 * Meant to be used for auton and teleop
 */
@SuppressWarnings("WeakerAccess")
public class MouthListener extends ToggleableCommand {
	protected final Lift m_lift;
	protected final DoubleSolenoid m_mouth;

	/**
	 * @param lift The {@link Lift} to observe
	 * @param mouth The solenoid to actuate based on {@link Lift} state
	 */
	public MouthListener(
			final Lift lift,
			final DoubleSolenoid mouth
	) {
		m_lift = lift;
		m_mouth = mouth;
	}

	@Override
	public void whenEnabled() {
		final WPI_TalonSRX talon = m_lift.getTalonLeft();
		final double currentSetpoint = m_lift.getSetpoint(),
				currentPosition = talon.getSelectedSensorPosition(0);

		if ((currentSetpoint <= currentPosition
			&& currentSetpoint < Lift.Position.CARRY.positionTicks)
			|| (currentPosition < Lift.Position.CLOSE_THRESHOLD.positionTicks)
		) {
			m_mouth.set(DoubleSolenoid.Value.kReverse);
		} else {
			m_mouth.set(DoubleSolenoid.Value.kForward);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
