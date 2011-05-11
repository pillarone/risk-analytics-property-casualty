package org.pillarone.riskanalytics.domain.pc.generators.claims;

import org.apache.commons.lang.ArrayUtils;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimPacketFactory;
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType;
import org.pillarone.riskanalytics.domain.pc.constants.Exposure;
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency;
import org.pillarone.riskanalytics.domain.pc.generators.severities.Severity;
import org.pillarone.riskanalytics.domain.utils.*;

import java.util.HashMap;

/**
 * The attritional claims generator sends one claim object to components
 * attached to the <tt>outClaims</tt>.<br/>
 * Claims are generated according to the parameterization of <tt>parmGenerator</tt>.
 * The claim size is generated in two different ways:
 * <ul><li>directly using the member variable <code>generator</code></li>
 * <li>wiring inProbability allows to correlate claim sizes generated in different
 * AttritionalClaimsGenerators. If this is the case the inverse function is used
 * to lookup the claim size</li>
 * </ul>
 * Among the possible distributions are normal, lognormal, pareto, uniform,
 * constant, poisson and negative binomial. In order to get a complete list, have
 * a look at org.pillarone.riskanalytics.util.DistributionType.<br/>
 * <b>Usage</b>: In order to use it in a model it has to be part of the startComponents
 * or of a compound component.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): profile the runtime difference between using of the generator and the inverse function
// todo(sku): apply a design pattern to clearly separate the two different use cases (generator vs probabiliy)
@ComponentCategory(categories = {"CLAIM","GENERATOR","ATTRITIONAL"})
public class AttritionalClaimsGenerator extends ClaimsGenerator {

    private IRandomNumberGenerator generator;

    /**
     * Input channel for claim severity to be generated
     */
    private PacketList<Severity> inProbability = new PacketList<Severity>(Severity.class);

    private PacketList<Frequency> inMultiplier = new PacketList<Frequency>(Frequency.class);

    private RandomDistribution parmDistribution = DistributionType.getStrategy(ClaimSizeDistributionType.CONSTANT,
        ArrayUtils.toMap(new Object[][]{{"constant", 0d}}));
    private DistributionModified parmModification = DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap());

    public void validateWiring() {
        if (!maxOneSenderWired(getInUnderwritingInfo())) {
            throw new IllegalStateException("AttritionalClaimsGenerator.invalidWiring");
        }
        super.validateWiring();
    }

    public void validateParameterization() {
        if (parmDistribution == null) {
            throw new IllegalStateException("AttritionalClaimsGenerator.missingDistribution");
        }
        if (!getParmBase().equals(Exposure.ABSOLUTE) && !isReceiverWired(getInUnderwritingInfo())) {
            throw new IllegalStateException("AttritionalClaimsGenerator.invalidExposureBase");
        }
        super.validateParameterization();
    }

    public void doCalculation() {
        if (!this.isReceiverWired(inProbability)) {
            generator = getCachedGenerator(parmDistribution, parmModification);
        }
        double scalingFactor = getScalingFactor();

        if (inMultiplier.size() > 1) {
            throw new IllegalStateException("AttritionalClaimsGenerator.invalidNoOfMultiplierPackets");
        }
        if (inMultiplier.size() == 1) {
            scalingFactor *= inMultiplier.get(0).value;
        }
        double claimSize = 0;
        if (this.isReceiverWired(inProbability)) {
            if (inProbability.size() > 1) {
                throw new IllegalStateException("AttritionalClaimsGenerator.invalidNoOfProbabilityPackets");
            }
            double probability;
            if (inProbability.size() == 1) {
                probability = inProbability.get(0).value;
            } else {
                probability = (Double) RandomNumberGeneratorFactory.getUniformGenerator().nextValue();
            }
            claimSize = parmDistribution.getDistribution().inverseF(probability) * scalingFactor;
        } else {
            claimSize = (Double) generator.nextValue() * scalingFactor;
        }
        Claim claim = ClaimPacketFactory.createPacket();
        claim.origin = this;
        claim.setClaimType(ClaimType.ATTRITIONAL);
        claim.setUltimate(claimSize);
        claim.setFractionOfPeriod(0.5d);
        getOutClaims().add(claim);
    }

    public PacketList<Severity> getInProbability() {
        return inProbability;
    }

    public void setInProbability(PacketList<Severity> inProbability) {
        this.inProbability = inProbability;
    }

    public PacketList<Frequency> getInMultiplier() {
        return inMultiplier;
    }

    public void setInMultiplier(PacketList<Frequency> inMultiplier) {
        this.inMultiplier = inMultiplier;
    }

    public RandomDistribution getParmDistribution() {
        return parmDistribution;
    }

    public void setParmDistribution(RandomDistribution parmDistribution) {
        this.parmDistribution = parmDistribution;
    }

    public DistributionModified getParmModification() {
        return parmModification;
    }

    public void setParmModification(DistributionModified parmModification) {
        this.parmModification = parmModification;
    }
}
