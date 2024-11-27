package gg.mineral.api.tick;

public interface TickLoop {
    public static interface RollingAverage {
        /**
         * Gets the average of the rolling average.
         * 
         * @return The average of the rolling average.
         */
        double getAverage();
    }

    /**
     * Gets the 1 minute TPS.
     * 
     * @return The 1 minute TPS.
     */
    RollingAverage getTps1();

    /**
     * Gets the 5 minute TPS.
     * 
     * @return The 5 minute TPS.
     */
    RollingAverage getTps5();

    /**
     * Gets the 15 minute TPS.
     * 
     * @return The 15 minute TPS.
     */
    RollingAverage getTps15();
}
