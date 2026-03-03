package psms.drivers;

import psms.SensorInterface;

/**
 * Sensor Driver (SAD Section 2.3)
 * Low-level communication between physical sensing hardware and the Sensor Interface.
 * Applies signal conditioning and noise filtering, converts raw electrical signals
 * into processed digital data, then forwards to the Sensor Interface.
 *
 * Talks only to: Sensor Interface
 */
public class SensorDriver {
    private final SensorInterface sensorInterface;

    public SensorDriver(SensorInterface sensorInterface) {
        this.sensorInterface = sensorInterface;
    }

    /**
     * Reads raw state from a physical sensor device, applies signal conditioning,
     * converts to processed digital data, and forwards to the Sensor Interface
     * for normalization and routing to the System Controller.
     *
     * @param deviceType identifier for the sensor hardware (e.g. "ENTRY_GATE")
     * @param sensorState the raw boolean state read from the device
     * @return result from the System Controller's event processing
     */
    public boolean processSensorReading(String deviceType, boolean sensorState) {
        String rawSignal = deviceType + ":" + (sensorState ? "ACTIVE" : "INACTIVE");
        String event = sensorInterface.normalizeSignal(rawSignal);
        return sensorInterface.routeToController(event);
    }
}
