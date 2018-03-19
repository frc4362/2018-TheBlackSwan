package frc.team4362.commands.auton;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.team4362.commands.LiftPositionChange;
import frc.team4362.commands.RunIntakes;
import frc.team4362.hardwares.Hardware;
import frc.team4362.subsystems.Lift;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.command.PowerUpCommandGroup;

import static frc.team4362.util.command.Commands.commandOf;

@Deprecated
public final class MegaAutonSwitchPriority extends PowerUpCommandGroup {
	public enum Side {
		LEFT(1, 'L'),
		RIGHT(-1, 'R');

		public final int multiplier;
		public final char character;

		Side(final int m, final char ch) {
			multiplier = m;
			character = ch;
		}
	}

	private final Side m_side;

	public MegaAutonSwitchPriority(final Side s) {
		m_side = s;
	}

	@Override
	public void init() {
		addSequential(commandOf(() ->
			Hardware.getInstance().getIntakes().getMouth().set(DoubleSolenoid.Value.kForward)));
		addSequential(new LiftPositionChange(Lift.Position.CARRY, 0));

		// TODO add a case for scaleSide 'E' and build in an empty gameMessage failure mode later
		if (getOurSwitchSide() == m_side.character) {
			addSequential(Navigate.to(0, 125, 0, 0.9, 6000));
			addSequential(Navigate.to(0, 0, 90 * m_side.multiplier, 0, 6000));
			addSequential(Navigate.to(0, 20, 0, 0.5,2000));
			addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.75);
//
//			addSequential(new Wait(1000));
//			addSequential(Navigate.to(0, 0, 90 * m_side.multiplier, 8, 3000));
//
//			addSequential(Navigate.to(0, 53, 0, 0.7, 3000));
//
//			addParallel(Navigate.to(0, 15, 0, 0.35, 2000));
//			addParallel(new RunIntakes(IntakeWheelSet.SpeedPreset.INTAKING, 2500));
//
//			addSequential(new LiftPositionChange(Lift.Position.CARRY, 1000));
//			addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.5);
		} else if (getOurScaleSide() == m_side.character) { // MAYBE do the scale
			addSequential(Navigate.to(0, 186, 0, 0.9, 10000));
			addSequential(Navigate.to(0, 64, 0, 0.8, 10000));
			addSequential(commandOf(() ->
				Hardware.getInstance().getDriveTrain().drive(0, 0)));
			addSequential(new LiftPositionChange(Lift.Position.TOP, 0));
			addSequential(new LiftPositionChange(Lift.Position.TOP, 2000));
			addSequential(Navigate.to(0, 0, 55 * m_side.multiplier, 0, 2000));
			addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.75);
			addSequential(new LiftPositionChange(Lift.Position.BOTTOM, 0));
			addSequential(new LiftPositionChange(Lift.Position.BOTTOM, 1000));
		} else {
			addSequential(Navigate.to(0, 170, 0, 0.8, 10000));
		}
	}
}
