package frc.team4362.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.hardwares.Hardware;
import frc.team4362.util.joy.Gemstick;

public final class ClimbListener extends Command {
	private final Gemstick m_climbStick;

	public ClimbListener(final Gemstick climbStick) {
		m_climbStick = climbStick;
	}

	@Override
	public void execute() {
		final double climbSpeed = (-m_climbStick.getThrottle() + 1) / 2;

		Hardware.getInstance().getClimber()
				.set(ControlMode.PercentOutput, climbSpeed);
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
