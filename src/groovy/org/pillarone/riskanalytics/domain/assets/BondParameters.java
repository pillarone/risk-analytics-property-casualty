package org.pillarone.riskanalytics.domain.assets;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.pillarone.riskanalytics.domain.assets.constants.BondType;
import org.pillarone.riskanalytics.domain.assets.constants.Rating;
import org.pillarone.riskanalytics.domain.assets.constants.Seniority;
import org.pillarone.riskanalytics.core.packets.Packet;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class BondParameters extends Packet {
    public BondType bondType;
    public double quantity;
    public double purchasePrice;
    public double bookValue;
    public double faceValue;
    public DateTime maturityDate;
    public double coupon;
    public Duration installmentPeriod;
    public Rating rating;
    public Seniority seniority;

    public BondType getBondType() {
        return bondType;
    }

    public void setBondType(BondType bondType) {
        this.bondType = bondType;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getBookValue() {
        return bookValue;
    }

    public void setBookValue(double bookValue) {
        this.bookValue = bookValue;
    }

    public double getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(double faceValue) {
        this.faceValue = faceValue;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(DateTime maturityDate) {
        this.maturityDate = maturityDate;
    }

    public double getCoupon() {
        return coupon;
    }

    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }

    public Duration getInstallmentPeriod() {
        return installmentPeriod;
    }

    public void setInstallmentPeriod(Duration installmentPeriod) {
        this.installmentPeriod = installmentPeriod;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Seniority getSeniority() {
        return seniority;
    }

    public void setSeniority(Seniority seniority) {
        this.seniority = seniority;
    }

}
