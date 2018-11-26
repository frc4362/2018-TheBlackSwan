package frc.team4362.commands.auton;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.command.Command;
import frc.team4362.Hardware;
import frc.team4362.kinematics.Kinematics;
import frc.team4362.kinematics.motion.Rotation;
import frc.team4362.kinematics.motion.Twist;

public class ProfiledTurn extends Command {
	private final Kinematics.Velocity m_movement;
	private final WPI_TalonSRX m_talonLeft, m_talonRight;
	private int m_runs;

	public ProfiledTurn(final double degrees) {
		final double rot = Rotation.fromDegrees(degrees).getRadians();
		m_movement = Kinematics.inverseKinematics(new Twist(0, 0, rot));

		m_talonLeft = Hardware.getInstance().getDriveTrain().getTalons().get(0);
		m_talonRight = Hardware.getInstance().getDriveTrain().getTalons().get(2);
	}

	@Override
	public void initialize() {
		m_runs = 0;
		Hardware.getInstance().getDriveTrain()
				.driveInches(m_movement.left, m_movement.right);
	}

	@Override
	public void execute() {
		m_runs++;
	}

	@Override
	public boolean isFinished() {
		return m_runs > 2
			   && Math.abs(m_talonLeft.getClosedLoopError(0)) < 500
			   && Math.abs(m_talonRight.getClosedLoopError(0)) < 500;
	}
}
