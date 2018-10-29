package frc.team4362.commands.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team4362.commands.LiftPositionChange;
import frc.team4362.commands.RunIntakes;
import frc.team4362.commands.any.Wait;
import frc.team4362.Hardware;
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
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING_BUT_FAST,1500));

		addSequential(Navigate.to(0, -48, 0, -0.3, 5000));
		addSequential(new LiftPositionChange(Lift.Position.BOTTOM, 0));
		addSequential(Navigate.to(0, 0,
				44.5 * (getOurSwitchSide() == 'L' ? 1 : -1), 1, 4000));
		addSequential(Navigate.to(0, 24, 0, 0.6, 4000));
		addSequential(new CommandGroup() {
			{
				addParallel(new RunIntakes(IntakeWheelSet.SpeedPreset.INTAKING, 4000));
				addParallel(Navigate.to(0, 20, 0, 0.4, 1500));
			}
		});
	}
}
