package frc.team4362.kinematics.math;

public interface InverseInterpolable<T> {
	double inverseInterpolate(T upper, T query);
}
