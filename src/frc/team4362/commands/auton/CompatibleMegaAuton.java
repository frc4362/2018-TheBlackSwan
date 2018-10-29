package frc.team4362.commands.auton;

/**
 * Can be as much as
 */
public class CompatibleMegaAuton extends MegaAutonomous {
	public CompatibleMegaAuton(final Side s, final boolean switchPriority) {
		super(s, switchPriority, false);
	}

	@Override
	protected void pickUpSecondCube() { }

	@Override
	protected void putSecondCubeInSwitch() { }

	@Override
	protected void putSecondCubeInScale() { }
}
