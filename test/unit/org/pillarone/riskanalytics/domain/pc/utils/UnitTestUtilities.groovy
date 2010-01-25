package org.pillarone.riskanalytics.domain.pc.utils

import junit.framework.Assert

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
class UnitTestUtilities {

    /**
     *  Asserts that the properties of two objects are not the same, if the property value
     *  is not null or its class is not an enum. Furthermore properties with key class and
     *  metaClass have to be the same and are therefore not compared.
     */
    // todo (sku): recursive call if a property is an object
    static void allPropertiesCloned(Object clone, Object original) {
        clone.properties.each {property ->
            if (property.value instanceof Number || property.value instanceof String) {
                Assert.assertEquals "${property.key}", original.properties.get(property.key), property.value
            }
            else if (property.key != "class" && property.key != "metaClass" && property.value != null && !property.value.class?.isEnum()) {
                Assert.assertNotSame "${property.key}", original.properties.get(property.key), property.value
            }
        }
    }



}