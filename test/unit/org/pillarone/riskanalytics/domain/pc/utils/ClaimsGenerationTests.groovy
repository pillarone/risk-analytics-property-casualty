package org.pillarone.riskanalytics.domain.pc.utils
/**
 * @author szu
 * Date: 28.07.2008,  15:32:05
 *
 */
class ClaimsGenerationTests  extends GroovyTestCase {
    /*
    * This routine caclculates the maximum difference between two empirical distributions in an extremely elegant way:
    * It counts the maximum lead of one sorted list over the other
    * Obviously it has the side effect of sorting the lists
    * */
    double dStatistics(List <Double> list1, List <Double> list2){
        int n=list1.size()
        int ind1=0
        int ind2=0
        assert(n == list2.size())
        list1.sort()
        list2.sort()
        int maxdiff=0
        int currentdiff=0
        while((ind1 < n-1) && (ind2 < n-1)){
                 if(list1[ind1] == list2[ind2]){
                     ind1++
                     ind2++
                 }
                    else{
                     if(list1[ind1] < list2[ind2]){
                         currentdiff++
                         ind1++
                 }
                     else{
                         currentdiff--
                         ind2++
                     }
             maxdiff=Math.max(maxdiff,currentdiff.abs())
                 }
        }
        return  Math.pow(n+n,-0.5) * maxdiff
    }

    void testDStat(){
    List <Double> list1=[2.0,4.0,6.0,8.0,10.0,12.0,14.0,16.0,18.0,20.0]
    List <Double> list2=[4.0,5.0,6.0,10.0,17.0,18.0,19.0,19.0,19.0,19.0]    //max diff is list1 4 items ahead of list 2
    assert dStatistics(list1, list2)==Math.pow(20,-0.5) *4
        }

    //returns true if data passes test, false otherwise
    boolean testKSListEqualSizeLevel_1percent(List <Double> list1, List <Double> list2){
       if (dStatistics(list1, list2) <= 1.627) return true
       else return false
      }

      //returns true if data passes test, false otherwise
    boolean testKSListEqualSizeLevel_5percent(List <Double> list1, List <Double> list2){
          if (dStatistics(list1, list2) <= 1.358) return true
          else return false
          }
    //following test is de-commented because it is expensive in terms of runtime
/*
    void testNormalDistribution() {
        IRandomNumberGenerator generator1 = RandomNumberGeneratorFactory.getGenerator(DistributionType.NORMAL, ["mean": 1000.0, "stDev": 20.0])
        IRandomNumberGenerator generator2 = RandomNumberGeneratorFactory.getGenerator(DistributionType.NORMAL, ["mean": 1000.0, "stDev": 19.0])
        List <Double> list1=[]
        List <Double> list2=[]
        List <Double> list3=[]

        for (int i=0; i<10000;i++) list1.add(generator1.nextValue())
        for (int i=0; i<10000;i++) list2.add(generator1.nextValue())
        for (int i=0; i<10000;i++) list3.add(generator2.nextValue())

        assert (testKSListEqualSizeLevel_5percent(list1, list2)==true)
        assert (testKSListEqualSizeLevel_1percent(list1, list2)==true)
        assert (testKSListEqualSizeLevel_1percent(list1, list3)==false)
    }
*/
}