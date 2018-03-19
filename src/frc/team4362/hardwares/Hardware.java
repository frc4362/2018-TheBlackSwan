package frc.team4362.hardwares;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.*;
import frc.team4362.Config;
import frc.team4362.subsystems.*;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.MyAHRS;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class Hardware {
	private static Hardware INSTANCE = null;

	private final DriveBase m_driveTrain;
	private final AHRS m_gyro;
	private final DoubleSolenoid m_shifter;
	private final Climber m_climber;
	private final Intakes m_intakes;
	private final Lift m_lift;

	protected Hardware() {
		m_lift = new Lift(43, 44); // used to be 43/44
		m_climber = new Climber(41);
		m_intakes = new Intakes(
				new DoubleSolenoid(6, 7),
				new IntakeWheelSet(21, 22),
				new IntakeWheelSet(31, 32)
		);
		m_driveTrain = new DriveBase(57, 58, 59, 60);

		m_gyro = new MyAHRS(SPI.Port.kMXP);
		m_shifter = new DoubleSolenoid(0, 1);
	}

	public static Hardware getInstance() {
		if (Objects.isNull(INSTANCE)) {
			if (Config.USE_CAMERA && CameraHardware.isCamerasPresent()) {
				INSTANCE = new CameraHardware();
			} else {
				INSTANCE = new Hardware();
			}
		}

		return INSTANCE;
	}

	public DriveBase getDriveTrain() {
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

	public Climber getClimber() {
		return m_climber;
	}
}
