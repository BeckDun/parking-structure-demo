package psms;

/**
 * Sensor Interface (SAD Section 3)
 * Abstraction layer between physical input devices and the system.
 * Normalizes hardware signals into standardized events and routes them
 * to the System Controller.
 *
 * Methods: normalizeSignal(RawSignal), routeToController(Event)
 */
public interface SensorInterface {
    String normalizeSignal();
    void routeToController();
}
