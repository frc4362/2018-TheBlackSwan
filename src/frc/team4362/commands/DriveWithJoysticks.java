package frc.team4362.commands;

import frc.team4362.subsystems.DriveBase;
import frc.team4362.util.command.ToggleableCommand;
import frc.team4362.util.joy.Gemstick;

public class DriveWithJoysticks extends ToggleableCommand {
	private final DriveBase m_driveBase;
	private final Gemstick m_leftStick, m_rightStick;

	public DriveWithJoysticks(
			final DriveBase driveBase,
			final Gemstick leftStick,
			final Gemstick rightStick
	) {
		super(StartMode.ENABLED);

		m_driveBase = driveBase;
		m_leftStick = leftStick;
		m_rightStick = rightStick;
	}

	public void whenEnabled() {
		m_driveBase.drive(
				m_leftStick.get(Gemstick.StickLens.Y),
				m_rightStick.get(Gemstick.StickLens.Y)
		);
	}

	protected boolean isFinished() {
		return false;
	}
}
