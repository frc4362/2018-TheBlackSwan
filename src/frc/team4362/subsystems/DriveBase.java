package frc.team4362.subsystems;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * *Our* drive class.
 * Originally purely a wrapper around WPILib's DriveTrain
 * Since then it has evolved into entirely our own class with a lot less bloat
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class DriveBase {
	private ControlMode m_controlMode;
	private final List<WPI_TalonSRX> m_driveTalons;
	private final WPI_TalonSRX
		m_talonLeftMaster,
		m_talonRightMaster,
		m_talonLeftSlave,
		m_talonRightSlave;

	private enum DriveSide {
		// mule numbers
//		LEFT_TELEOP(5800, 0.7*.45, 0.001, 0, 0.1763793), // p used to be 0.6
//		RIGHT_TELEOP(5800, 0.315, 0.001, 0, 0.1763793);

		// normal numbers
		// i used to be 0.001
		LEFT_TELEOP(2100, 0.3, 0.001, 0.0, 0.48714285714285716),
		RIGHT_TELEOP(2100, 0.3, 0.001, 0.0, 0.48714285714285716),

		// new ones for tuning
		LEFT_NEW(2100, 300, 5, 0, 0.48714285714285716),
		RIGHT_NEW(2100, 300, 5, 0, 0.48714285714285716);

		public final double kP, kI, kD, kF, targetSpeed;

		DriveSide(
				final double tSpeed,
				final double p,
				final double i,
				final double d,
				final double f
		) {
			targetSpeed = tSpeed;

			kP = p;
			kI = i;
			kD = d;
			kF = f;
		}
	}

	private static void configureTalonVoltage(final WPI_TalonSRX device) {
		device.configPeakOutputForward(1.0f, 0);
		device.configPeakOutputReverse(-1.0f, 0);

		device.configNominalOutputForward(0.0, 0);
		device.configNominalOutputReverse(0.0, 0);
	}

	private static void configureTalonFront(final WPI_TalonSRX device, final DriveSide vars) {
		device.config_kP(0, vars.kP, 0);
		device.config_kI(0, vars.kI, 0);
		device.config_kD(0, vars.kD, 0);
		device.config_kF(0, vars.kF, 0);
	}

	// front left, back left, front right, back right
	public DriveBase(final Integer... ports) {
		if (ports.length != 4) {
			throw new RuntimeException("Invalid amount of ports passed to DriveBase::DriveBase!");
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
	}

	/**
	 * Allows the motors to be driven from -1 to +1
	 * without worrying about whether or not velocity control is enabled
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

	public void driveAuton(
			final double speedLeft,
			final double speedRight
	) {
		m_talonLeftMaster.set(m_controlMode, speedLeft * getTargetSpeed(DriveSide.LEFT_NEW));
		m_talonLeftSlave.set(ControlMode.Follower, m_talonLeftMaster.getDeviceID());

		m_talonRightMaster.set(m_controlMode, speedRight * getTargetSpeed(DriveSide.RIGHT_NEW));
		m_talonRightSlave.set(ControlMode.Follower, m_talonRightMaster.getDeviceID());
	}

	public void setPIDEnabled(final boolean usePID) {
		m_controlMode = usePID ? ControlMode.Velocity : ControlMode.PercentOutput;
	}

	public boolean isPIDMode() {
		return m_controlMode == ControlMode.Velocity;
	}

	private double getTargetSpeed(final DriveSide side) {
		return isPIDMode() ? side.targetSpeed : 1.0;
	}

	public List<WPI_TalonSRX> getTalons() {
		return m_driveTalons;
	}
}
