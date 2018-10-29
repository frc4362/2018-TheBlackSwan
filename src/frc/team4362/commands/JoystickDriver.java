package frc.team4362.commands;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.team4362.subsystems.DifferentialDrive;
import frc.team4362.util.command.ToggleableCommand;
import frc.team4362.util.func.DriveConsumer;
import frc.team4362.util.joy.DriveItems;
import frc.team4362.util.joy.Gemstick;

import java.util.Arrays;
import java.util.List;

public final class JoystickDriver extends ToggleableCommand {
	private static final SendableChooser<DriveMode> modeChooser;
	static {
		modeChooser = new SendableChooser<>();
		modeChooser.addDefault(DriveMode.CURVATURE.toString(), DriveMode.CURVATURE);
		DriveMode.getModes().forEach(mode ->
			modeChooser.addObject(mode.name(), mode));
	}

	/**
	 * @return The SmartDashboard {@link DriveMode} selector
	 */
	public static SendableChooser<DriveMode> getDriveModeSelector() {
		return modeChooser;
	}

	private final DriveItems m_driveItems;

	/**
	 * Defines and implements several different drive modes
	 */
	public enum DriveMode {
		/**
		 * Two-joystick drive, raw control from left -> left and right -> right
		 */
		TANK(items -> {
			items.driveBase.drive(
					items.stickLeft.get(Gemstick.StickLens.Y),
					items.stickRight.get(Gemstick.StickLens.Y)
			);
		}),
		/**
		 * Maintains a consistent turning radius no matter the speed travelling,
		 * by directing the angular power and velocity on separate sticks.
		 * also provides a quickturn button which allows one to stop their momentum
		 * and quickly turn.
		 */
		CURVATURE(items -> {
			final double
					throttle = items.stickLeft.get(Gemstick.StickLens.Y),
					wheel = items.stickRight.get(Gemstick.StickLens.X);

			final boolean isQuickTurn = items.stickRight.getRawButton(2);
			final boolean isSlowTurn = items.stickLeft.getRawButton(2);

			DifferentialDrive.QuickTurnKind kind;

			if (isQuickTurn) {
				kind = DifferentialDrive.QuickTurnKind.FAST;
			} else if (isSlowTurn) {
				kind = DifferentialDrive.QuickTurnKind.SLOW;
			} else {
				kind = DifferentialDrive.QuickTurnKind.OFF;
			}

			items.driveBase.driveCurvature(throttle, wheel, kind);
		}),
		/**
		 * One-stick expressive drive based on X and Y axis
		 */
		ARCADE(items -> {
			final double
					speed = items.stickRight.get(Gemstick.StickLens.Y),
					rotation = items.stickRight.get(Gemstick.StickLens.X);

			items.driveBase.driveArcade(speed, rotation, true);
		}),
		/**
		 * Drive mode which exists purely to charge air.
		 */
		AIRING(items -> {});

		private final DriveConsumer m_action;

		DriveMode(final DriveConsumer action) {
			m_action = action;
		}

		public static List<DriveMode> getModes() {
			return Arrays.asList(CURVATURE, ARCADE, TANK, AIRING);
		}

		public DriveConsumer getAction() {
			return m_action;
		}
	}

	public JoystickDriver(
			final DifferentialDrive driveBase,
			final Gemstick leftStick,
			final Gemstick rightStick
	) {
		super(StartMode.ENABLED);
		m_driveItems = new DriveItems(
				driveBase,
				leftStick,
				rightStick
		);
	}

	public void whenEnabled() {
		modeChooser.getSelected()
				.getAction()
				.accept(m_driveItems);
	}

	protected boolean isFinished() {
		return false;
	}
}
