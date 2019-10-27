package com.matteodri.services;

/**
 * Model for a battery used as household energy storage.
 *
 * @author Matteo Dri 25 Oct 2019
 */
public class Battery {

    // Defaults to a Tesla Powerwall 2
    private static final double DEFAULT_CAPACITY_WH = 13_500;
    private static final int DEFAULT_EFFICIENCY_PERC = 90;

    private double capacityWh;
    private double efficiency;
    private double currentlyStoredEnergyWh = 0;

    private Battery() {
    }

    public static class Builder {

        private double capacityWh = DEFAULT_CAPACITY_WH;
        private double efficiency = (double) DEFAULT_EFFICIENCY_PERC / 100;

        public Builder withCapacityWh(double capacityWh) {
            this.capacityWh = capacityWh;
            return this;
        }

        public Builder withEfficiencyPercentage(int efficiencyPercentage) {
            this.efficiency = (double) efficiencyPercentage / 100;
            return this;
        }

        public Battery build() {
            Battery battery = new Battery();
            battery.capacityWh = this.capacityWh;
            battery.efficiency = this.efficiency;
            return battery;
        }
    }

    /**
     * Add energy to the battery for future use.
     *
     * @param powerWh Power to be stored into the battery in Wh. Negative values will be treated as 0.
     * @return true if there is enough capacity to store the input energy, false if battery is full
     */
    public boolean storePower(double powerWh) {
        if (powerWh <= 0) {
            return true;
        }

        double powerMinusStorageLossWh = powerWh * efficiency;
        synchronized (this) {
            if (currentlyStoredEnergyWh + powerMinusStorageLossWh > capacityWh) {
                return false;
            }
            currentlyStoredEnergyWh = Math.min(currentlyStoredEnergyWh + powerMinusStorageLossWh, capacityWh);
        }
        return true;
    }

    /**
     * Get energy from the battery for immediate use. Negative values will be treated as 0.
     *
     * @param powerWh Power to be retrieved from the battery in Wh
     * @return true if energy required is retrieved, false if request can't be satisfied.
     */
    public boolean retrievePower(double powerWh) {
        if (powerWh <= 0) {
            return true;
        }

        synchronized (this) {
            if (currentlyStoredEnergyWh < powerWh) {
                return false;
            }
            currentlyStoredEnergyWh -= powerWh;
        }
        return true;
    }

    /**
     * Return amount of energy currently stored in the battery.
     * @return energy in Wh
     */
    public double getCurrentlyStoredEnergyWh() {
        synchronized (this) {
            return currentlyStoredEnergyWh;
        }
    }
}
