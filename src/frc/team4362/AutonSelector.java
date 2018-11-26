package frc.team4362;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.team4362.commands.RunIntakes;
import frc.team4362.commands.auton.*;
import frc.team4362.profiling.ProfileFollowerBuilder;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.command.Commands;
import frc.team4362.util.command.PowerUpCommandGroup;

/**
 * Represents the auton selector on SmartDashboard
 */
@SuppressWarnings("WeakerAccess")
public class AutonSelector extends SendableChooser<Command> {
	{
		addDefault("Drive Straight",
				new DriveAcrossLineAuton());

		addObject("Turn",
				new ProfiledTurn(90));

		addObject("Best Smart Switch Auton",
				new CorrectSwitchSideAuton());

		addObject("Cross Auton Force",
				new RightCrossAuton(true));
		addObject("Full Right Side Auton",
				new ProfiledRightTwoCube(false, true));
		addObject("Right Side Motion Profiled One Cube Auton Forced",
				new ProfiledRightTwoCube(true, false));
		addObject("Right Side Motion Profiled One Cube Auton No Cross",
				new ProfiledRightTwoCube(false, false));

		addObject("None",
				Commands.nullCommand());

		addObject("Left Mega Auton (SCALE Priority)",
				new MegaAutonomous(MegaAutonomous.Side.LEFT, false, true));
		addObject("Right Mega Auton (SCALE Priority)",
				new MegaAutonomous(MegaAutonomous.Side.RIGHT, false, true));
//		addObject("Left Mega Auton (SWITCH Priority)",
//				new MegaAutonomous(MegaAutonomous.Side.LEFT, true, true));
//		addObject("Right Mega Auton (SWITCH Priority)",
//				new MegaAutonomous(MegaAutonomous.Side.RIGHT, true, true));
//
		addObject("Left Mega Auton (NO CROSS) (SCALE Priority)",
				new MegaAutonomous(MegaAutonomous.Side.LEFT, false, false));
		addObject("Right Mega Auton (NO CROSS) (SCALE Priority)",
				new MegaAutonomous(MegaAutonomous.Side.RIGHT, false, false));
//		addObject("Left Mega Auton (NO CROSS) (SWITCH Priority)",
//				new MegaAutonomous(MegaAutonomous.Side.LEFT, true, false));
//		addObject("Right Mega Auton (NO CROSS) (SWITCH Priority)",
//				new MegaAutonomous(MegaAutonomous.Side.RIGHT, true, false));
//
//		addObject("Left Side Switch Auton",
//				new SideSwitchAuton(SideSwitchAuton.Side.LEFT));
//		addObject("Right Side Switch Auton",
//				new SideSwitchAuton(SideSwitchAuton.Side.RIGHT));
//
		addObject("Left Close Scale or Cross Line Auton",
				new ScaleOrNothingAuton(MegaAutonomous.Side.LEFT));
		addObject("Right Close Scale or Cross Line Auton",
				new ScaleOrNothingAuton(MegaAutonomous.Side.RIGHT));
//
//		addObject("Force Left Two Cube",
//				new LiesMegaAuton(MegaAutonomous.Side.LEFT, false, false,
//						true, false));
//		addObject("Force Right Two Cube",
//				new LiesMegaAuton(MegaAutonomous.Side.RIGHT, false, false,
//						true, false));
//		addObject("Force Left Cross 1.5 Cube",
//				new LiesMegaAuton(MegaAutonomous.Side.LEFT, false, true,
//						false, false));
//		addObject("Force Right Cross 1.5 Cube",
//				new LiesMegaAuton(MegaAutonomous.Side.RIGHT, false, true,
//						false, false));
//		addObject("Force Left Side Scale/Switch",
//				new LiesMegaAuton(MegaAutonomous.Side.LEFT, true, false,
//						true, true));
//		addObject("Force Right Side Scale/Switch",
//			new LiesMegaAuton(MegaAutonomous.Side.LEFT, true, false,
//					true, true));
//
//		addObject("Field Check Auton",
//			new LiesMegaAuton(MegaAutonomous.Side.FIELD_CHECK, false, false,
//					true, false));
//
//		addObject("2-Cube Compatible Left (SCALE Priority)",
//				new CompatibleMegaAuton(MegaAutonomous.Side.LEFT, false));
//		addObject("2-Cube Compatible Right (SCALE Priority)",
//				new CompatibleMegaAuton(MegaAutonomous.Side.RIGHT, false));
//		addObject("2-Cube Compatible Left (SWITCH Priority)",
//				new CompatibleMegaAuton(MegaAutonomous.Side.LEFT, true));
//		addObject("2-Cube Compatible Right (SWITCH Priority)",
//				new CompatibleMegaAuton(MegaAutonomous.Side.RIGHT, true));
//
//		addObject("Left One Cube or Cross Auton",
//				new OneCubeOrCrossAuton(MegaAutonomous.Side.LEFT, false, true));
//		addObject("Right One Cube or Cross Auton",
//				new OneCubeOrCrossAuton(MegaAutonomous.Side.RIGHT, false, true));

		// we really don't need this, but I won't let this joke go away just yet
//		addObject("Mike Pence drive", new DriveStraight(-120, -0.5));
	}

	public class MyPowerUpCommandGroup extends PowerUpCommandGroup {
		public void init() {
			addSequential(ProfileFollowerBuilder.loadProfile("scale_r_2", true));
			addParallel(new RunIntakes(IntakeWheelSet.SpeedPreset.INTAKING, 3500));
			addSequential(ProfileFollowerBuilder.loadProfile("scale_r_grab_cube", false));
			addSequential(ProfileFollowerBuilder.loadProfile("scale_r_grab_cube", true));
		}
	}
}
