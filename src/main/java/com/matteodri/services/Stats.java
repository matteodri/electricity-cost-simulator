package com.matteodri.services;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Energy consumption statistics.
 *
 * @author Matteo Dri 12 Oct 2019
 */
public class Stats {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double overallCost;
    private Double f1Cost;
    private Double f2Cost;
    private Double f3Cost;
    private Double f1CostIfHadBattery;
    private Double f2CostIfHadBattery;
    private Double f3CostIfHadBattery;
    private Integer peakConsumptionW;
    private LocalDateTime peakConsumptionTime;
    private Duration timeOverWarningThreshold;
    private long daysWithConsumptionGreaterThanSolarProduction;
    private long daysProcessed;
    private long processedLines;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getOverallCost() {
        return overallCost;
    }

    public void setOverallCost(Double overallCost) {
        this.overallCost = overallCost;
    }

    public Double getF1Cost() {
        return f1Cost;
    }

    public void setF1Cost(Double f1Cost) {
        this.f1Cost = f1Cost;
    }

    public Double getF2Cost() {
        return f2Cost;
    }

    public void setF2Cost(Double f2Cost) {
        this.f2Cost = f2Cost;
    }

    public Double getF3Cost() {
        return f3Cost;
    }

    public void setF3Cost(Double f3Cost) {
        this.f3Cost = f3Cost;
    }

    public Double getF1CostIfHadBattery() {
        return f1CostIfHadBattery;
    }

    public void setF1CostIfHadBattery(Double f1CostIfHadBattery) {
        this.f1CostIfHadBattery = f1CostIfHadBattery;
    }

    public Double getF2CostIfHadBattery() {
        return f2CostIfHadBattery;
    }

    public void setF2CostIfHadBattery(Double f2CostIfHadBattery) {
        this.f2CostIfHadBattery = f2CostIfHadBattery;
    }

    public Double getF3CostIfHadBattery() {
        return f3CostIfHadBattery;
    }

    public void setF3CostIfHadBattery(Double f3CostIfHadBattery) {
        this.f3CostIfHadBattery = f3CostIfHadBattery;
    }

    public Integer getPeakConsumptionW() {
        return peakConsumptionW;
    }

    public void setPeakConsumptionW(Integer peakConsumptionW) {
        this.peakConsumptionW = peakConsumptionW;
    }

    public LocalDateTime getPeakConsumptionTime() {
        return peakConsumptionTime;
    }

    public void setPeakConsumptionTime(LocalDateTime peakConsumptionTime) {
        this.peakConsumptionTime = peakConsumptionTime;
    }

    public Duration getTimeOverWarningThreshold() {
        return timeOverWarningThreshold;
    }

    public void setTimeOverWarningThreshold(Duration timeOverWarningThreshold) {
        this.timeOverWarningThreshold = timeOverWarningThreshold;
    }

    public long getDaysWithConsumptionGreaterThanSolarProduction() {
        return daysWithConsumptionGreaterThanSolarProduction;
    }

    public void setDaysWithConsumptionGreaterThanSolarProduction(long daysWithConsumptionGreaterThanSolarProduction) {
        this.daysWithConsumptionGreaterThanSolarProduction = daysWithConsumptionGreaterThanSolarProduction;
    }
    public long getDaysProcessed() {
        return daysProcessed;
    }

    public void setDaysProcessed(long daysProcessed) {
        this.daysProcessed = daysProcessed;
    }

    public long getProcessedLines() {
        return processedLines;
    }

    public void setProcessedLines(long processedLines) {
        this.processedLines = processedLines;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Stats{");
        sb.append("startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", overallCost=").append(overallCost);
        sb.append(", f1Cost=").append(f1Cost);
        sb.append(", f2Cost=").append(f2Cost);
        sb.append(", f3Cost=").append(f3Cost);
        sb.append(", f1CostIfHadBattery=").append(f1CostIfHadBattery);
        sb.append(", f2CostIfHadBattery=").append(f2CostIfHadBattery);
        sb.append(", f3CostIfHadBattery=").append(f3CostIfHadBattery);
        sb.append(", peakConsumptionW=").append(peakConsumptionW);
        sb.append(", peakConsumptionTime=").append(peakConsumptionTime);
        sb.append(", timeOverWarningThreshold=").append(timeOverWarningThreshold);
        sb.append(", daysWithConsumptionGreaterThanSolarProduction=").append(daysWithConsumptionGreaterThanSolarProduction);
        sb.append(", daysProcessed=").append(daysProcessed);
        sb.append(", processedLines=").append(processedLines);
        sb.append('}');
        return sb.toString();
    }

}
