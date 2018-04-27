package frc.team4362.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.hardwares.Hardware;

public final class ClimbListener extends Command {
	private final XboxController m_controller;

	public ClimbListener(final XboxController controller) {
		m_controller = controller;
	}

	@Override
	public void execute() {
		double climbSpeed = Math.abs(m_controller.getY(GenericHID.Hand.kRight));

		if (!m_controller.getRawButton(7)) {
			climbSpeed = 0.0;
		}

		Hardware.getInstance().getClimber1()
				.set(ControlMode.PercentOutput, -climbSpeed);
		Hardware.getInstance().getClimber2()
				.set(ControlMode.PercentOutput, climbSpeed);
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
