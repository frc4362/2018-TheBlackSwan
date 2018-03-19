package frc.team4362.commands.log;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.subsystems.DriveBase;

import java.util.Arrays;

public class ExternalLogTalonSpeeds extends Command {
	private static final NetworkTableInstance NETWORK_TABLE_INSTANCE = NetworkTableInstance.getDefault();

	private final DriveBase m_driveBase;

	private int m_runs;

	public ExternalLogTalonSpeeds(final DriveBase driveBase) {
		m_driveBase = driveBase;
		m_runs = 0;
	}

	@Override
	public void execute() {
		if (m_runs % 2 == 0) {
			final double speedLeft = m_driveBase.getTalons().get(0).get();
			final double speedRight = m_driveBase.getTalons().get(2).get();

			final double[] leftOld =
					NETWORK_TABLE_INSTANCE.getTable("pid")
							.getEntry("outLeft").getDoubleArray(new double[0]);

			final double[] rightOld =
					NETWORK_TABLE_INSTANCE.getTable("pid")
							.getEntry("outRight").getDoubleArray(new double[0]);

			final int length = leftOld.length + 1;

			final double[] leftNew = Arrays.copyOf(leftOld, length),
				rightNew = Arrays.copyOf(rightOld, length);

			leftNew[length - 1] = speedLeft;
			rightNew[length - 1] = speedRight;

			NETWORK_TABLE_INSTANCE.getTable("pid")
					.getEntry("outLeft")
					.setDoubleArray(leftNew);

			NETWORK_TABLE_INSTANCE.getTable("pid")
					.getEntry("outRight")
					.setDoubleArray(rightNew);
		}

		m_runs++;
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
