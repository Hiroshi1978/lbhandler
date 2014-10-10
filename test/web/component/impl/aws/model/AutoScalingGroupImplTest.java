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
import web.component.api.model.AutoScalingGroup;
import web.component.api.model.Instance;
import web.component.api.model.LaunchConfiguration;
import web.component.api.model.Zone;
import web.component.impl.aws.AWS;
import web.component.impl.aws.AWSAutoScaling;


/**
 *
 * @author ksc
 */
public class AutoScalingGroupImplTest {
    
    private static final Map<String,LaunchConfiguration> testLaunchConfigurations = new HashMap<>();
    private static final List<String> testLaunchConfigurationNames = new ArrayList<>();
    
    private static final Map<String,AutoScalingGroup> testASGroups = new HashMap<>();
    private static final List<String> testASGroupNames = new ArrayList<>();
    private static final Map<String, com.amazonaws.services.autoscaling.model.AutoScalingGroup> awsASGroups = new HashMap<>();
    
    private static final int testMaxSize = 1;
    private static final int testMinSize = 1;
    private static final int testDesiredCapacity = 1;
    
    private static final Map<String,Instance> testInstances = new HashMap<>();
    private static List<String> testInstanceIds;
    private static final String testImageId = "";
    private static final String testInstanceType = "";
    private static final String testZoneName = "";
    
    public AutoScalingGroupImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        //createTestInstances();
        getExistTestInstances();
        //createTestLaunchConfigurations();
        createTestAutoScalingGroups();
        
    }

    @AfterClass
    public static void tearDownClass() {
        
        deleteTestAutoScalingGroups();
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            throw new RuntimeException("failed to delete test autoscaling groups.", ex);
        }
        
        deleteTestLaunchConfigurations();
    }
    
    static private void createTestInstances(){
        
        int testInstanceCount = 2;
        testInstanceIds = new ArrayList<>();
        
        for(int i=0; i<testInstanceCount;i++){
            Instance newInstance = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
            //build instance from create method to obtain reference to the object of the newly created instance.
            testInstanceIds.add(newInstance.getId());
            testInstances.put(newInstance.getId(), newInstance);
            System.out.println("test instance created [" + newInstance + "]");
        }
    }
    
    static private void getExistTestInstances(){

        String[] existInstanceIds = {""};
        testInstanceIds = Arrays.asList(existInstanceIds);
        
        for(String testInstanceId : testInstanceIds){
            Instance newInstance = new InstanceImpl.Builder().id(testInstanceId).get();
            testInstances.put(newInstance.getId(), newInstance);
            System.out.println("get test instance [" + newInstance + "]");
        }
    }

    static private void createTestLaunchConfigurations(){
        
        String testLaunchConfigurationName1 = "test-lc-1";
        LaunchConfiguration testLaunchConfiguration1 = new LaunchConfigurationImpl.Builder()
                .instanceId(testInstanceIds.get(0))
                .name(testLaunchConfigurationName1)
                .create();
        String testLaunchConfigurationName2 = "test-lc-2";
        LaunchConfiguration testLaunchConfiguration2 = new LaunchConfigurationImpl.Builder()
                .instanceId(testInstanceIds.get(0))
                .name(testLaunchConfigurationName2)
                .create();
        String testLaunchConfigurationName3 = "test-lc-3";
        LaunchConfiguration testLaunchConfiguration3 = new LaunchConfigurationImpl.Builder()
                .instanceId(testInstanceIds.get(0))
                .name(testLaunchConfigurationName3)
                .create();
        
        testLaunchConfigurationNames.add(testLaunchConfigurationName1);
        testLaunchConfigurations.put(testLaunchConfigurationName1, testLaunchConfiguration1);
        testLaunchConfigurationNames.add(testLaunchConfigurationName2);
        testLaunchConfigurations.put(testLaunchConfigurationName2, testLaunchConfiguration2);
        testLaunchConfigurationNames.add(testLaunchConfigurationName3);
        testLaunchConfigurations.put(testLaunchConfigurationName3, testLaunchConfiguration3);
        
        for(String lcName : testLaunchConfigurationNames)
            System.out.println("test launch configuration " + lcName + " created.");
    }
    
    static private void deleteTestLaunchConfigurations(){
        for(String lcName : testLaunchConfigurationNames){
            testLaunchConfigurations.get(lcName).delete();
            System.out.println("test launch configuration " + lcName + " deleted.");
        }
    }
    
    static private void createTestAutoScalingGroups(){
        
        String testGroupName1 = "test-asg-1";
        AutoScalingGroup testGroup1 = new AutoScalingGroupImpl.Builder()
                .name(testGroupName1)
                .max(testMaxSize)
                .min(testMinSize)
                .desiredCapacity(testDesiredCapacity)
                //.launchConfiguration(testLaunchConfigurationNames.get(0))
                .instanceId(testInstanceIds.get(0))
                .zones(Arrays.asList(new String[]{testZoneName}))
                .create();
        
        AWSAutoScaling as = AWS.access().as();
        
        testASGroupNames.add(testGroupName1);
        testASGroups.put(testGroupName1, testGroup1);
        String testLcName1 = testGroup1.getLaunchConfigurationName();
        testLaunchConfigurationNames.add(testLcName1);
        testLaunchConfigurations.put(testLcName1, new LaunchConfigurationImpl.Builder().name(testLcName1).get());
        awsASGroups.put(testGroupName1, as.getExistAutoScalingGroupByName(testGroupName1));
        
        for(String groupName : testASGroupNames)
            System.out.println("test autoscaling group " + groupName + " created.");
    }

    static private void deleteTestAutoScalingGroups(){
        for(String groupName : testASGroupNames){
            testASGroups.get(groupName).delete();
            System.out.println("test auto scaling group " + groupName + " deleted.");
        }
    }
 
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getAutoScalingGroupARN method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetAutoScalingGroupARN() {
        
        System.out.println("getAutoScalingGroupARN");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.getAutoScalingGroupARN(), testGroup.getAutoScalingGroupARN());
    }

    /**
     * Test of getName method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetName() {
        
        System.out.println("getName");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        assertEquals(testASGroupNames.get(0), testGroup.getName());
    }

    /**
     * Test of getZones method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetZones() {

        System.out.println("getZones");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.getAvailabilityZones(), testGroup.getZones());
    }

    /**
     * Test of getCreatedTime method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetCreatedTime() {
        
        System.out.println("getCreatedTime");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.getCreatedTime(), testGroup.getCreatedTime());
    }

    /**
     * Test of getDefaultCoolDown method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetDefaultCoolDown() {
        
        System.out.println("getDefaultCoolDown");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals((int)awsGroup.getDefaultCooldown(), testGroup.getDefaultCoolDown());
    }

    /**
     * Test of getDesiredCapacity method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetDesiredCapacity() {
        
        System.out.println("getDesiredCapacity");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals((int)awsGroup.getDesiredCapacity(), testGroup.getDesiredCapacity());
   }

    /**
     * Test of getHealthCheckGracePeriod method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetHealthCheckGracePeriod() {
        
        System.out.println("getHealthCheckGracePeriod");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals((int)awsGroup.getHealthCheckGracePeriod(), testGroup.getHealthCheckGracePeriod());
    }

    /**
     * Test of getHealthCheckType method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetHealthCheckType() {
        
        System.out.println("getHealthCheckType");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.getHealthCheckType(), testGroup.getHealthCheckType());
    }

    /**
     * Test of getInstances method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetInstances() {
        
        System.out.println("getInstances");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.getInstances(), testGroup.getInstances());
    }

    /**
     * Test of getLaunchConfigurationName method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetLaunchConfigurationName() {
        
        System.out.println("getLaunchConfigurationName");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.getLaunchConfigurationName(), testGroup.getLaunchConfigurationName());
    }

    /**
     * Test of getLoadBalancerNames method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetLoadBalancerNames() {
        
        System.out.println("getLoadBalancerNames");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.getLoadBalancerNames(), testGroup.getLoadBalancerNames());
    }

    /**
     * Test of getMaxSize method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetMaxSize() {

        System.out.println("getMaxSize");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals((int)awsGroup.getMaxSize(), testGroup.getMaxSize());
    }

    /**
     * Test of getMinSize method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetMinSize() {

        System.out.println("getMinSize");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals((int)awsGroup.getMinSize(), testGroup.getMinSize());
    }

    /**
     * Test of getPlacementGroup method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetPlacementGroup() {
        
        System.out.println("getPlacementGroup");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.getPlacementGroup(), testGroup.getPlacementGroup());
    }

    /**
     * Test of getStatus method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetStatus() {
        
        System.out.println("getStatus");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.getStatus(), testGroup.getStatus());
    }

    /**
     * Test of getTerminationPolicies method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetTerminationPolicies() {
        
        System.out.println("getTerminationPolicies");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.getTerminationPolicies(), testGroup.getTerminationPolicies());
    }

    /**
     * Test of getVPCZoneIdentifier method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testGetVPCZoneIdentifier() {
        
        System.out.println("getVPCZoneIdentifier");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.getVPCZoneIdentifier(), testGroup.getVPCZoneIdentifier());
    }

    /**
     * Test of setDesiredCapacity method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testSetDesiredCapacity() {
        System.out.println("setDesiredCapacity");
        int desiredCapacity = 0;
        AutoScalingGroupImpl instance = null;
        instance.setDesiredCapacity(desiredCapacity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of compareTo method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        AutoScalingGroup o = null;
        AutoScalingGroupImpl instance = null;
        int expResult = 0;
        int result = instance.compareTo(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of attachInstances method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testAttachInstances() {
        System.out.println("attachInstances");
        List<Instance> instances = null;
        AutoScalingGroupImpl instance = null;
        instance.attachInstances(instances);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of attachInstance method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testAttachInstance() {
        System.out.println("attachInstance");
        Instance instance_2 = null;
        AutoScalingGroupImpl instance = null;
        instance.attachInstance(instance_2);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of detachInstances method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testDetachInstances() {
        System.out.println("detachInstances");
        List<Instance> instances = null;
        AutoScalingGroupImpl instance = null;
        instance.detachInstances(instances);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of detachInstance method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testDetachInstance() {
        System.out.println("detachInstance");
        Instance instance_2 = null;
        AutoScalingGroupImpl instance = null;
        instance.detachInstance(instance_2);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setZones method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testSetZones() {
        System.out.println("setZones");
        List<Zone> zones = null;
        AutoScalingGroupImpl instance = null;
        instance.setZones(zones);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDefaultCoolDown method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testSetDefaultCoolDown() {
        System.out.println("setDefaultCoolDown");
        int defaultCoolDown = 0;
        AutoScalingGroupImpl instance = null;
        instance.setDefaultCoolDown(defaultCoolDown);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setHealthCheckGracePeriod method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testSetHealthCheckGracePeriod() {
        System.out.println("setHealthCheckGracePeriod");
        int healthCheckGracePeriod = 0;
        AutoScalingGroupImpl instance = null;
        instance.setHealthCheckGracePeriod(healthCheckGracePeriod);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setHealthCheckType method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testSetHealthCheckType() {
        System.out.println("setHealthCheckType");
        String healthCheckType = "";
        AutoScalingGroupImpl instance = null;
        instance.setHealthCheckType(healthCheckType);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLaunchConfigurationName method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testSetLaunchConfigurationName() {
        System.out.println("setLaunchConfigurationName");
        String launchConfigurationName = "";
        AutoScalingGroupImpl instance = null;
        instance.setLaunchConfigurationName(launchConfigurationName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMaxSize method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testSetMaxSize() {
        System.out.println("setMaxSize");
        int maxSize = 0;
        AutoScalingGroupImpl instance = null;
        instance.setMaxSize(maxSize);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMinSize method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testSetMinSize() {
        System.out.println("setMinSize");
        int minSize = 0;
        AutoScalingGroupImpl instance = null;
        instance.setMinSize(minSize);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPlacementGroup method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testSetPlacementGroup() {
        System.out.println("setPlacementGroup");
        String placementGroup = "";
        AutoScalingGroupImpl instance = null;
        instance.setPlacementGroup(placementGroup);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setVPCZoneIdentifier method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testSetVPCZoneIdentifier() {
        System.out.println("setVPCZoneIdentifier");
        String vpcZoneIdentifier = "";
        AutoScalingGroupImpl instance = null;
        instance.setVPCZoneIdentifier(vpcZoneIdentifier);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTerminationPolicies method, of class AutoScalingGroupImpl.
     */
    @Test
    public void testSetTerminationPolicies() {
        System.out.println("setTerminationPolicies");
        List<String> terminationPolicies = null;
        AutoScalingGroupImpl instance = null;
        instance.setTerminationPolicies(terminationPolicies);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    @Test
    public void testToString(){
        
        System.out.println("toString");
        AutoScalingGroup testGroup = testASGroups.get(testASGroupNames.get(0));
        com.amazonaws.services.autoscaling.model.AutoScalingGroup awsGroup =
                awsASGroups.get(testASGroupNames.get(0));
        assertEquals(awsGroup.toString(), testGroup.toString());
    }
    
    @Test 
    public void testExists(){
        
        System.out.println("exists");
        fail("The test case is a prototype.");
    }
    
}
