package frc.team4362.commands.auton;

public class ScaleOrNothingAuton extends MegaAutonomous {
	public ScaleOrNothingAuton(final Side side) {
		super(side, false, false);
	}

	@Override
	public void closeSwitchOnly() {
		addSequential(Navigate.to(0, 130, 0, 0.9, 5000));
	}
}
