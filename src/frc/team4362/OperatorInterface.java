package frc.team4362;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.XboxController;
import frc.team4362.commands.MouthListener;
import frc.team4362.subsystems.Intakes;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.joy.Gembutton;
import frc.team4362.util.joy.Gemstick;

import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class OperatorInterface {
	private static OperatorInterface INSTANCE = null;

	public static OperatorInterface getInstance() {
		if (Objects.isNull(INSTANCE)) {
			INSTANCE = new OperatorInterface();
		}

		return INSTANCE;
	}

	/**
	 * Binds controls to the controllers
	 * @param leftStick Left driver joystick
	 * @param rightStick Right driver joystick
	 * @param controller Operator controller
	 * @param mouthListener Mouth listener command
	 */
	public final void configureControls(
			final Gemstick leftStick,
			final Gemstick rightStick,
			final XboxController controller,
			final MouthListener mouthListener
	) {
		final Gembutton
			shiftUpButton      = new Gembutton(leftStick, 1),
			shiftDownButton    = new Gembutton(rightStick, 1),
			intakeButton       = new Gembutton(controller, 1),
			outtakeButton      = new Gembutton(controller, 4),
			climbExtendButton  = new Gembutton(controller, 8),
			climbRetractButton = new Gembutton(controller, 7),
			mouthForcerButton  = new Gembutton(controller, 6);

		// shift up and down
		shiftUpButton.whenPressed(() ->
		    Hardware.getInstance().getShifter().set(DoubleSolenoid.Value.kForward));
		shiftDownButton.whenPressed(() ->
			Hardware.getInstance().getShifter().set(DoubleSolenoid.Value.kReverse));

		final Intakes intakes = Hardware.getInstance().getIntakes();
		// run the intakes in
		intakeButton.whenPressed(() ->
			intakes.set(IntakeWheelSet.SpeedPreset.INTAKING));
		intakeButton.whenReleased(() ->
			intakes.set(IntakeWheelSet.SpeedPreset.NEUTRAL));

		// runs the intakes out, and provides a second speed
		outtakeButton.whileHeldIfElse(
			() -> controller.getRawButton(5),
			() -> intakes.set(IntakeWheelSet.SpeedPreset.OUTTAKING_BUT_FAST),
			() -> intakes.set(IntakeWheelSet.SpeedPreset.OUTTAKING));
		outtakeButton.whenReleased(
			() -> intakes.set(IntakeWheelSet.SpeedPreset.NEUTRAL));

		// force the intakes closed
		mouthForcerButton.whenPressed(() -> {
			intakes.getMouth().set(DoubleSolenoid.Value.kForward);
			mouthListener.disable();
		});
		mouthForcerButton.whenReleased(mouthListener::enable);
	}
}
