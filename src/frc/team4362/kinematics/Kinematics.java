package frc.team4362.kinematics;

import frc.team4362.kinematics.motion.Pose;
import frc.team4362.kinematics.motion.Rotation;
import frc.team4362.kinematics.motion.Twist;

import static frc.team4362.kinematics.Physics.EPSILON;
import static frc.team4362.kinematics.Physics.dcf;

// thanks FRC4028 Beak Squad
public final class Kinematics {
	private Kinematics() { }

	public static Twist forwardKinematics(
			final double wheelDeltaLeft,
			final double wheelDeltaRight
	) {
		final double velocityDelta = (wheelDeltaRight - wheelDeltaLeft) / 2;
		final double rotationDelta = velocityDelta * 2 / Physics.DRIVE_TRAIN_WIDTH;
		return forwardKinematics(wheelDeltaLeft, wheelDeltaRight, rotationDelta);
	}

	public static Twist forwardKinematics(
			final double wheelDeltaLeft,
			final double wheelDeltaRight,
			final double rotationDelta
	) {
		final double dx = (wheelDeltaLeft + wheelDeltaRight) / 2.0;
		return new Twist(dx, 0, rotationDelta);
	}

	public static Twist forwardKinematics(
			final Rotation previousHeading,
			final double wheelLeftDelta,
			final double wheelRightDelta,
			final Rotation currentHeading
	) {
		final double dx = (wheelLeftDelta + wheelRightDelta) / 2.0;
		final double dy = 0.0;
		return new Twist(dx, dy, previousHeading.inverse().rotate(currentHeading).getRadians());
	}

	public static Pose integrateForwardKinematics(
			final Pose currentPose,
			final Twist forwardKinematics
	) {
		return currentPose.transform(Pose.exp(forwardKinematics));
	}

	public static class Velocity {
		public final double left, right;

		public Velocity(final double l, final double r) {
			left = l;
			right = r;
		}

		private static final String FORMAT_STR = "Velocity[%s, %s]";

		@Override
		public String toString() {
			return String.format(FORMAT_STR, dcf.format(left), dcf.format(right));
		}
	}

	public static Velocity inverseKinematics(final Twist velocity) {
		if (Math.abs(velocity.dtheta) < EPSILON) {
			return new Velocity(velocity.dx, velocity.dy);
		} else {
			final double dv = Physics.DRIVE_TRAIN_WIDTH * velocity.dtheta / 2.0;
			return new Velocity(velocity.dx - dv, velocity.dx + dv);
		}
	}
}
