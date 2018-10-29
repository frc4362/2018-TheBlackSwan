package frc.team4362.commands.auton;

import frc.team4362.commands.LiftPositionChange;
import frc.team4362.commands.RunIntakes;
import frc.team4362.commands.any.Wait;
import frc.team4362.Hardware;
import frc.team4362.subsystems.Lift;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.IntakeWheelSet.SpeedPreset;
import frc.team4362.util.command.PowerUpCommandGroup;

import static frc.team4362.profiling.ProfileFollowerBuilder.loadProfile;
import static frc.team4362.util.command.Commands.autonOf;
import static frc.team4362.util.command.Commands.commandOf;

public class ProfiledRightTwoCube extends PowerUpCommandGroup {
	private final boolean m_force, m_doCross;

	public ProfiledRightTwoCube(final boolean force, final boolean doCross) {
		m_force = force;
		m_doCross = doCross;
	}

	@Override
	public void init() {
		if (getOurScaleSide() == 'R' || m_force) {
			addSequential(loadProfile("scale_r_1", false));
//			addParallel(loadProfile("scale_r_1", false));
			addParallel(commandOf(() ->
				Hardware.getInstance().getLift().setLiftPreset(Lift.Position.SCALE)));
			addSequential(new Wait(1000));   // yeet the cube out
			addSequential(new RunIntakes(SpeedPreset.OUTTAKING_BUT_FAST, 500));
			addSequential(commandOf(() ->
				Hardware.getInstance().getLift().setLiftPreset(Lift.Position.BOTTOM)));
			addSequential(new Wait(500));
			addSequential(new TurnDegrees(205, 3000));
			addParallel(autonOf(new Wait(750), new RunIntakes(SpeedPreset.INTAKING, 3000)));
			addSequential(loadProfile("scale_pick_up_cube", false));
			addSequential(loadProfile("scale_r_3", true));
			addSequential(new TurnDegrees(-180));

//			addSequential(ProfileFollowerBuilder.loadProfile("scale_r_2", true));
//			addParallel(new RunIntakes(IntakeWheelSet.SpeedPreset.INTAKING, 3500));
//			addSequential(ProfileFollowerBuilder.loadProfile("scale_r_grab_cube", false));
//			addSequential(ProfileFollowerBuilder.loadProfile("scale_r_grab_cube", true));

//			addParallel(new RunIntakes(SpeedPreset.INTAKING, 1000));
//			addSequential(loadProfile("scale_r_3", false));
//			addSequential(loadProfile("scale_r_4", true));
//			addParallel(loadProfile("scale_r_5", false));
//			addSequential(new Wait(200));
//			addParallel(commandOf(() ->
//				Hardware.getInstance().getLift().setLiftPreset(Lift.Position.TOP)));
//			addSequential(new Wait(700));
//			addSequential(new RunIntakes(SpeedPreset.OUTTAKING_BUT_FAST, 500));
		} else if (m_doCross) {
			addSequential(Navigate.to(0, 184, 0, 0.9, 6000));
			addSequential(Navigate.to(0, 0, -90, 9, 4000));
			addSequential(new DriveStraight(176, 0.9, 7000));
			addSequential(new LiftPositionChange(Lift.Position.TOP, 0));
			addSequential(Navigate.to(0, 0, 97, 3, 4000));
			addSequential(Navigate.to(0, 30, 0, 0.7, 3000));
			addSequential(new Wait(400));
			addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING_BUT_FAST, 750));
			addSequential(new LiftPositionChange(Lift.Position.BOTTOM, 1000));
			addSequential(Navigate.to(0, 0, 124, 0, 4000));
			addParallel(new RunIntakes(IntakeWheelSet.SpeedPreset.INTAKING, 1500));
			addSequential(Navigate.to(0, 53, 0, 0.5, 2000));
			addSequential(Navigate.to(0, -53, 0, -0.5, 2000));
		} else {
//			addSequential(loadProfile("half_cross", false));
			addSequential(Navigate.to(0, 188, 0, 0.9, 10000));
			addSequential(Navigate.to(0, 0, -90, 1, 4000));
			addSequential(Navigate.to(0, 50, 0, 0.7, 5000));
		}
	}
}
