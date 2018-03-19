package frc.team4362.util.command;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team4362.hardwares.Hardware;

import java.sql.Driver;

import static frc.team4362.util.command.Commands.commandOf;

@SuppressWarnings("WeakerAccess")
public abstract class PowerUpCommandGroup extends CommandGroup {
	@Override
	public final void initialize() {
		// there used to be more here
		Hardware.getInstance().getIntakes().getMouth().set(DoubleSolenoid.Value.kForward);
	}

	protected final char getOurSwitchSide() {
		return DriverStation.getInstance().getGameSpecificMessage().charAt(0);
	}

	protected final char getOurScaleSide() {
		return DriverStation.getInstance().getGameSpecificMessage().charAt(1);
	}

	public abstract void init();
}
