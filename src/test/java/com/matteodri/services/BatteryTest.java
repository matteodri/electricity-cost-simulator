package com.matteodri.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Battery}.
 */
class BatteryTest {

    private Battery target;

    @Test
    @DisplayName("Test adding energy and checking current value with 100% efficiency")
    public void getCurrentlyStoredEnergyWh() {
        target = new Battery.Builder().withCapacityWh(10_000).withEfficiencyPercentage(100).build();
        target.storePower(5000);

        double energy = target.getCurrentlyStoredEnergyWh();
        assertEquals(5000, energy);
    }

    @Test
    @DisplayName("Test storage with 100% efficiency")
    public void storePowerFullEfficiency() {
        target = new Battery.Builder().withCapacityWh(10_000).withEfficiencyPercentage(100).build();
        target.storePower(5000);
        target.storePower(1000);

        double energy = target.getCurrentlyStoredEnergyWh();
        assertEquals(6000, energy);
    }

    @Test
    @DisplayName("Test storage and retrieval with 100% efficiency")
    public void retrievePowerFullEfficiency() {
        target = new Battery.Builder().withCapacityWh(10_000).withEfficiencyPercentage(100).build();
        target.storePower(5000);
        boolean retrieved = target.retrievePower(1000);

        double energy = target.getCurrentlyStoredEnergyWh();
        assertEquals(4000, energy);
        assertTrue(retrieved);
    }

    @Test
    @DisplayName("Test storage with 50% efficiency")
    public void storePowerLowEfficiency() {
        target = new Battery.Builder().withCapacityWh(20_000).withEfficiencyPercentage(50).build();
        target.storePower(10_000);
        target.storePower(2000);
        target.retrievePower(2000);

        double energy = target.getCurrentlyStoredEnergyWh();
        assertEquals(4000, energy);
    }

    @Test
    @DisplayName("Test retrieval of energy from empty battery")
    public void retrieveEnergyFromEmptyBattery() {
        target = new Battery.Builder().withCapacityWh(20_000).withEfficiencyPercentage(100).build();
        target.storePower(10_000);
        boolean retrieveOk = target.retrievePower(10_000);
        boolean retrieveNotOk = target.retrievePower(2000);

        double energy = target.getCurrentlyStoredEnergyWh();
        assertEquals(0, energy);
        assertTrue(retrieveOk);
        assertFalse(retrieveNotOk);
    }
}