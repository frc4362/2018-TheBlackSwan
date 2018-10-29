package frc.team4362.commands.auton;

public class OneCubeOrCrossAuton extends MegaAutonomous {
	public OneCubeOrCrossAuton(final Side s, final boolean switchPriority, final boolean doCross) {
		super(s, switchPriority, doCross);
	}

	@Override
	public void pickUpSecondCube() { }

	@Override
	public void putSecondCubeInScale() { }

	@Override
	public void putSecondCubeInSwitch() { }
}
