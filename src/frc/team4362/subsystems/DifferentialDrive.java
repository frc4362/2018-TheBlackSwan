package frc.team4362.subsystems;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import static java.lang.Math.copySign;
import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * *Our* drive class.
 * Originally purely a wrapper around WPILib's DriveTrain
 * Since then it has evolved into entirely our own class with a lot less bloat
 * 																this used to be true
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class DifferentialDrive implements Sendable {
	private String m_name, m_subsystem;
	private ControlMode m_controlMode;

	// native units (nu) / 100 milliseconds
	// 21000nu / second = ~5.12 rotations / second
	// ~307RPM = 5.47mph
	public static final int TARGET_DRIVE_SPEED = 2100;

	private final List<WPI_TalonSRX> m_driveTalons;
	private final WPI_TalonSRX
		m_talonLeftMaster,
		m_talonRightMaster,
		m_talonLeftSlave,
		m_talonRightSlave;

	// how severely quickturn adjusts stuff
	private static final double LIMIT = 1.0;
	private static final double QUICKSTOP_THRESHOLD = 0.2;
	private static final double kTurnSensitivity = 1.0;

	// 0.225 * 12V = 2.7V of stiction
	private static final double kStaticFriction = 0.225;

	private static final String INVALID_PORTS_MSG =
			"Invalid amount of talon ports passed to DifferentialDrive::DifferentialDrive!";

	private static final String[] TALON_NAMES = {
			"Front Left",
			"Back Left",
			"Front Right",
			"Back Right"
	};

	private double m_quickStopAccumulator;

	// deadband a val
	private double limit(final double v) {
		return Math.abs(v) < LIMIT ? v : LIMIT * Math.signum(v);
	}

	/**
	 * Allow different tuning values for each side, or even auton
	 */
	private enum DriveSide {
		LEFT_TELEOP(0.3, 0.001, 0.0),
		RIGHT_TELEOP(0.3, 0.001, 0.0),

		LEFT_NEW(300, 5, 0),
		RIGHT_NEW(300, 5, 0);

		public final double kP, kI, kD, kF, targetSpeed;

		DriveSide(
				final double p,
				final double i,
				final double d
		) {
			targetSpeed = (double) DifferentialDrive.TARGET_DRIVE_SPEED;

			kP = p;
			kI = i;
			kD = d;
			kF = 0.48714285714285716;
		}
	}

	private static void configureTalonVoltage(final WPI_TalonSRX device) {
		device.configPeakOutputForward(1.0f, 0);
		device.configPeakOutputReverse(-1.0f, 0);
	}

	/**
	 * Allows us to overcome static friction before applying actual drive power
	 * @param enabled Turns nominal voltage on and off
	 */
	public void setNominalVoltage(final boolean enabled) {
		m_driveTalons.forEach(device -> {
			if (enabled) {
				device.configNominalOutputForward(kStaticFriction, 0);
				device.configNominalOutputReverse(-kStaticFriction, 0);
			} else {
				device.configNominalOutputForward(0, 0);
				device.configNominalOutputReverse(0, 0);
			}
		});
	}

	private static void configureTalonFront(
			final WPI_TalonSRX device,
			final DriveSide vars
	) {
		device.config_kP(0, vars.kP, 0);
		device.config_kI(0, vars.kI, 0);
		device.config_kD(0, vars.kD, 0);
		device.config_kF(0, vars.kF, 0);
	}

	/**
	 * Allows for the construction of the {@link DifferentialDrive} with four talon ports
	 * @param ports Front left, back left, front right, back right drive ports
	 */
	public DifferentialDrive(final Integer... ports) {
		setName("Drive Train 2");
		setSubsystem("Chassis");

		if (ports.length != 4) {
			throw new RuntimeException(INVALID_PORTS_MSG);
		}

		m_controlMode = ControlMode.Velocity;

		m_driveTalons = Arrays.stream(ports)
				.map(WPI_TalonSRX::new)
				.collect(Collectors.toList());

		m_driveTalons.forEach(talon -> {
			configureTalonVoltage(talon);
			talon.setNeutralMode(NeutralMode.Brake);
			talon.setSelectedSensorPosition(0, 0, 0);
		});

		m_talonLeftMaster = m_driveTalons.get(0);
		m_talonLeftMaster.setInverted(false);
		m_talonLeftMaster.setSensorPhase(false);
		configureTalonFront(m_talonLeftMaster, DriveSide.LEFT_TELEOP);

		m_talonLeftSlave = m_driveTalons.get(1);
		m_talonLeftSlave.setInverted(false);
		m_talonLeftSlave.setSensorPhase(false);

		m_talonRightMaster = m_driveTalons.get(2);
		m_talonRightMaster.setInverted(true);
		m_talonRightMaster.setSensorPhase(false);
		configureTalonFront(m_talonRightMaster, DriveSide.RIGHT_TELEOP);

		m_talonRightSlave = m_driveTalons.get(3);
		m_talonRightSlave.setInverted(true);
		m_talonRightSlave.setSensorPhase(false);

		setNominalVoltage(true);
	}

	/**
	 * Allows motors to be driven from [-1.0,+1.0] without worrying about control mode
	 * @param speedLeft Percent output from the left drive train
	 * @param speedRight Percent output from the left drive train
	 */
	public void drive(
			final double speedLeft,
			final double speedRight
	) {
		m_talonLeftMaster.set(m_controlMode, speedLeft * getTargetSpeed(DriveSide.LEFT_TELEOP));
		m_talonLeftSlave.set(ControlMode.Follower, m_talonLeftMaster.getDeviceID());

		m_talonRightMaster.set(m_controlMode, speedRight * getTargetSpeed(DriveSide.RIGHT_TELEOP));
		m_talonRightSlave.set(ControlMode.Follower, m_talonRightMaster.getDeviceID());
	}

	/**
	 * Quickturn smoothness. Determines how severely it will stop you
	 * if you quickturn while driving.
	 */
	public enum QuickTurnKind {
		OFF(-1), FAST(0.1), SLOW(0.03);

		public final double alpha;

		QuickTurnKind(final double turn) {
			alpha = turn;
		}
	}

	/**
	 * Commonly known also as Cheesy Drive, allows two handed arcade style control
	 * for better expressiveness and intuition
	 * @param xSpeed Intended velocity of the robot
	 * @param zRotation Angular power of the drive
	 * @param quickTurnKind Quickturn setting
	 */
	public void driveCurvature(
			final double xSpeed,
			double zRotation,
			final QuickTurnKind quickTurnKind
	) {

		double overPower, angularPower;

		if (quickTurnKind != QuickTurnKind.OFF) {
			if (Math.abs(xSpeed) < QUICKSTOP_THRESHOLD) {
				final double alpha = quickTurnKind.alpha;
				m_quickStopAccumulator =
						(1 - alpha) * m_quickStopAccumulator + alpha * limit(zRotation) * 2;
			}

			overPower = 1.0;
			angularPower = -zRotation;
		} else {
			overPower = 0.0;
			zRotation *= -Math.signum(xSpeed);
			angularPower = Math.abs(xSpeed) * zRotation * kTurnSensitivity - m_quickStopAccumulator;

			if (m_quickStopAccumulator > 1) {
				m_quickStopAccumulator -= 1;
			} else if (m_quickStopAccumulator < -1) {
				m_quickStopAccumulator += 1;
			} else {
				m_quickStopAccumulator = 0.0;
			}
		}

		double leftPower = xSpeed - angularPower,
				rightPower = xSpeed + angularPower;

		if (leftPower > 1.0) {
			rightPower -= overPower * (leftPower - 1.0);
			leftPower = 1.0;
		} else if (rightPower > 1.0) {
			leftPower -= overPower * (rightPower - 1.0);
			rightPower = 1.0;
		} else if (leftPower < -1.0) {
			rightPower += overPower * (-1.0 - leftPower);
			leftPower = -1.0;
		} else if (rightPower < -1.0) {
			leftPower += overPower * (-1.0 - rightPower);
			rightPower = -1.0;
		}

		drive(leftPower, rightPower);
	}

	/**
	 * Set both motors to 0 throttle.
	 */
	public void stopDrive() {
		drive(0, 0);
	}

	/**
	 * One-handed, 2-axis robot control.
	 * @param speed Forward and backwards speed
	 * @param rotation Factor to apply to each wheel differentially to turn
	 * @param squareInput Allows one to scale the input exponentially for greater low-speed control
	 */
	public void driveArcade(
			double speed,
			final double rotation,
			final boolean squareInput
	) {
		if (squareInput) {
			speed = copySign(speed * speed, speed);
		}

		final double maxOutput = copySign(max(abs(speed), abs(rotation)), speed);
		final boolean
				forward = speed >= 0.0,
				right = rotation >= 0.0;

		final double speedLeft, speedRight;

		if (forward) {
			if (right) {
				speedLeft = maxOutput;
				speedRight = speed - rotation;
			} else {
				speedLeft = speed + rotation;
				speedRight = maxOutput;
			}
		} else {
			if (right) {
				speedLeft = speed + rotation;
				speedRight = maxOutput;
			} else {
				speedLeft = maxOutput;
				speedRight = speed - rotation;
			}
		}

		drive(speedLeft, speedRight);
	}

	/**
	 * Enables and disables velocity-based drive train control
	 * @param usePID Should velocity mode be enabled?
	 */
	public void setPIDEnabled(final boolean usePID) {
		m_controlMode = usePID ? ControlMode.Velocity : ControlMode.PercentOutput;
	}

	/**
	 * @return whether or not we're currently controlling for Velocity.
	 * @see DifferentialDrive#setPIDEnabled(boolean)
	 */
	public boolean isPIDMode() {
		return m_controlMode == ControlMode.Velocity;
	}

	/**
	 * @param side Left and right side might have different settings- use this
	 * @return
	 * Returns the current "maximum" speed for the selected drive mode
	 */
	private double getTargetSpeed(final DriveSide side) {
		return isPIDMode() ? side.targetSpeed : 1.0;
	}

	/**
	 * @return A list of the four drive talons in the order front left, back left, front right, back right
	 */
	public List<WPI_TalonSRX> getTalons() {
		return m_driveTalons;
	}

	/**
	 * Builds a real-time logging object to be given to the OutlineViewer
	 * @param builder This is used internally by WPILib, don't worry about it
	 */
	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("West Coast Drive");

		final List<Runnable> updaters = IntStream.range(0, 4).<Runnable>mapToObj(i -> {
			final WPI_TalonSRX talon = m_driveTalons.get(i);
			final String name = TALON_NAMES[i];
			final String id = Integer.toString(talon.getDeviceID());

			final NetworkTableEntry
					setSpeedEntry = builder.getEntry(name + " Set Speed"),
					speedEntry = builder.getEntry(name + " Encoder Speed"),
					errorEntry = builder.getEntry(name + " Closed-Loop Error"),
					positionEntry = builder.getEntry(name + " Position");

			builder.getEntry(name + " CAN ID").setString(id);

			return () -> {
				setSpeedEntry.setDouble(talon.get());
				speedEntry.setDouble(talon.getSelectedSensorVelocity(0));
				errorEntry.setDouble(talon.getClosedLoopError(0));
				positionEntry.setDouble(talon.getSelectedSensorPosition(0));
			};
		}).collect(Collectors.toList());

		builder.setUpdateTable(() ->
			updaters.forEach(Runnable::run));
	}

	@Override
	public void setName(String name) {
		m_name = name;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public void setSubsystem(String name) {
		m_subsystem = name;
	}

	@Override
	public String getSubsystem() {
		return m_subsystem;
	}
}
