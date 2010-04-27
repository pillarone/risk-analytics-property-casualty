package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.domain.pc.constants.Exposure;
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo (sku): add additional properties such as payment and earning pattern, inception and end date
// todo (meli): start- and end-time...
// todo (sku): extend by pml and sum insured
// todo (sku): discuss whether what properties might be different for gross and net
// todo (sku): discuss segmentation and naming
// todo (meli): how should the origin be set in the copy constructor?
public class ExposureInfo extends MultiValuePacket {

    public ExposureInfo originalExposureInfo;
    public double premiumWrittenAsIf;
    public double numberOfPolicies;
    public double sumInsured;
    public double maxSumInsured;
    public Exposure exposureDefinition;

    public ExposureInfo() {
        super();
    }
    // todo(meli): discuss with canoo whether the copy constructor needs to be defined explicitly in groovy or not.
    ExposureInfo(ExposureInfo expInfo) {
        super();
        premiumWrittenAsIf = expInfo.premiumWrittenAsIf;
        numberOfPolicies = expInfo.numberOfPolicies;
        exposureDefinition = expInfo.exposureDefinition;
        sumInsured = expInfo.sumInsured;
        maxSumInsured = expInfo.maxSumInsured;
        origin = expInfo.origin;
    }

    public double scaleValue(Exposure base) {
        switch (base) {
            case ABSOLUTE:
                return 1d;
            case NUMBER_OF_POLICIES:
                return getNumberOfPolicies();
            case PREMIUM_WRITTEN:
                return getPremiumWrittenAsIf();
        }
        return 1;
    }

    public double scaleValue(FrequencyBase base) {
        switch (base) {
            case ABSOLUTE:
                return 1d;
            case NUMBER_OF_POLICIES:
                return getNumberOfPolicies();
        }
        return 1;
    }

    public void scale(double factor) {
        maxSumInsured *= factor;
        premiumWrittenAsIf *= factor;
        sumInsured *= factor;
        if (factor == 0) {
            numberOfPolicies = 0;
        }
    }

    /**
     * Adds additive ExposureInfo fields (premiumWrittenAsIf, numberOfPolicies); averages sumInsured, weighted by the respective numberOfPolicies.
     * @param other
     * @return ExposureInfo packet with resulting fields
     */
    public ExposureInfo plus(ExposureInfo other) {
        if (other == null) return this;
        sumInsured = (numberOfPolicies * sumInsured + other.numberOfPolicies * other.sumInsured);
        premiumWrittenAsIf += other.premiumWrittenAsIf;
        numberOfPolicies += other.numberOfPolicies;
        if (numberOfPolicies > 0) {
            sumInsured = sumInsured / numberOfPolicies;
        }
        maxSumInsured = Math.max(maxSumInsured, other.maxSumInsured);
        if (exposureDefinition != other.exposureDefinition) {
            exposureDefinition = null;
        }
        return this;
    }


    /** caveat : for the minus method defined below we have to be careful with the number of policies here
    used for deriving the (averaged) sum insured from the total sum insured.
    For the correct approach follow the two outlined steps:
        step 1: Compute the difference of the total sums insured (analogously to premiumWrittenAsIf)
        step 2: To obtain the averaged sum insured use the number of policies of the minuend only.
    This is the natural procedure for insurance contracts where the minuend corresponds to the gross portfolio
    while the subtrahend is associated to the ceded portfolio. Hence for the number of policies we solely use the
    information from the gross portfolio.
    Sole exception (implemented in subclass): gross and ceded portfolio are equal, then numberOfPolicies =0.
    */

    ExposureInfo minus(ExposureInfo other) {
        if (other == null) return this;
        sumInsured = numberOfPolicies * sumInsured - other.numberOfPolicies * other.sumInsured;
        premiumWrittenAsIf -= other.premiumWrittenAsIf;
        if (numberOfPolicies > 0) {
            sumInsured = sumInsured / numberOfPolicies;
        }
        maxSumInsured = Math.max(maxSumInsured, other.maxSumInsured);
        if (exposureDefinition != null && exposureDefinition != other.exposureDefinition) {
            exposureDefinition = null;
        }
        return this;
    }

   /* // todo (sku): generalize to make sure it works even if a new property would be added to the class
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (this.getClass() != other.getClass()) return false;
        return premiumWrittenAsIf == ((ExposureInfo) other).premiumWrittenAsIf &&
            numberOfPolicies == ((ExposureInfo) other).numberOfPolicies &&
            exposureDefinition == ((ExposureInfo) other).exposureDefinition &&
            sumInsured == ((ExposureInfo) other).sumInsured &&
            maxSumInsured == ((ExposureInfo) other).maxSumInsured;
    }

    // todo (sku): generalize to make sure it works even if a new property would be added to the class
    public int hashCode() {
        return ((int) premiumWrittenAsIf +
                (int) numberOfPolicies +
                (exposureDefinition != null ? getExposureDefinition().hashCode() : 0) +
                (int) sumInsured +
                (int) maxSumInsured);
    }*/

    /* todo (sku): use this as idea for equals() and hasCode()
    static void allPropertiesCloned(Object clone, Object original) {
        clone.properties.each {property ->
            if (property.key != "class" && property.key != "metaClass" && property.value != null && !property.value.class.isEnum()) {
                Assert.assertNotSame "${property.key}", original.properties.get(property.key), property.value
            }
        }
    }
    */

    public double getPremiumWrittenAsIf() {
        return premiumWrittenAsIf;
    }

    public void setPremiumWrittenAsIf(double premiumWrittenAsIf) {
        this.premiumWrittenAsIf = premiumWrittenAsIf;
    }

    public double getNumberOfPolicies() {
        return numberOfPolicies;
    }

    public void setNumberOfPolicies(double numberOfPolicies) {
        this.numberOfPolicies = numberOfPolicies;
    }

    public double getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(double sumInsured) {
        this.sumInsured = sumInsured;
    }

    public double getMaxSumInsured() {
        return maxSumInsured;
    }

    public void setMaxSumInsured(double maxSumInsured) {
        this.maxSumInsured = maxSumInsured;
    }

    public Exposure getExposureDefinition() {
        return exposureDefinition;
    }

    public void setExposureDefinition(Exposure exposureDefinition) {
        this.exposureDefinition = exposureDefinition;
    }
}
