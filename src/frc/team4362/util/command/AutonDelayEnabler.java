package frc.team4362.util.command;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@SuppressWarnings({"WeakerAccess", "SpellCheckingInspection"})
public class AutonDelayEnabler extends SendableChooser<Boolean> {
	public AutonDelayEnabler() {
		addDefault("Auton Delay: Disabled", false);
		addObject("Auton Delay: Enabled", true);

		SmartDashboard.putData("auton delayyeerr", this);
	}
}
