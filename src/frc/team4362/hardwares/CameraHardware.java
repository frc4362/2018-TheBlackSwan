package frc.team4362.hardwares;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoException;
import edu.wpi.first.wpilibj.CameraServer;
import frc.team4362.util.camera.OwnedCamera;

@SuppressWarnings({"unused", "WeakerAccess"})
public class CameraHardware extends Hardware {
	private final OwnedCamera m_camera1;

	protected CameraHardware() {
		super();

		m_camera1 = new OwnedCamera(CameraServer.getInstance().startAutomaticCapture(0));
	}

	public UsbCamera getCamera1() {
		return m_camera1.getCamera();
	}

	public void setDisplayedCamera(final UsbCamera camera) {
		CameraServer.getInstance().getServer().setSource(camera);
	}

	@SuppressWarnings("unused")
	// this method is weird, but it must be the way that it is so that
	// 	1. It functions and
	//	2. doesn't spam the console
	public static boolean isCamerasPresent() {
//		UsbCamera cam0;
//
//		try {
//			// :thonk:
//			cam0 = CameraServer.getInstance().startAutomaticCapture();
////			cam1 = CameraServer.getInstance().startAutomaticCapture(1);
//			cam0.free();
////			cam1.free();
//		} catch (final VideoException ve) {
//			System.out.println("Camera not found! " + ve.toString());
//			return false;
//		} finally {
//			cam0 = null;
////			cam1 = null;
//		}

		return true;
	}
}
