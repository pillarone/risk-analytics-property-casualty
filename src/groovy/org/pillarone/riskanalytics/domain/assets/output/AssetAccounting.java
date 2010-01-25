package org.pillarone.riskanalytics.domain.assets.output;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class AssetAccounting extends MultiValuePacket {

    private double bookValue;
    private double marketValue;
    private double unrealizedGains;
    private double cashIncome;
    private double realizedGains;
    private double valueAdjustments;
    private double expenses;
    private double bookResult;
    private double markedToMarketResult;
    private double cashFromSales;
    private double cashFromMaturities;
    private double cashInvested;
    private double markedToMarketReturn;
    private double bookReturn;

    public void addBookValue(double value){
        bookValue += value;
    }

    public void addMarketValue(double value){
        marketValue += value;
    }

    public void addUnrealizedGains(double value){
        unrealizedGains += value;
    }

    public void addCashIncome(double value){
        cashIncome += value;
    }

    public void addRealizedGains(double value){
        realizedGains += value;
    }

    public void addValueAdjustments(double value){
        valueAdjustments += value;
    }

    public void addExpenses(double value){
        expenses += value;
    }

    public void addBookResult(double value){
        bookResult += value;
    }

    public void addMarkedToMarketResult(double value){
        markedToMarketResult += value;
    }

    public void addCashFromSales(double value){
        cashFromSales += value;
    }

    public void addCashFromMaturities(double value){
        cashFromMaturities += value;
    }

    public void addCashInvested(double value){
        cashInvested += value;
    }

    public void addMarkedToMarketReturn(double value){
        markedToMarketReturn += value;
    }

    public void addBookReturn(double value){
        bookReturn += value;
    }

    public double getBookValue() {
        return bookValue;
    }

    public void setBookValue(double bookValue) {
        this.bookValue = bookValue;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    public double getUnrealizedGains() {
        return unrealizedGains;
    }

    public void setUnrealizedGains(double unrealizedGains) {
        this.unrealizedGains = unrealizedGains;
    }

    public double getCashIncome() {
        return cashIncome;
    }

    public void setCashIncome(double cashIncome) {
        this.cashIncome = cashIncome;
    }

    public double getRealizedGains() {
        return realizedGains;
    }

    public void setRealizedGains(double realizedGains) {
        this.realizedGains = realizedGains;
    }

    public double getValueAdjustments() {
        return valueAdjustments;
    }

    public void setValueAdjustments(double valueAdjustments) {
        this.valueAdjustments = valueAdjustments;
    }

    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public double getBookResult() {
        return bookResult;
    }

    public void setBookResult(double bookResult) {
        this.bookResult = bookResult;
    }

    public double getMarkedToMarketResult() {
        return markedToMarketResult;
    }

    public void setMarkedToMarketResult(double markedToMarketResult) {
        this.markedToMarketResult = markedToMarketResult;
    }

    public double getCashFromSales() {
        return cashFromSales;
    }

    public void setCashFromSales(double cashFromSales) {
        this.cashFromSales = cashFromSales;
    }

    public double getCashFromMaturities() {
        return cashFromMaturities;
    }

    public void setCashFromMaturities(double cashFromMaturities) {
        this.cashFromMaturities = cashFromMaturities;
    }

    public double getCashInvested() {
        return cashInvested;
    }

    public void setCashInvested(double cashInvested) {
        this.cashInvested = cashInvested;
    }

    public double getMarkedToMarketReturn() {
        return markedToMarketReturn;
    }

    public void setMarkedToMarketReturn(double markedToMarketReturn) {
        this.markedToMarketReturn = markedToMarketReturn;
    }

    public double getBookReturn() {
        return bookReturn;
    }

    public void setBookReturn(double bookReturn) {
        this.bookReturn = bookReturn;
    }
}
