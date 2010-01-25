package org.pillarone.riskanalytics.domain.assets.parameterization;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.pillarone.riskanalytics.domain.assets.BondParameters;
import org.pillarone.riskanalytics.domain.assets.constants.BondType;
import org.pillarone.riskanalytics.domain.assets.constants.Rating;
import org.pillarone.riskanalytics.domain.assets.constants.Seniority;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class Bond extends Component {

    private BondType parmBondType = BondType.CORPORATE_BOND;
    private double parmQuantity;
    private double parmPurchasePrice;
    private double parmBookValue;
    private double parmFaceValue;
    private String parmMaturityDate = "2011-12-31";
    private double parmCoupon;
    private String parmInstallmentPeriod = "YEARLY"; //todo (cne) this has to be converted as well... for the moment, only MONTHLY, YEARLY
    private Rating parmRating = Rating.AAA;
    private Seniority parmSeniority = Seniority.SENIOR_SECURED;


    private PacketList<BondParameters> outBondParameters = new PacketList<BondParameters>(BondParameters.class);

    protected void doCalculation() {
        BondParameters packet = new BondParameters();
        packet.setBondType(getParmBondType());
        packet.setQuantity(getParmQuantity());
        packet.setPurchasePrice(getParmPurchasePrice());
        packet.setBookValue(getParmBookValue());
        packet.setFaceValue(getParmFaceValue());
        packet.setMaturityDate(convertDate(getParmMaturityDate()));
        packet.setCoupon(getParmCoupon());
        packet.setInstallmentPeriod(convertDuration(getParmInstallmentPeriod()));
        packet.setRating(getParmRating());
        packet.setSeniority(getParmSeniority());
        outBondParameters.add(packet);
    }

    // todo(sku): the following converter functions are temporary added till the table models allow more complex data type.
    private static DateTime convertDate(String date) {
        String[] splittedDate = date.split("-");
        return new DateTime(Integer.valueOf(splittedDate[0]), Integer.valueOf(splittedDate[1]), Integer.valueOf(splittedDate[2]), 0, 0, 0, 0);
    }

    private static Duration convertDuration(String date) {
        if (date.contentEquals("YEARLY")) {
            return Duration.standardDays(365);
        } else if (date.contentEquals("MONTHLY")) {
            return Duration.standardDays(31);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public BondType getParmBondType() {
        return parmBondType;
    }

    public void setParmBondType(BondType parmBondType) {
        this.parmBondType = parmBondType;
    }

    public double getParmQuantity() {
        return parmQuantity;
    }

    public void setParmQuantity(double parmQuantity) {
        this.parmQuantity = parmQuantity;
    }

    public double getParmPurchasePrice() {
        return parmPurchasePrice;
    }

    public void setParmPurchasePrice(double parmPurchasePrice) {
        this.parmPurchasePrice = parmPurchasePrice;
    }

    public double getParmBookValue() {
        return parmBookValue;
    }

    public void setParmBookValue(double parmBookValue) {
        this.parmBookValue = parmBookValue;
    }

    public double getParmFaceValue() {
        return parmFaceValue;
    }

    public void setParmFaceValue(double parmFaceValue) {
        this.parmFaceValue = parmFaceValue;
    }

    public String getParmMaturityDate() {
        return parmMaturityDate;
    }

    public void setParmMaturityDate(String parmMaturityDate) {
        this.parmMaturityDate = parmMaturityDate;
    }

    public double getParmCoupon() {
        return parmCoupon;
    }

    public void setParmCoupon(double parmCoupon) {
        this.parmCoupon = parmCoupon;
    }

    public String getParmInstallmentPeriod() {
        return parmInstallmentPeriod;
    }

    public void setParmInstallmentPeriod(String parmInstallmentPeriod) {
        this.parmInstallmentPeriod = parmInstallmentPeriod;
    }

    public Rating getParmRating() {
        return parmRating;
    }

    public void setParmRating(Rating parmRating) {
        this.parmRating = parmRating;
    }

    public Seniority getParmSeniority() {
        return parmSeniority;
    }

    public void setParmSeniority(Seniority parmSeniority) {
        this.parmSeniority = parmSeniority;
    }

    public PacketList<BondParameters> getOutBondParameters() {
        return outBondParameters;
    }

    public void setOutBondParameters(PacketList<BondParameters> outBondParameters) {
        this.outBondParameters = outBondParameters;
    }

}
