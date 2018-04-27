package frc.team4362.commands.auton;

import frc.team4362.commands.LiftPositionChange;
import frc.team4362.commands.RunIntakes;
import frc.team4362.subsystems.Lift;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.command.PowerUpCommandGroup;

public class CompatibleDumbScale extends PowerUpCommandGroup {
	private final MegaAutonomous.Side m_side;

	public CompatibleDumbScale(final MegaAutonomous.Side s) {
		m_side = s;
	}

	@Override
	public void init() {
		if (getOurScaleSide() == m_side.character) {
			addSequential(Navigate.to(0, 280, 0, 0.85, 7000));
			addSequential(new LiftPositionChange(Lift.Position.TOP, 700));
			addSequential(Navigate.to(0, 0, 90 * m_side.multiplier, 1, 3000));
			addSequential(Navigate.to(0, 12, 0, 0.5, 1000));
			addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING_BUT_FAST, 750));
			addSequential(Navigate.to(0, -18, 0, 0.6,1000));
		}
	}
}
