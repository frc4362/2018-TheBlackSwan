package frc.team4362.kinematics.math;

public interface Interpolable<T> {
	T interpolate(final T other, final double n);
}
