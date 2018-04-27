package frc.team4362.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.team4362.commands.LiftPositionChange;
import frc.team4362.commands.any.Wait;
import frc.team4362.hardwares.Hardware;

import static frc.team4362.subsystems.Lift.Position.BOTTOM;
import static frc.team4362.subsystems.Lift.Position.TOP;
import static frc.team4362.util.command.Commands.commandOf;

public final class Lift {
	private double m_setpoint;

	public enum Position {
		TOP(1.0),
		SCALE(0.9),
		NEW_SCALE(0.6746987951807228),
		CARRY_PLUS(0.293),
		CARRY(0.233), // used to be .39
		STARTING(0.233),
		CLOSE_THRESHOLD(0.2),
		BOTTOM(-0.006024096385542169);

		public final double positionTicks;

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
		device.setSelectedSensorPosition(
				(int) Position.STARTING.positionTicks,
				0,
				0
		);
		device.configNominalOutputForward(0.25, 0); // used to be 0.15,-0.15.
		device.configNominalOutputReverse(-0.25, 0);
	}

	public Lift(final int portLeft, final int portRight) {
		m_talonLeft = new WPI_TalonSRX(portLeft);
		m_talonLeft.setInverted(false);
		m_talonLeft.setSensorPhase(true);

		m_talonRight = new WPI_TalonSRX(portRight);
		m_talonRight.setInverted(false);

		configureTalon(m_talonLeft);
		configureTalon(m_talonRight);
	}

	private void setTicks(final double ticks) {
		m_setpoint = ticks;
		m_talonLeft.set(ControlMode.Position, ticks);
		m_talonRight.set(ControlMode.Follower, m_talonLeft.getDeviceID());
	}

	public void setLiftPreset(final Position liftPreset) {
		setTicks(liftPreset.positionTicks);
		setTicks(liftPreset.positionTicks);
	}

	public void adjustPosition(final double percent) {
		final double adjustment = percent * LIFT_HEIGHT_TICKS;

		m_setpoint = Math.min(TOP.positionTicks, Math.max(BOTTOM.positionTicks, m_setpoint + adjustment));

		setTicks(m_setpoint);
	}

	public boolean isAtSetpoint() {
		return Math.abs(m_talonLeft.getClosedLoopError(0)) < ALLOWED_ERROR;
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
}
