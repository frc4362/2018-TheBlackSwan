package frc.team4362.commands.any;

import edu.wpi.first.wpilibj.command.Command;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Wait extends Command {
	private final long m_length;
	private long m_endTime;

    public Wait(final long length) {
    	m_length = length;
    }

    protected void initialize() {
    	m_endTime = System.currentTimeMillis() + m_length;
    }

    protected boolean isFinished() {
        return m_endTime < System.currentTimeMillis();
    }
}
