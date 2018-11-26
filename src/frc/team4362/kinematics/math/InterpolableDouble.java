package frc.team4362.kinematics.math;

public class InterpolableDouble implements Interpolable<InterpolableDouble>,
	InverseInterpolable<InterpolableDouble>, Comparable<InterpolableDouble>
{
	public Double value;

	public InterpolableDouble(final Double val) {
		value = val;
	}

	@Override
	public InterpolableDouble interpolate(final InterpolableDouble other, final double n) {
		return new InterpolableDouble((other.value - value) * n + value);
	}

	@Override
	public double inverseInterpolate(
			final InterpolableDouble upper,
			final InterpolableDouble query
	) {
		final double upperToLower = upper.value - value,
				queryToLower = query.value - value;

		if (upperToLower <= 0.0) {
			return 0.0;
		} else if (queryToLower <= 0.0) {
			return 0.0;
		} else {
			return queryToLower / upperToLower;
		}
	}

	@Override
	public int compareTo(final InterpolableDouble other) {
		if (other.value < value) {
			return 1;
		} else if (other.value > value) {
			return -1;
		} else {
			return 0;
		}
	}
}
