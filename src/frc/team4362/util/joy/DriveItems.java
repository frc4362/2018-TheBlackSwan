package frc.team4362.util.joy;

import frc.team4362.subsystems.DifferentialDrive;

/**
 * Simple struct for packing the components needed
 * to be passed to {@link frc.team4362.commands.JoystickDriver.DriveMode}
 */
@SuppressWarnings("WeakerAccess")
public class DriveItems {
	public final DifferentialDrive driveBase;
	public final Gemstick stickLeft, stickRight;

	public DriveItems(
			DifferentialDrive drive,
			Gemstick stickL,
			Gemstick stickR
	) {
		driveBase = drive;
		stickLeft = stickL;
		stickRight = stickR;
	}
}
