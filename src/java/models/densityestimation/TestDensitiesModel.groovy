package models.densityestimation

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.constants.PremiumBase
import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.RandomNumberGeneratorFactory

/**
 * @author martin.melchior (at) fhnw (dot) ch
 */
class TestDensitiesModel extends StochasticModel {

    AttritionalClaimsGenerator claims
    ReinsuranceContract sl
    MultiModalDistribution triModalClaims

    void initComponents() {
        claims = new AttritionalClaimsGenerator(
            parmDistribution: DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 100d, "stDev": 20d]),
            parmBase: Exposure.ABSOLUTE)
        sl = new ReinsuranceContract(
            parmContractStrategy: ReinsuranceContractType.getStrategy(
                ReinsuranceContractType.STOPLOSS,
                ["attachmentPoint": 120d, "limit": 20d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d, "coveredByReinsurer": 1d])
        )
        allComponents << claims
        allComponents << sl
        addStartComponent claims


        triModalClaims = new MultiModalDistribution()
        allComponents << triModalClaims
        addStartComponent triModalClaims
    }

    void wireComponents() {
        sl.inClaims = claims.outClaims
    }
}


class MultiModalDistribution extends Component {

    PacketList<Claim> outClaims = new PacketList(Claim)
    RandomDistribution parmDistribution = DistributionType.getStrategy(DistributionType.NORMAL, ["mean": 100d, "stDev": 20d])
    AbstractMultiDimensionalParameter parmShifts = new SimpleMultiDimensionalParameter([85d, 100d])
    RandomDistribution parmMixDistribution = DistributionType.getStrategy(DistributionType.UNIFORM, ["a": 0d, "b": 1d])

    Long claimsIdx = 0

    public void doCalculation() {
        IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getGenerator(parmDistribution)
        IRandomNumberGenerator mixGenerator = RandomNumberGeneratorFactory.getGenerator(parmMixDistribution)
        double u = (Double) mixGenerator.nextValue()
        int index = Math.floor(u * (parmShifts.values.size() + 1))
        double shift = index == 0 ? 0d : (Double) parmShifts.values.get(index - 1)
        double x = shift + (Double) generator.nextValue()
        outClaims << new Claim(origin: this, claimType: ClaimType.ATTRITIONAL,
            value: x,
            fractionOfPeriod: 0.5d)
    }
}