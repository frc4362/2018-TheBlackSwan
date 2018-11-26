package frc.team4362.util.func;

import frc.team4362.subsystems.DifferentialDrive;
import frc.team4362.util.joy.DriveItems;
import frc.team4362.util.joy.Gemstick;

/**
 * The functional interface which represents a function
 * which will take the drive train and joysticks in the form {@link DriveItems}
 * and control the movement of the robot
 */
@FunctionalInterface
public interface DriveConsumer {
	void accept(
			final DifferentialDrive driveTrain,
			final Gemstick stickLeft,
			final Gemstick stickRight);
}
