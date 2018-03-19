package frc.team4362.util.joy;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.team4362.util.command.Commands;

@SuppressWarnings("serial")
public class DPadListener extends Command {
	private final XboxController m_controller;
	private final Map<Integer, Command> m_bindings;
	private int m_lastPressed;

	private static int angleToID(final int input) {
		return input == -1 ? input : input / 45;
	}

	public enum Direction {
		NORTH(0),
		NORTH_EAST(1),
		EAST(2),
		SOUTH_EAST(3),
		SOUTH(4),
		SOUTH_WEST(5),
		WEST(6),
		NORTH_WEST(7);
		
		private final int m_value;

		Direction(final int value) {
			m_value = value;
		}
		
		public int getValue() {
			return m_value;
		}
	}

	private DPadListener(
			final XboxController controller,
			Map<Integer, Command> bindings
	) {
		m_controller = controller;
		m_bindings = new HashMap<>();
		IntStream.range(-1, 8).forEach(id ->
			m_bindings.put(id, bindings.getOrDefault(id, Commands.nullCommand())));
	}

	public static DPadListener of(
			final XboxController controller,
			Map<Direction, Command> bindings
	) {
		return new DPadListener(
			controller,
			new HashMap<Integer, Command>() {{
				bindings.forEach((k, v) ->
					put(k.getValue(), v));
			}}
		);
	}

    protected void execute() {
    	final int pressed = m_controller.getPOV();

    	if (pressed != m_lastPressed) {
    		System.out.println("Recognized POV state change to POV(" + pressed + ")");
    		Scheduler.getInstance().add(m_bindings.get(angleToID(pressed)));
    	}
    	
    	m_lastPressed = pressed;
    }

	public static boolean isPressed(final XboxController controller, final Direction button) {
		return angleToID(controller.getPOV()) == button.getValue();
	}

    protected boolean isFinished() {
        return false;
    }
}
