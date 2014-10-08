/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import web.component.api.model.LaunchConfiguration;
import web.component.impl.aws.AWS;
import web.component.impl.aws.AWSAutoScaling;

/**
 *
 * @author Hiroshi
 */
public class LaunchConfigurationImplTest {
    
    private static final Map<String,LaunchConfiguration> testLaunchConfigurations = new HashMap<>();
    private static final List<String> testLaunchConfigurationNames = new ArrayList<>();
    private static final Map<String,com.amazonaws.services.autoscaling.model.LaunchConfiguration> awsLaunchConfigurations = new HashMap<>();
    
    private static final List<String> testInstanceIds = Arrays.asList(new String[]{""});

    public LaunchConfigurationImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        String testLaunchConfigurationName1 = "test-lc-1";
        LaunchConfiguration testLaunchConfiguration1 = new LaunchConfigurationImpl.Builder()
                .associatePublicIpAddress(true)
                .ebsOptimized(true)
                .instanceId(testInstanceIds.get(0))
                .name(testLaunchConfigurationName1)
                .create();
        String testLaunchConfigurationName2 = "test-lc-2";
        LaunchConfiguration testLaunchConfiguration2 = new LaunchConfigurationImpl.Builder()
                .associatePublicIpAddress(true)
                .ebsOptimized(true)
                .instanceId(testInstanceIds.get(0))
                .name(testLaunchConfigurationName2)
                .create();
        String testLaunchConfigurationName3 = "test-lc-3";
        LaunchConfiguration testLaunchConfiguration3 = new LaunchConfigurationImpl.Builder()
                .associatePublicIpAddress(true)
                .ebsOptimized(true)
                .instanceId(testInstanceIds.get(0))
                .name(testLaunchConfigurationName3)
                .create();
        
        AWSAutoScaling as = AWS.access().as();
        
        testLaunchConfigurationNames.add(testLaunchConfigurationName1);
        testLaunchConfigurations.put(testLaunchConfigurationName1, testLaunchConfiguration1);
        awsLaunchConfigurations.put(testLaunchConfigurationName1, as.getExistLaunchConfiguration(testLaunchConfigurationName1));
        testLaunchConfigurationNames.add(testLaunchConfigurationName2);
        testLaunchConfigurations.put(testLaunchConfigurationName2, testLaunchConfiguration2);
        awsLaunchConfigurations.put(testLaunchConfigurationName2, as.getExistLaunchConfiguration(testLaunchConfigurationName2));
        testLaunchConfigurationNames.add(testLaunchConfigurationName3);
        testLaunchConfigurations.put(testLaunchConfigurationName3, testLaunchConfiguration3);
        awsLaunchConfigurations.put(testLaunchConfigurationName3, as.getExistLaunchConfiguration(testLaunchConfigurationName3));
        
        for(String name : testLaunchConfigurationNames)
            System.out.println("test launch configuration " + name + " created.");
    }
    
    @AfterClass
    public static void tearDownClass() {
        for(String name : testLaunchConfigurationNames){
            testLaunchConfigurations.get(name).delete();
            System.out.println("test launch configuration " + name + " deleted.");
        }
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of asAwsLaunchConfiguration method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testAsAwsLaunchConfiguration() {
        
        System.out.println("asAwsLaunchConfiguration");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        com.amazonaws.services.autoscaling.model.LaunchConfiguration expected = 
                awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(expected, ((LaunchConfigurationImpl)lc).asAwsLaunchConfiguration());
    }

    /**
     * Test of getName method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetName() {
        
        System.out.println("getName");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        assertEquals(testLaunchConfigurationNames.get(0),lc.getName());
        
    }

    /**
     * Test of compareTo method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testCompareTo() {
        
        System.out.println("compareTo");
        LaunchConfiguration lc1 = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        LaunchConfiguration lc2 = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        LaunchConfiguration lc3 = testLaunchConfigurations.get(testLaunchConfigurationNames.get(1));
        
        assertTrue(lc1.compareTo(lc1) == 0);
        assertTrue(lc1.compareTo(lc2) == 0);
        assertTrue(lc1.compareTo(lc3) < 0);
        assertTrue(lc2.compareTo(lc1) == 0);
        assertTrue(lc2.compareTo(lc2) == 0);
        assertTrue(lc2.compareTo(lc3) < 0);
        assertTrue(lc3.compareTo(lc1) > 0);
        assertTrue(lc3.compareTo(lc2) > 0);
        assertTrue(lc3.compareTo(lc3) == 0);

    }

    /**
     * Test of getAssociatePublicIpAddress method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetAssociatePublicIpAddress() {
        
        System.out.println("getAssociatePublicIpAddress");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(true, lc.getAssociatePublicIpAddress());
    }

    /**
     * Test of getEbsOptimized method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetEbsOptimized() {

        System.out.println("getEbsOptimized");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(true, lc.getEbsOptimized());
    }

    /**
     * Test of getIamInstanceProfile method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetIamInstanceProfile() {
        
        System.out.println("getIamInstanceProfile");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        com.amazonaws.services.autoscaling.model.LaunchConfiguration awsLc = 
                awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(awsLc.getIamInstanceProfile(), lc.getIamInstanceProfile());
    }

    /**
     * Test of getImageId method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetImageId() {
        
        System.out.println("getImageId");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        com.amazonaws.services.autoscaling.model.LaunchConfiguration awsLc = 
                awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(awsLc.getImageId(), lc.getImageId());
    }

    /**
     * Test of getInstanceType method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetInstanceType() {
        
        System.out.println("getInstanceType");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        com.amazonaws.services.autoscaling.model.LaunchConfiguration awsLc = 
                awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(awsLc.getInstanceType(), lc.getInstanceType());
    }

    /**
     * Test of getKernelId method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetKernelId() {
        
        System.out.println("getKernelId");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        com.amazonaws.services.autoscaling.model.LaunchConfiguration awsLc = 
                awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(awsLc.getKernelId(), lc.getKernelId());
    }

    /**
     * Test of getKeyName method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetKeyName() {
        
        System.out.println("getKeyName");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        com.amazonaws.services.autoscaling.model.LaunchConfiguration awsLc = 
                awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(awsLc.getKeyName(), lc.getKeyName());
    }

    /**
     * Test of getPlacementTenancy method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetPlacementTenancy() {
        
        System.out.println("getPlacementTenancy");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        com.amazonaws.services.autoscaling.model.LaunchConfiguration awsLc = 
                awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(awsLc.getPlacementTenancy(), lc.getPlacementTenancy());
    }

    /**
     * Test of getRamdiskId method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetRamdiskId() {
        
        System.out.println("getRamdiskId");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        com.amazonaws.services.autoscaling.model.LaunchConfiguration awsLc = 
                awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(awsLc.getRamdiskId(), lc.getRamdiskId());
    }

    /**
     * Test of getSecurityGroups method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetSecurityGroups() {
        
        System.out.println("getSecurityGroups");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        com.amazonaws.services.autoscaling.model.LaunchConfiguration awsLc = 
                awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(awsLc.getSecurityGroups(), lc.getSecurityGroups());
    }

    /**
     * Test of getSpotPrice method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetSpotPrice() {
        
        System.out.println("getSpotPrice");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        com.amazonaws.services.autoscaling.model.LaunchConfiguration awsLc = 
                awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(awsLc.getSpotPrice(), lc.getSpotPrice());
    }

    /**
     * Test of getUserData method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testGetUserData() {
        
        System.out.println("getUserData");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        com.amazonaws.services.autoscaling.model.LaunchConfiguration awsLc = 
                awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(awsLc.getUserData(), lc.getUserData());
    }

    /**
     * Test of delete method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testDelete() {
        
        System.out.println("delete");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(2));
        
        lc.delete();
        
        AWSAutoScaling as = AWS.access().as();
        assertEquals(null, as.getExistLaunchConfiguration(lc.getName()));
    }

    /**
     * Test of toString method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testToString() {
        
        System.out.println("toString");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        String expected = awsLaunchConfigurations.get(testLaunchConfigurationNames.get(0)).toString();
        assertEquals(expected, lc.toString());
    }

    /**
     * Test of equals method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testEquals() {
        
        System.out.println("equals");
        LaunchConfiguration lc1 = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        LaunchConfiguration lc2 = testLaunchConfigurations.get(testLaunchConfigurationNames.get(1));
        
        assertTrue(lc1.equals(lc1));
        assertFalse(lc1.equals(lc2));
        assertFalse(lc2.equals(lc1));
        assertTrue(lc2.equals(lc2));
    }

    /**
     * Test of hashCode method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testHashCode() {
        
        System.out.println("hashCode");
        LaunchConfiguration lc1 = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        LaunchConfiguration lc2 = testLaunchConfigurations.get(testLaunchConfigurationNames.get(1));
        
        assertTrue(lc1.hashCode() == lc1.hashCode());
        assertFalse(lc1.hashCode() == lc2.hashCode());
        assertTrue(lc2.hashCode() == lc2.hashCode());
    }

    /**
     * Test of isAssociatePublicIpAddress method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testIsAssociatePublicIpAddress() {
        
        System.out.println("isAssociatePublicIpAddress");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(true, lc.isAssociatePublicIpAddress());
    }

    /**
     * Test of isEbsOptimized method, of class LaunchConfigurationImpl.
     */
    @Test
    public void testIsEbsOptimized() {
        
        System.out.println("isEbsOptimized");
        LaunchConfiguration lc = testLaunchConfigurations.get(testLaunchConfigurationNames.get(0));
        
        assertEquals(true, lc.isEbsOptimized());

    }
    
}
