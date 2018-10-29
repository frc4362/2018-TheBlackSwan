package frc.team4362.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import static frc.team4362.subsystems.Lift.Position.BOTTOM;
import static frc.team4362.subsystems.Lift.Position.TOP;

public final class Lift implements Sendable {
	private double m_setpoint;
	private String m_name, m_subsystem;

	/**
	 * List position presets, to provide adjustable constants
	 */
	public enum Position {
		TOP(1.0),
		SCALE(0.9),
		NEW_SCALE(0.6746987951807228),
		CARRY_PLUS(0.293),
		CARRY(0.233),
		STARTING(0.233),
		CLOSE_THRESHOLD(0.2),
		// bottom position is below the bottom to account for a setpoint
		BOTTOM(-0.006024096385542169);
		public final double positionTicks;

		/**
		 * @param percent The percent extension to put the lift at
		 */
		Position(final double percent) {
			positionTicks = percent * LIFT_HEIGHT_TICKS;
		}
	}

	private static final double
		kP = 0.235,
		kI = 0.0002,
		kD = 0.0,
		kF = 0.01;

	private static final int ALLOWED_ERROR = 400;

	private static final double LIFT_HEIGHT_TICKS = 30100;

	/**
	 * Duplicate this constant for public access
	 * also change the name for consideration of context
	 */
	public static final double CYCLE_LENGTH = LIFT_HEIGHT_TICKS;

	private final WPI_TalonSRX m_talonLeft, m_talonRight;

	private static void configureTalon(final WPI_TalonSRX device) {
		device.configPeakOutputForward(1, 0);
		device.configPeakOutputReverse(-0.8, 0);

		device.config_kP(0, kP, 0);
		device.config_kI(0, kI, 0);
		device.config_kD(0, kD, 0);
		device.config_kF(0, kF, 0);

		device.configAllowableClosedloopError(0, ALLOWED_ERROR, 0);
		// better to jiggle back and forth than power the motors so low they don't drive
		device.configNominalOutputForward(0.25, 0);
		device.configNominalOutputReverse(0.25, 0);
		device.setSelectedSensorPosition(
				(int) Position.STARTING.positionTicks,
				0,
				0);
	}

	/**
	 * @param portLeft Port for the left lift motor
	 * @param portRight Port for the right lift motor
	 */
	public Lift(final int portLeft, final int portRight) {
		setName("Lift");
		setSubsystem("Elevator");

		m_talonLeft = new WPI_TalonSRX(portLeft);
		m_talonLeft.setInverted(false);
		m_talonLeft.setSensorPhase(true);

		m_talonRight = new WPI_TalonSRX(portRight);
		m_talonRight.setInverted(false);

		configureTalon(m_talonLeft);
		configureTalon(m_talonRight);
	}

	/**
	 * The bottom-line method which drives the lift to a specific tick
	 * @param ticks Ticks from the bottom to make the setpoint
	 */
	private void setTicks(final double ticks) {
		m_setpoint = ticks;
		m_talonLeft.set(ControlMode.Position, ticks);
		m_talonRight.set(ControlMode.Follower, m_talonLeft.getDeviceID());
	}

	/**
	 * This is the main way to interface with the lift. Allows movement to specific presets only
	 * @param liftPreset Pre-determined position to drive to
	 */
	public void setLiftPreset(final Position liftPreset) {
		setTicks(liftPreset.positionTicks);
		setTicks(liftPreset.positionTicks);
	}

	/**
	 * For fine adjustment or scrubbing. Effectively abuses positional pid
	 * by moving the setpoint just a bit so the lift can keep up
	 * @param percent Percent of total height to adjust the setpoint by.
	 */
	public void adjustPosition(final double percent) {
		final double adjustment = percent * LIFT_HEIGHT_TICKS;

		m_setpoint = Math.min(TOP.positionTicks, Math.max(BOTTOM.positionTicks, m_setpoint + adjustment));

		setTicks(m_setpoint);
	}

	public boolean isAtSetpoint() {
		return Math.abs(m_talonLeft.getClosedLoopError(0)) < ALLOWED_ERROR;
	}

	/**
	 * Creates a real-time logging object for the lift on the OutlineViewer
	 */
	@Override
	public void initSendable(final SendableBuilder builder) {
		builder.setSmartDashboardType("3-Stage Lift");

		final NetworkTableEntry
				setpointEntry = builder.getEntry("Setpoint"),
				errorEntry = builder.getEntry("Closed-Loop Error");

		builder.setUpdateTable(() -> {
			setpointEntry.setDouble(m_talonLeft.get());
			errorEntry.setDouble(m_talonLeft.getClosedLoopError(0));
		});
	}

	public WPI_TalonSRX getTalonLeft() {
		return m_talonLeft;
	}

	public WPI_TalonSRX getTalonRight() {
		return m_talonRight;
	}

	public double getSetpoint() {
		return m_setpoint;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public void setName(final String name) {
		m_name = name;
	}

	@Override
	public String getSubsystem() {
		return m_subsystem;
	}

	@Override
	public void setSubsystem(final String subsystem) {
		m_subsystem = subsystem;
	}
}
