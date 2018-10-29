package frc.team4362.commands.auton;

import frc.team4362.commands.LiftPositionChange;
import frc.team4362.commands.RunIntakes;
import frc.team4362.subsystems.Lift;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.command.PowerUpCommandGroup;

import static frc.team4362.profiling.ProfileFollowerBuilder.loadProfile;

public class RightCrossAuton extends PowerUpCommandGroup {
	private final boolean m_force;

	public RightCrossAuton(final boolean force) {
		m_force = force;
	}

	@Override
	public void init() {
		if (getOurScaleSide() == 'L' || m_force) {
			addSequential(loadProfile("scale_r_cross_1", false));
			addSequential(loadProfile("scale_r_cross_2", false));
			addSequential(new LiftPositionChange(Lift.Position.SCALE, 1000));
			addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING_BUT_FAST, 1000));
		} else {
			addSequential(new DriveAcrossLineAuton());
		}
	}
}
