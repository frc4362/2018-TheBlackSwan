package frc.team4362;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.*;
import frc.team4362.subsystems.*;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.MyAHRS;

import java.util.Objects;

/**
 * A singleton allowing for protected instances of the hardware
 */
@SuppressWarnings("WeakerAccess")
public class Hardware {
	private static Hardware INSTANCE = null;

	private final DifferentialDrive m_driveTrain;
	private final AHRS m_gyro;
	private final DoubleSolenoid m_shifter;
	private final WPI_TalonSRX m_climber1, m_climber2;
	private final Intakes m_intakes;
	private final Lift m_lift;
	private final ShifterLEDs m_lights;

	protected Hardware() {
		m_lift = new Lift(43, 44); // used to be 43/44
		m_climber1 = new WPI_TalonSRX(41);
		m_climber2 = new WPI_TalonSRX(42);
		m_intakes = new Intakes(
				new DoubleSolenoid(6, 7),
				new IntakeWheelSet(21, 22),
				new IntakeWheelSet(31, 32)
		);
		m_driveTrain = new DifferentialDrive(57, 58, 59, 60);

		m_gyro = new MyAHRS(SPI.Port.kMXP);
		m_shifter = new DoubleSolenoid(0, 1);
		m_lights = new ShifterLEDs(0, m_shifter);
	}

	public static Hardware getInstance() {
		if (Objects.isNull(INSTANCE)) {
			INSTANCE = new Hardware();
		}

		return INSTANCE;
	}

	public DifferentialDrive getDriveTrain() {
		return m_driveTrain;
	}

	public AHRS getMXP() {
		return m_gyro;
	}

	public DoubleSolenoid getShifter() {
		return m_shifter;
	}

	public Intakes getIntakes() {
		return m_intakes;
	}

	public Lift getLift() {
		return m_lift;
	}

	public WPI_TalonSRX getClimber1() {
		return m_climber1;
	}

	public WPI_TalonSRX getClimber2() {
		return m_climber2;
	}

	public ShifterLEDs getLEDs() {
		return m_lights;
	}
}
