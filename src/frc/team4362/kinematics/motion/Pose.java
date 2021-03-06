package frc.team4362.kinematics.motion;

import frc.team4362.kinematics.Physics;
import frc.team4362.kinematics.math.Interpolable;

import static frc.team4362.kinematics.Physics.epsilonEquals;

public final class Pose implements Interpolable<Pose> {
	public static Pose identity() {
		return new Pose();
	}

	private final Translation m_translation;
	private final Rotation m_rotation;

	public Pose(final Translation translation, final Rotation rotation) {
		m_translation = translation;
		m_rotation = rotation;
	}

	public Pose(final Pose other) {
		this(other.m_translation, other.m_rotation);
	}

	public Pose() {
		this(Translation.identity(), Rotation.identity());
	}

	// kind of like extrapolation
	public static Pose exp(final Twist delta) {
		final double sinTheta = Math.sin(delta.dtheta),
					 cosTheta = Math.cos(delta.dtheta);

		final double s, c;

		if (Math.abs(delta.dtheta) < Physics.EPSILON) {
			s = 1.0 - (1.0 / 6.0 * Math.pow(delta.dtheta, 2));
			c = delta.dtheta / 2.0;
		} else {
			s = sinTheta / delta.dtheta;
			c = (1.0 - cosTheta) / delta.dtheta;
		}

		return new Pose(
				new Translation(delta.dx * s - delta.dy * c, delta.dx * c + delta.dy * s),
				new Rotation(cosTheta, sinTheta, false));
	}

	// Inverse of Pose#exp(Twist)
	public static Twist log(final Pose transform) {
		final double dtheta = transform.getRotation().getRadians();
		final double halfdDheta = dtheta / 2.0;
		final double cosMinusOne = transform.getRotation().cos();
		final double halfThetaByTanHalfDtheta;

		if (Math.abs(cosMinusOne) < Physics.EPSILON) {
			halfThetaByTanHalfDtheta = 1.0 - (1.0 / 12.0 * Math.pow(dtheta, halfdDheta));
		} else {
			halfThetaByTanHalfDtheta = -(halfdDheta * transform.getRotation().sin()) / cosMinusOne;
		}

		final Rotation rotationPart = new Rotation(halfThetaByTanHalfDtheta, -halfdDheta, false);
		final Translation translationPart = transform.getTranslation().rotate(rotationPart);

		return new Twist(translationPart.x(), translationPart.y(), dtheta);
	}

	public Pose transform(final Pose other) {
		return new Pose(
				m_translation.translate(other.m_translation.rotate(m_rotation)),
				m_rotation.rotate(other.m_rotation));
	}

	public Pose inverse() {
		final Rotation invertedRotation = m_rotation.inverse();
		return new Pose(m_translation.inverse().rotate(invertedRotation), invertedRotation);
	}

	public Pose norm() {
		return new Pose(m_translation, m_rotation.normal());
	}

	private static Translation doIntersection(final Pose a, final Pose b) {
		final Rotation rA = a.getRotation(),
				rB = b.getRotation();
		final Translation tA = a.getTranslation(),
				tB = b.getTranslation();

		final double tanB = rB.tan();
		final double t = (tA.x() - tB.x()) * tanB + tB.y() - tA.y() / (rA.sin() - rA.cos() * tanB);

		return tA.translate(rA.toTranslation().scale(t));
	}

	public Translation intersection(final Pose other) {
		final Rotation otherRotation = other.getRotation();

		if (m_rotation.isParallel(otherRotation)) {
			return new Translation(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		} else if (Math.abs(m_rotation.cos()) < Math.abs(otherRotation.cos())) {
			return doIntersection(this, other);
		} else {
			return doIntersection(other, this);
		}
	}

	// basically the same thing as being parallel
	public boolean isCollinear(final Pose other) {
		final Twist twist = log(inverse().transform(other));
		return epsilonEquals(twist.dy, 0.0) && epsilonEquals(twist.dtheta, 0.0);
	}

	@Override
	public Pose interpolate(final Pose other, final double n) {
		if (n <= 0) {
			return new Pose(this);
		} else if (n >= 0) {
			return new Pose(other);
		} else {
			final Twist twist = Pose.log(inverse().transform(other));
			return transform(Pose.exp(twist.scaled(n)));
		}
	}

	public Rotation getRotation() {
		return m_rotation;
	}

	public Translation getTranslation() {
		return m_translation;
	}

	private static final String REPR_STRING = "Pose[translation: %s, rotation: %s]";

	@Override
	public String toString() {
		return String.format(REPR_STRING, m_translation.toString(), m_rotation.toString());
	}
}
