package frc.team4362.util.func;

import frc.team4362.util.joy.DriveItems;

/**
 * The functional interface which represents a function
 * which will take the drive train and joysticks in the form {@link DriveItems}
 * and control the movement of the robot
 */
@FunctionalInterface
public interface DriveConsumer {
	void accept(DriveItems items);
}
