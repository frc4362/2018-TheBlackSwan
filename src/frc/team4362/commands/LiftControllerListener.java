package frc.team4362.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.subsystems.Lift;

import static edu.wpi.first.wpilibj.GenericHID.Hand.kLeft;
import static edu.wpi.first.wpilibj.GenericHID.Hand.kRight;

public final class LiftControllerListener extends Command {
	private static final double
			MAX_CHANGE_PER_SECOND = 0.25,
			MAX_CHANGE_PER_TICK = MAX_CHANGE_PER_SECOND / 20;

	private final Lift m_lift;
	private final XboxController m_controller;

	public LiftControllerListener(final Lift lift, final XboxController controller) {
		m_lift = lift;
		m_controller = controller;
	}

	@Override
	public void execute() {
		final double adjustmentRatio =
				m_controller.getTriggerAxis(kRight) - m_controller.getTriggerAxis(kLeft);

		if (Math.abs(adjustmentRatio) > 0.3) {
			m_lift.adjustPosition(adjustmentRatio * MAX_CHANGE_PER_TICK);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
