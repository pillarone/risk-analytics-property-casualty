package org.pillarone.riskanalytics.domain.pc.generators.frequency;

import org.apache.commons.lang.ArrayUtils;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.constants.FrequencyBase;
import org.pillarone.riskanalytics.domain.pc.generators.GeneratorCachingComponent;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.utils.*;

import java.util.HashMap;

/**
 * This component generates frequencies according to the used RandomNumberGenerator.
 * Frequencies are generated as soon as the doCalculation() method is invoked.
 * Afterwards they are available at the outFrequency channel.<br>
 * <h3>Available distributions</h3>
 * <ul>
 * <li>Poisson</li>
 * <li>Negative binomial</li>
 * <li>Constant</li>
 * </ul>
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FrequencyGenerator extends GeneratorCachingComponent {

    private DistributionModified modifier = DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap());

    private FrequencyBase parmBase = FrequencyBase.ABSOLUTE;
    private RandomDistribution parmDistribution = DistributionType.getStrategy(
        FrequencyDistributionType.CONSTANT,
        ArrayUtils.toMap(new Object[][]{
            {"constant", 0d}
        }));

    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private PacketList<Frequency> outFrequency = new PacketList<Frequency>(Frequency.class);

    public void validateParameterization() {
        if (parmDistribution == null) {
            throw new IllegalStateException("FrequencyGenerator.missingDistribution");
        }
        if (!parmBase.equals(FrequencyBase.ABSOLUTE) && !isReceiverWired(inUnderwritingInfo)) {
            throw new IllegalStateException("FrequencyGenerator.invalidFrequencyBase");
        }
        if (parmBase.equals(FrequencyBase.NUMBER_OF_POLICIES) && !isReceiverWired(inUnderwritingInfo)) {
            throw new IllegalStateException("FrequencyGenerator.invalidFreqeuncyBaseNoOfPolicies");
        }
        super.validateParameterization();
    }

    public void doCalculation() {
        IRandomNumberGenerator generator = getCachedGenerator(parmDistribution, modifier);
        Frequency frequency = FrequencyPacketFactory.createPacket();
        if (parmBase.equals(FrequencyBase.NUMBER_OF_POLICIES)) {
            frequency.value = (((Double) generator.nextValue()) * inUnderwritingInfo.get(0).getNumberOfPolicies());
        } else {
            frequency.value = generator.nextValue().intValue();
        }
        outFrequency.add(frequency);
    }

    public RandomDistribution getParmDistribution() {
        return parmDistribution;
    }

    public void setParmDistribution(RandomDistribution parmDistribution) {
        this.parmDistribution = parmDistribution;
    }

    public FrequencyBase getParmBase() {
        return parmBase;
    }

    public void setParmBase(FrequencyBase parmBase) {
        this.parmBase = parmBase;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfo> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<Frequency> getOutFrequency() {
        return outFrequency;
    }

    public void setOutFrequency(PacketList<Frequency> outFrequency) {
        this.outFrequency = outFrequency;
    }
}
