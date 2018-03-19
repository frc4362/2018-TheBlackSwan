package frc.team4362.util.camera;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;

public final class OwnedCamera {
	private static int newId;
	static {
		newId = 0;
	}

	private final UsbCamera m_camera;
	private final CvSink m_sink;

	public OwnedCamera(final UsbCamera camera) {
		m_camera = camera;
		m_sink = new CvSink("sink" + newId++);
		m_sink.setSource(m_camera);
		m_sink.setEnabled(true);
	}

	public UsbCamera getCamera() {
		return m_camera;
	}
}
