package frc.team4362.commands.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team4362.commands.LiftPositionChange;
import frc.team4362.commands.RunIntakes;
import frc.team4362.hardwares.Hardware;
import frc.team4362.subsystems.Lift;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.command.PowerUpCommandGroup;

import static frc.team4362.util.command.Commands.commandOf;

public class EnginerdsAuton extends PowerUpCommandGroup {
	@Override
	public void init() {
		addSequential(commandOf(() ->
			Hardware.getInstance().getLift().setLiftPreset(Lift.Position.CARRY_PLUS)));

		addSequential(Navigate.to(0, 10, 0, 0.5, 1000));

		final char switchSide = getOurSwitchSide();
		final double multiplier = getMultiplier(switchSide);

		if (getOurSwitchSide() == 'L') {
			addSequential(Navigate.to(0, 0, -26f, 1, 2000));
			addSequential(Navigate.to(0, 140, 0, 1,1800));
		} else {
			addSequential(Navigate.to(0, 0, 30, 1, 2000));
			addSequential(Navigate.to(0, 140, 0, 1, 1800));
		}

		addSequential(Navigate.to(0, 15, 0, 0.5, 1000));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING, 300));
//
//		addSequential(new LiftPositionChange(Lift.Position.BOTTOM, 0));
//		addSequential(Navigate.to(0, -48, 0, -0.7, 2000));
//		addSequential(
//				Navigate.to(0, 0,
//					getDegrees(switchSide), 2, 3000));
//
//		addSequential(Navigate.to(0, 40, 0, 0.7, 4000));
//		addSequential(new CommandGroup() {
//			{
//				addParallel(new RunIntakes(IntakeWheelSet.SpeedPreset.INTAKING), 1.5);
//				addParallel(Navigate.to(0, 22, 0, 0.45, 1500));
//			}
//		}, 1.501);
//		// after we grab cube
//
//		addSequential(Navigate.to(0, -20, 0, -0.7, 4000));
//		addSequential(Navigate.to(0, 0, getSecondDegrees(switchSide), 1, 5000));
//		addSequential(Navigate.to(0, 108, 0, 0.9, 4000));
	}

	private double getMultiplier(final char switchSide) {
		if (switchSide == 'L') {
			return 1.0;
		} else {
			return -1.0;
		}
	}

	private double getDegrees(final char switchSide) {
		if (switchSide == 'L') {
			return 26.5;
		} else {
			return -26.5;
		}
	}

//	private double getSecondDegrees(final char switchSide) {
//		if (switchSide == 'L') {
//			return -72.0;
//		} else if (switchSide == 'R') {
//			return 72.0;
//		}
//	}

//
//	private double getSecondDegrees(final char switchSide) {
//		if (switchSide == 'L' && getOurScaleSide() == 'L') {
//			return -72.0;
//		} else if (switchSide == 'R' && ){
//			return 72;
//		}
//	}
}
