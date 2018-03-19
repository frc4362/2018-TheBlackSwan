package frc.team4362.util.joy;

import static frc.team4362.util.command.Commands.commandOf;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * it's times like these where I wish Java had a typedef
 * @author Ethan
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class Gembutton extends JoystickButton {
	public Gembutton(final GenericHID joystick, final int buttonNumber) {
		super(joystick, buttonNumber);
	}

	private static Supplier<Boolean> negated(final Supplier<Boolean> base) {
		return () -> !base.get();
	}

	public void whenPressedIfElse(
			final Supplier<Boolean> condition, 
			final Runnable action, 
			final Runnable otherwise
	) {
		if (condition.get()) {
			this.whenPressed(action);
		} else {
			this.whenPressed(otherwise);
		}
	}

	public void whenPressedIf(final Supplier<Boolean> condition, final Runnable action) {
		this.whenPressedIfElse(condition, action, () -> {});
	}
	
	public void whenPressedUnless(final Supplier<Boolean> condition, final Runnable action) {
		this.whenPressedIf(negated(condition), action);
	}

	public void whenPressed(final Runnable action) {
		super.whenPressed(commandOf(action));
	}

	public void whenReleasedIfElse(final Supplier<Boolean> condition, final Runnable action, final Runnable otherwise) {
		if (condition.get()) {
			this.whenReleased(action);
		} else {
			this.whenReleased(otherwise);
		}
	}

	public void whenReleasedIf(final Supplier<Boolean> condition, final Runnable action) {
		this.whenReleasedIfElse(condition, action, () -> {});
	}

	public void whenReleasedUnless(final Supplier<Boolean> condition, final Runnable action) {
		this.whenReleasedIf(negated(condition), action);
	}

	public void whenReleased(final Runnable action) {
		super.whenReleased(commandOf(action));
	}

	public void whileHeldIf(final Supplier<Boolean> condition, final Runnable action) {
		this.whileHeldIfElse(condition, action, () -> {});
	}

	public void whileHeldIfElse(final Supplier<Boolean> condition, final Runnable action, final Runnable otherwise) {
		this.whileHeld(() -> {
			if (condition.get()) {
				action.run();
			} else {
				otherwise.run();
			}
		});
	}

	public void whileHeld(final Runnable action) {
		super.whileHeld(commandOf(action));
	}
}
