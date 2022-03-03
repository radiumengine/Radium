package Radium;

/**
 * Stores time settings and variables
 */
public class Time {

    private static float timeStarted = System.nanoTime();

    public static float time;

    /**
     * The time between frames:
     * E.G. 1/60 = 0.016
     */
    public static float deltaTime;

    protected Time() {}

    /**
     * Returns the time since the editor has started playing in seconds
     */
    public static float GetTime() {
        time = (float)((System.nanoTime() - timeStarted) * 1E-9);
        return time;
    }

}
