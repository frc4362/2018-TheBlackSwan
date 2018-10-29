package frc.team4362.util;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import jaci.pathfinder.Pathfinder;

/**
 * Wrapper class for our AHRS/Nav MXP
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MyAHRS extends AHRS implements Sendable {
	public MyAHRS(final SPI.Port id) {
		super(id);
	}

	/**
	 * @return [-180,+180] degrees instead of infinite in either direction
	 */
	public double getHalfAngle() {
		return Pathfinder.boundHalfDegrees(getAngle());
	}

	/**
	 * Creates a real-time logging object for use on OutlineViewer/LiveWindow
	 */
	@Override
	public void initSendable(final SendableBuilder builder) {
		builder.setSmartDashboardType("navMXP");

		final NetworkTableEntry headingEntry =
				builder.getEntry("Heading");

		builder.setUpdateTable(() ->
			headingEntry.setDouble(getHalfAngle()));
	}
}
