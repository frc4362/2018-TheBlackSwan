package frc.team4362.profiling;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.Constants;
import frc.team4362.Hardware;
import jaci.pathfinder.followers.EncoderFollower;

import static jaci.pathfinder.Pathfinder.boundHalfDegrees;
import static jaci.pathfinder.Pathfinder.r2d;

/**
 * ProfileFollower for following a pre-generated motion profile
 * Please only make with {@link ProfileFollowerBuilder}
 */
@SuppressWarnings("WeakerAccess")
public class ProfileFollower extends Command {
	public static final double DEFAULT_K_TURN = 0.01;

	private final WPI_TalonSRX m_talonLeft;
	private final WPI_TalonSRX m_talonRight;
	private final EncoderFollower m_followerLeft;
	private final EncoderFollower m_followerRight;
	private final double m_kTurn;
	private final int m_kDirection;

	private boolean m_isFinished;
	private final boolean m_reversed;

	/**
	 * please use ProfileBuilderFollower
	 * please
	 * @param kTurn Determines the sensitivity of the runtime heading adjustment, default = 0.01
	 * @param reversed Whether the profile is meant to be run in reverse
	 */
	public ProfileFollower(
			final WPI_TalonSRX talonLeft,
			final WPI_TalonSRX talonRight,
			final EncoderFollower followerLeft,
			final EncoderFollower followerRight,
			final double kTurn,
			final boolean reversed
	) {
		m_talonLeft = talonLeft;
		m_talonRight = talonRight;
		m_followerLeft = followerLeft;
		m_followerRight = followerRight;
		m_kTurn = kTurn;
		m_reversed = reversed;
		m_kDirection = m_reversed ? -1 : 1;
	}

	@Override
	public void initialize() {
		System.out.println("Profile beginning!");
		// must be turned off for small corrections
		Hardware.getInstance().getDriveTrain().setNominalVoltage(false);
		// don't need our systems fighting :')
		Hardware.getInstance().getDriveTrain().setPIDEnabled(false);
		Hardware.getInstance().getMXP().reset();

		m_isFinished = false;

		// configure starting point for each run- it might not be the first profile
		m_followerLeft.configureEncoder(
				m_talonLeft.getSelectedSensorPosition(0) * m_kDirection,
				(int) Constants.COUNTS_PER_ROTATION_QUADRATURE,
				Constants.WHEEL_DIAMETER_INCHES);
		m_followerRight.configureEncoder(
				m_talonRight.getSelectedSensorPosition(0) * m_kDirection,
				(int) Constants.COUNTS_PER_ROTATION_QUADRATURE,
				Constants.WHEEL_DIAMETER_INCHES);
	}

	@Override
	public void execute() {
		// lots of variables but I think it's better than inline math
		final int positionLeft = m_talonLeft.getSelectedSensorPosition(0),
				positionRight = m_talonRight.getSelectedSensorPosition(0),
				adjustedLeft = m_kDirection * positionLeft,
				adjustedRight = m_kDirection * positionRight;

		final double // calculate both the output values and the required heading adjustments
				outputLeft = m_followerLeft.calculate(adjustedLeft),
				outputRight = m_followerRight.calculate(adjustedRight),
				heading = -Hardware.getInstance().getMXP().getYaw(),
				desiredHeading = r2d(m_followerLeft.getHeading()),
				angleDifference = boundHalfDegrees(desiredHeading - heading),
				turn = -m_kTurn * angleDifference;

		// mutable stuff :disgust:
		double percentVBusLeft = outputLeft / Profiling.MAX_VELOCITY,
			    percentVBusRight = outputRight / Profiling.MAX_VELOCITY;

		if (m_reversed) {
			final double tempLeft = percentVBusLeft;
			percentVBusLeft = -(percentVBusRight - turn);
			percentVBusRight = -(tempLeft + turn);
		} else {
			percentVBusLeft += turn;
			percentVBusRight += turn;
		}

		Hardware.getInstance().getDriveTrain().drive(
				percentVBusLeft,
				percentVBusRight
		);

		m_isFinished = outputLeft == 0.0d && outputRight == 0.0d;
	}

	@Override
	public boolean isFinished() {
		return m_isFinished;
	}

	@Override
	public void end() {
		// make sure to turn it back on
		Hardware.getInstance().getDriveTrain().stopDrive();
		Hardware.getInstance().getDriveTrain().setPIDEnabled(true);
		Hardware.getInstance().getDriveTrain().setNominalVoltage(true);
	}
}
