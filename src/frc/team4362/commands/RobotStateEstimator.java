package frc.team4362.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.kinematics.Kinematics;
import frc.team4362.kinematics.RobotState;
import frc.team4362.kinematics.motion.Rotation;
import frc.team4362.kinematics.motion.Twist;
import frc.team4362.subsystems.DifferentialDrive;

import java.util.function.Supplier;

public final class RobotStateEstimator extends Command {
	public static final double dt = 0.02;

	private double m_encoderPrevDistanceLeft;
	private double m_encoderPrevDistanceRight;

	private DifferentialDrive m_driveTrain;
	private Supplier<Double> m_angleSupplier;

	public RobotStateEstimator(
			final DifferentialDrive driveTrain,
			final Supplier<Double> angleSupplier
	) {
		m_encoderPrevDistanceLeft = 0.0;
		m_encoderPrevDistanceRight = 0.0;

		m_driveTrain = driveTrain;
		m_angleSupplier = angleSupplier;
	}

	@Override
	public void initialize() {
		m_encoderPrevDistanceLeft = m_driveTrain.getInchesDrivenLeft();
		m_encoderPrevDistanceRight = m_driveTrain.getInchesDrivenRight();
	}

	@Override
	public void execute() {
		final double distanceLeft = m_driveTrain.getInchesDrivenLeft(),
				distanceRight = m_driveTrain.getInchesDrivenRight();

		final Rotation angle = Rotation.fromDegrees(m_angleSupplier.get());
		final Twist velocityMeasured = RobotState.getInstance().generateOdometry(
				distanceLeft - m_encoderPrevDistanceLeft,
				distanceRight - m_encoderPrevDistanceRight,
				angle);
		final Twist velocityPredicted = Kinematics.forwardKinematics(
				m_driveTrain.getInchesPerSecondLeft() * dt,
				m_driveTrain.getInchesPerSecondRight() * dt);

		RobotState.getInstance().addObservations(
				Timer.getFPGATimestamp(),
				velocityMeasured,
				velocityPredicted);

		m_encoderPrevDistanceLeft = distanceLeft;
		m_encoderPrevDistanceRight = distanceRight;
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
