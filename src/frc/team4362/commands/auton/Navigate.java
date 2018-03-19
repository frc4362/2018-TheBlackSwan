package frc.team4362.commands.auton;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team4362.util.Point;
import frc.team4362.util.command.Commands;

import static java.lang.Math.sin;
import static java.lang.Math.abs;
import static java.lang.Math.toRadians;
import static frc.team4362.util.command.Commands.autonOf;

/**
 * yeet
 * @author Ethan
 */
@SuppressWarnings("WeakerAccess")
public final class Navigate {
	private static final long D = 100_000;

	private Navigate() {}

	/**
	 * Will create an arbitrary drive command to navigate to a point
	 * @param speed The peak speed of the outer wheel
	 * @param x The change in x you expect from the movement
	 * @param y The change in y you expect from the movement
	 * @param duration The maximum time the turn is allowed
	 * @param heading The angle change you wish to aim for
	 * @return A drive command of some kind which will do the movement expected
	 */
	public static Command to(
			final double x,
			final double y,
			final double heading,
			final double speed,
			final long duration
	) {
		if (x == 0.0 && y == 0.0 && heading == 0.0) {
			return Commands.nullCommand();
		} else if (x == 0.0 && y == 0.0 && heading != 0){
			return new TurnDegrees(heading);
		} else if (x == 0.0 && y != 0) {
			return new DriveStraight(y, speed, duration);
		} else if (abs(x) == abs(y)) {
			return new DriveArcDegrees(speed, x, duration, heading);
		} else {
			final CommandGroup movement = makeRectangleMovement(x, y, speed, duration);
			final Command correction;

			if (abs(heading) != 90) {
				correction = new TurnDegrees(heading - (90 * Math.signum(speed)) * Math.signum(x));
			} else {
				correction = Commands.nullCommand();
			}

			return new CommandGroup() {
				{
					addSequential(autonOf(movement, correction), duration);
				}
			};
		}
	}

	// no :[
	private static DriveArcDegrees arbitraryCurveDrive(
			final double x,
			final double y,
			final double headingChange,
			final double speed,
			final long duration
	) {
		final Point endPoint = new Point(x, y);
		final double chordLength = Point.distance(Point.origin(), endPoint);
		final double subtendedAngle = abs(headingChange);
		final double smallAngle = (180 - subtendedAngle) / 2.0;

		double r = (sin(toRadians(smallAngle)) * chordLength) / sin(toRadians(subtendedAngle));

		// should NEVER be equal to 0
		if (x < 0) {
			r *= -1;
		}

		// so it's all about calculating r
		return new DriveArcDegrees(speed, r, duration, headingChange);
	}

	/**
	 * @return The position change described expressed as two distinct movements- a drive and a turn
	 */
	private static CommandGroup makeRectangleMovement(
			final double x,
			final double y,
			final double speed,
			final long duration
	) {
		// they should never be equal
		final double sideLong, sideShort;

		if (abs(x) > abs(y)) {
			sideLong = x;
			sideShort = y;
		} else {
			sideLong = y;
			sideShort = x;
		}

		final Command first, second, turn, drive;

		final double turnRadius = abs(sideShort);

		turn = new DriveArcDegrees(
				speed,
				turnRadius * Math.signum(x),
				D,
				90 * Math.signum(x) * Math.signum(speed)
		);

		final double dInches = (abs(sideLong) - abs(sideShort)) * Math.signum(y);

		if (abs(x) > abs(y)) {
			first = turn;
			second = new DriveDistanceRamp(dInches, speed, D);
		} else {
			second = turn;
			first = new DriveDistanceCoast(dInches, speed, D);
		}

		return autonOf(first, second);
	}
}
