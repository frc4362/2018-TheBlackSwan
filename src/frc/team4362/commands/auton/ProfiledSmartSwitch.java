package frc.team4362.commands.auton;

import frc.team4362.commands.RunIntakes;
import frc.team4362.Hardware;
import frc.team4362.profiling.ProfileFollowerBuilder;
import frc.team4362.subsystems.Lift;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.command.PowerUpCommandGroup;

import static frc.team4362.util.command.Commands.commandOf;

public class ProfiledSmartSwitch extends PowerUpCommandGroup {
	@Override
	public void init() {
		addSequential(commandOf(() ->
			Hardware.getInstance().getLift().setLiftPreset(Lift.Position.CARRY_PLUS)));

		if (getOurSwitchSide() == 'L') {
			addSequential(ProfileFollowerBuilder.loadProfile("switch_l", false));
		} else {
			addSequential(ProfileFollowerBuilder.loadProfile("switch_r", false));
		}

		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING_BUT_FAST, 1500));
	}
}
