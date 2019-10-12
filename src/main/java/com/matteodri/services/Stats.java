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
    private Integer peakConsumptionW;
    private LocalDateTime peakConsumptionTime;
    private Duration timeOverWarningThreshold;
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
        sb.append(", peakConsumptionW=").append(peakConsumptionW);
        sb.append(", peakConsumptionTime=").append(peakConsumptionTime);
        sb.append(", timeOverWarningThreshold=").append(timeOverWarningThreshold);
        sb.append(", processedLines=").append(processedLines);
        sb.append('}');
        return sb.toString();
    }
}
