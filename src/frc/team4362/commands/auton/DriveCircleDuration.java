package frc.team4362.commands.auton;

@SuppressWarnings("WeakerAccess")
public class DriveCircleDuration extends DriveCircle {
	private final long m_duration;

	public DriveCircleDuration(final double speed, final double radius, final long duration) {
		super(speed, radius);
		m_duration = duration;
	}

	public boolean isFinished() {
		return getRuntime() > m_duration;
	}
}
