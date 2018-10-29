package frc.team4362.util.command;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import frc.team4362.Hardware;

/**
 * A game-specific implementation of {@link RuntimeCommandGroup} for FRC2018 Power-Up
 */
@SuppressWarnings("WeakerAccess")
public abstract class PowerUpCommandGroup extends RuntimeCommandGroup {
	@Override
	public final void initialize() {
		// there used to be more here
		Hardware.getInstance().getIntakes().getMouth().set(DoubleSolenoid.Value.kForward);
	}

	protected char getOurSwitchSide() {
		return DriverStation.getInstance().getGameSpecificMessage().charAt(0);
	}

	protected char getOurScaleSide() {
		return DriverStation.getInstance().getGameSpecificMessage().charAt(1);
	}
}
