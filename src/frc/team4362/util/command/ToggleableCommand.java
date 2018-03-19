package frc.team4362.util.command;

import edu.wpi.first.wpilibj.command.Command;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ToggleableCommand extends Command {
	protected enum StartMode {
		ENABLED(true), DISABLED(false), DEFAULT(true);
		
		private boolean m_mode;
		
		StartMode(final boolean b) {
			m_mode = b;
		}
		
		public boolean toBoolean() {
			return m_mode;
		}
	}
	
	private boolean m_enabled;

	protected ToggleableCommand(final StartMode startEnabled) {
		m_enabled = startEnabled.toBoolean();
	}

	protected ToggleableCommand() {
		this(StartMode.ENABLED);
	}

	public final void setEnabled(final boolean enabled) {
		m_enabled = enabled;
	}

	public final void enable() {
		setEnabled(true);
	}

	public final void disable() {
		setEnabled(false);
	}

	public final boolean isEnabled() {
		return m_enabled;
	}
	
	public final boolean isDisabled() {
		return !isEnabled();
	}

	protected abstract void whenEnabled();
	
	@SuppressWarnings("EmptyMethod")
	protected void whenDisabled() {	}
	
	@Override
	protected final void execute() {
		if (isEnabled()) {
			whenEnabled();
		} else {
			whenDisabled();
		}
	}
}
