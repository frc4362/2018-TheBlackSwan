package frc.team4362.commands.log;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.subsystems.DifferentialDrive;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
public class StictionCalculator extends Command {
	private static final String OUTPUT_DIR = "/home/lvuser/velocity.log";
	/**
	 * Minimum speed required to count the chassis as "moving"
	 */
	public static final int MOVEMENT_THRESHOLD = 160;

	private final List<WPI_TalonSRX> m_talons;
	private final DifferentialDrive m_driveTrain;

	private int m_cycles;
	private double m_outputSpeed;

	public StictionCalculator(final DifferentialDrive driveBase) {
		m_driveTrain = driveBase;
		m_talons = driveBase.getTalons();
	}

	/**
	 * @param talon The {@link WPI_TalonSRX} to check for movement
	 * @return Whether or not the specified {@link WPI_TalonSRX} is currently moving.
	 */
	public static boolean isMotorMoving(final WPI_TalonSRX talon) {
		return talon.getSelectedSensorVelocity(0) > MOVEMENT_THRESHOLD;
	}

	public void initialize() {
		m_driveTrain.setPIDEnabled(false);

		m_outputSpeed = 0.0;
		m_cycles = 0;

		final File file = new File(OUTPUT_DIR);

		try {
			file.createNewFile();
		} catch (final IOException ioException) {
			throw new RuntimeException("Couldn't log StictionValues!");
		}
	}

	public void execute() {
		m_cycles++;

		// event 7 drive train frames
		if (m_cycles % 7 == 0) {
			// raise the voltage by 0.12
			m_outputSpeed += 0.01;

			// apply it
			m_driveTrain.drive(0, m_outputSpeed);

			final String msg =
					String.format("[Speed = %f, Voltage = %f, Speed = (%d, %d)]\n",
							m_outputSpeed,
							12.0 * m_outputSpeed,
							m_talons.get(0).getSelectedSensorVelocity(0),
							m_talons.get(2).getSelectedSensorVelocity(0));

			try {
				Files.write(Paths.get(OUTPUT_DIR), msg.getBytes(), StandardOpenOption.APPEND);
			} catch (final IOException ioException) {
				throw new RuntimeException("Invalid talon speed logging!");
			}
		}
	}

	public boolean isFinished() {
		final boolean voltageOverrun = m_outputSpeed >= 1.0;
		final boolean hasOvercomeStiction =
				isMotorMoving(m_talons.get(0)) && isMotorMoving(m_talons.get(2));

		return voltageOverrun || hasOvercomeStiction;
	}

	public void end() {
		m_driveTrain.setPIDEnabled(true);
		m_driveTrain.stopDrive();
	}
}
