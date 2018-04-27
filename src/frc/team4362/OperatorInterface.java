package frc.team4362;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import frc.team4362.commands.MouthListener;
import frc.team4362.hardwares.Hardware;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.joy.Gembutton;
import frc.team4362.util.joy.Gemstick;

import java.util.Objects;

@SuppressWarnings("unused")
public class OperatorInterface {
	private static OperatorInterface INSTANCE = null;

	public static OperatorInterface getInstance() {
		if (Objects.isNull(INSTANCE)) {
			INSTANCE = new OperatorInterface();
		}

		return INSTANCE;
	}

	public final void configureControls(
			final Gemstick leftStick,
			final Gemstick rightStick,
			final XboxController controller,
			final MouthListener mouthListener
	) {
		final Gembutton shiftUpButton = new Gembutton(leftStick, 1),
				shiftDownButton = new Gembutton(rightStick, 1),

				intakeButton = new Gembutton(controller, 1),
				outtakeButton = new Gembutton(controller, 4),
//				openMouthButton = new Gembutton(controller, 9),

				climbExtendButton = new Gembutton(controller, 8),
				climbRetractButton = new Gembutton(controller, 7),

				mouthForcerButton = new Gembutton(controller, 10),
				lightButton = new Gembutton(controller,5),
				pinchButton = new Gembutton(controller, 6);

		shiftUpButton.whenPressed(() ->
		  	Hardware.getInstance().getShifter().set(DoubleSolenoid.Value.kForward));
		shiftDownButton.whenPressed(() ->
			Hardware.getInstance().getShifter().set(DoubleSolenoid.Value.kReverse));

		intakeButton.whenPressed(() ->
			Hardware.getInstance().getIntakes().set(IntakeWheelSet.SpeedPreset.INTAKING));
		intakeButton.whenReleased(() ->
			Hardware.getInstance().getIntakes().set(IntakeWheelSet.SpeedPreset.NEUTRAL));

		outtakeButton.whileHeldIfElse(
				() -> controller.getStickButton(GenericHID.Hand.kLeft),
				() -> Hardware.getInstance().getIntakes()
							  .set(IntakeWheelSet.SpeedPreset.OUTTAKING_BUT_FAST),
				() -> Hardware.getInstance().getIntakes()
							  .set(IntakeWheelSet.SpeedPreset.OUTTAKING));
		outtakeButton.whenReleased(
				() -> Hardware.getInstance().getIntakes()
							  .set(IntakeWheelSet.SpeedPreset.NEUTRAL));

//		openMouthButton.whenPressed(() ->
//			Hardware.getInstance().getIntakes().getMouth().set(DoubleSolenoid.Value.kForward));
//		openMouthButton.whenReleased(() ->
//			Hardware.getInstance().getIntakes().getMouth().set(DoubleSolenoid.Value.kReverse));

		mouthForcerButton.whenPressed(() -> {
			Hardware.getInstance().getIntakes().getMouth().set(DoubleSolenoid.Value.kForward);
			mouthListener.disable();
		});
		mouthForcerButton.whenReleased(mouthListener::enable);

		lightButton.whenPressed(Hardware.getInstance().getLEDs()::turnOn);
		lightButton.whenReleased(Hardware.getInstance().getLEDs()::turnOff);

		pinchButton.whenPressed(() ->
			Hardware.getInstance().getPincher().set(true));
		pinchButton.whenReleased(() ->
			Hardware.getInstance().getPincher().set(false));
	}
}
