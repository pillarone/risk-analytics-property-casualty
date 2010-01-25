package org.pillarone.riskanalytics.domain.assets.output;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class BondAccounting extends AssetAccounting {


    private double duration;
    private double marketValueOfShortTermBonds;
    private double marketValueOfMediumTermBonds;
    private double marketValueOfLongTermBonds;


    public void addDuration(double value){
        duration += value;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getMarketValueOfShortTermBonds() {
        return marketValueOfShortTermBonds;
    }

    public void setMarketValueOfShortTermBonds(double marketValueOfShortTermBonds) {
        this.marketValueOfShortTermBonds = marketValueOfShortTermBonds;
    }

    public double getMarketValueOfMediumTermBonds() {
        return marketValueOfMediumTermBonds;
    }

    public void setMarketValueOfMediumTermBonds(double marketValueOfMediumTermBonds) {
        this.marketValueOfMediumTermBonds = marketValueOfMediumTermBonds;
    }

    public double getMarketValueOfLongTermBonds() {
        return marketValueOfLongTermBonds;
    }

    public void setMarketValueOfLongTermBonds(double marketValueOfLongTermBonds) {
        this.marketValueOfLongTermBonds = marketValueOfLongTermBonds;
    }
}