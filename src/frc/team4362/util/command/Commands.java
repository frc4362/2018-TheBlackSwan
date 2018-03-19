package frc.team4362.util.command;


import java.util.Arrays;
import java.util.List;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.InstantCommand;

@SuppressWarnings("unused")
public final class Commands {
	private Commands() {}

	public static InstantCommand commandOf(final Runnable action) {
		return new InstantCommand() {
			// FredBoat is with u
			protected void initialize() {
				action.run();
			}
		};
	}

	public static CommandGroup autonOf(final Command... actions) {
		return new CommandGroup() {
			{
				Arrays.asList(actions).forEach(this::addSequential);
			}
		};
	}

	public static CommandGroup autonOf(final List<Command> actions) {
		return autonOf(actions.toArray(new Command[0]));
	}

	public static Command nullCommand() {
		return commandOf(() -> {});
	}
}
