package gg.mineral.api.tick

interface TickLoop {
    interface RollingAverage {
        /**
         * Gets the average of the rolling average.
         *
         * @return The average of the rolling average.
         */
        val average: Double
    }

    /**
     * Gets the 1 minute TPS.
     *
     * @return The 1 minute TPS.
     */
    val tps1: RollingAverage?

    /**
     * Gets the 5 minute TPS.
     *
     * @return The 5 minute TPS.
     */
    val tps5: RollingAverage?

    /**
     * Gets the 15 minute TPS.
     *
     * @return The 15 minute TPS.
     */
    val tps15: RollingAverage?
}
