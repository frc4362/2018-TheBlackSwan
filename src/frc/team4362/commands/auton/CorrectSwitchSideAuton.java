package frc.team4362.commands.auton;

import frc.team4362.commands.RunIntakes;
import frc.team4362.commands.any.Wait;
import frc.team4362.hardwares.Hardware;
import frc.team4362.subsystems.Lift;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.command.PowerUpCommandGroup;

import static frc.team4362.util.command.Commands.commandOf;

public class CorrectSwitchSideAuton extends PowerUpCommandGroup {
	@Override
	public void init() {
		addSequential(commandOf(() ->
			Hardware.getInstance().getLift().setLiftPreset(Lift.Position.CARRY_PLUS)));

		if (getOurSwitchSide() == 'R') {						// used to be 0.4
			addSequential(Navigate.to(33, 33, 90, 0.8, 4000));
			addSequential(Navigate.to(0, 0, -90, 4, 4700));
			addSequential(new Wait(250));
			addSequential(new DriveDistanceRamp(64, 0.8, 1750));
		} else { // 												used to be 0.4
			addSequential(Navigate.to(-35.5, 35.5, -90, 0.8, 4000));
			addSequential(Navigate.to(0, 0, 70, 4, 4700));
			addSequential(new Wait(250));
			addSequential(new DriveDistanceRamp(48, 0.8, 1750));
		}

		addSequential(new Wait(500));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.75);
		addSequential(Navigate.to(0, -30, 0, -0.3, 5000));
//
//		addSequential(commandOf(() ->
//			Hardware.getInstance().getLift().setLiftPreset(Lift.Position.BOTTOM)));
//		addSequential(commandOf(() ->
//			Hardware.getInstance().getLift().setLiftPreset(Lift.Position.BOTTOM)));
//		addSequential(commandOf(() ->
//			Hardware.getInstance().getLift().setLiftPreset(Lift.Position.BOTTOM)));
//
//		if (getOurSwitchSide() == 'R') {
//			addSequential(Navigate.to(0, 0, -45, 2345, 3000));
//		} else {
//			addSequential(Navigate.to(0, 0, 45, 2345, 3000));
//		}
//
//		addSequential(commandOf(() ->
//			Hardware.getInstance().getLift().setLiftPreset(Lift.Position.BOTTOM)));
	}
}
