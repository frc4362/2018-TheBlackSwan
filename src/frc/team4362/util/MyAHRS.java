package frc.team4362.util;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;

public class MyAHRS extends AHRS {
	public MyAHRS(final SPI.Port id) {
		super(id);
	}

	@Override
	public double getAngle() {
		return super.getAngle();
	}
}
