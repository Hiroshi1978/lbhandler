/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import web.component.api.model.Instance;
import web.component.api.model.LoadBalancer;
import web.component.impl.aws.AWS;
import web.component.impl.aws.ec2.AWSEC2;
import web.component.impl.aws.elb.AWSELB;

/**
 *
 * @author Hiroshi
 */
public class InstanceImplTest {
    
    private static final Map<String,Instance> testInstances = new HashMap<>();
    private static final List<String> testInstanceIds = new ArrayList<>();
    
    private static  Instance instanceToTestStart;
    private static  Instance instanceToTestStop;
    private static  Instance instanceToTestTerminate1;
    private static  Instance instanceToTestTerminate2;
    private static  Instance instanceToTestTerminate3;
    
    private static final Map<String, LoadBalancer> testLbs = new HashMap<>();
    private static final List<String> testLbNames = new ArrayList<>();
    static{
        testLbNames.add("test-lb-name-1");
        testLbNames.add("test-lb-name-2");
    }
    
    private static final String testImageId = "";
    private static final String testInstanceType = "";
    private static final String testInstanceLifeCycle = null;
    private static final String testInstanceTenancy = "";
    private static final String testZoneName = "";
    private static final String testPlacement = "{AvailabilityZone: " + testZoneName + ",GroupName: ,Tenancy: " + testInstanceTenancy + "}";
    
    public InstanceImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {

    	/*
    	Instance newInstance = new InstanceImpl.Builder().id("").get();
        testInstances.put(newInstance.getId(), newInstance);
        testInstanceIds.add(newInstance.getId());
        newInstance = new InstanceImpl.Builder().id("").get();
        testInstances.put(newInstance.getId(), newInstance);
        testInstanceIds.add(newInstance.getId());
        */

    	//prepare 3 test instances
        for(int i=0; i<2;i++){
            Instance newInstance = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
            //build instance from create method to obtain reference to the object of the newly created instance.
            testInstanceIds.add(newInstance.getId());
            testInstances.put(newInstance.getId(), newInstance);
            System.out.println("test instance created [" + newInstance + "]");
        }
        instanceToTestStart      = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
        instanceToTestStop       = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
        instanceToTestTerminate1 = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
        instanceToTestTerminate2 = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
        instanceToTestTerminate3 = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
        
        System.out.println("test instance created [" + instanceToTestStart + "]");
        System.out.println("test instance created [" + instanceToTestStop + "]");
        System.out.println("test instance created [" + instanceToTestTerminate1 + "]");
        System.out.println("test instance created [" + instanceToTestTerminate2 + "]");
        System.out.println("test instance created [" + instanceToTestTerminate3 + "]");
        
        //use exist load balancers for test.
        for(String testLbName : testLbNames){
            LoadBalancer testLb = LoadBalancerImpl.getExistLoadBalancerByName(testLbName);
            testLbs.put(testLbName, testLb);
            System.out.println("add test load balancer [" + testLb.toString() + "]");
        }
    	
        /*
        //create new load balancers for test.
        for(String testLbName : testLbNames){
            LoadBalancer testLb = new LoadBalancerImpl.Builder(testLbName).defaultHttpListener().zone(testZoneName).build();
            testLbs.put(testLbName, testLb);
            System.out.println("add test load balancer [" + testLb.toString() + "]");
        }
        
        for(LoadBalancer testLb : testLbs.values()){
            while(!testLb.isStarted()){
                System.out.println("wait for test load balancer [" + testLb.getName() + "] to bre available ...");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                }
            }
            System.out.println("test load balancer [" + testLb.getName() + "] is now available");
        }
        */
    }
    
    @AfterClass
    public static void tearDownClass() {
    	
    	//enable these codes if you need.
        /*
        //stop test instances.
        for(Instance toDelete : testInstances.values()){
            System.out.println("stop test instance [" + toDelete + "]");
            toDelete.stop();
        }
        
        //terminate test instances.
        for(Instance toDelete : testInstances.values()){
            System.out.println("terminate test instance [" + toDelete + "]");
            toDelete.terminate();
        }
        
        instanceToTestStart.stop();
        instanceToTestStop.stop();
        instanceToTestTerminate1.stop();
        instanceToTestTerminate2.stop();
        instanceToTestTerminate3.stop();
        
        instanceToTestStart.terminate();
        instanceToTestStop.terminate();
        instanceToTestTerminate1.terminate();
        instanceToTestTerminate2.terminate();
        instanceToTestTerminate3.terminate();
        
        System.out.println("terminate test instance [" + instanceToTestStart + "]");
        System.out.println("terminate test instance [" + instanceToTestStop + "]");
        System.out.println("terminate test instance [" + instanceToTestTerminate1 + "]");
        System.out.println("terminate test instance [" + instanceToTestTerminate2 + "]");
        System.out.println("terminate test instance [" + instanceToTestTerminate3 + "]");
        
        */
    }
    
    @Before
    public void setUp() {
        

        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of asElbInstance method, of class InstanceImpl.
     */
    @Test
    public void testAsElbInstance() {
        
        System.out.println("asElbInstance");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        
        //test instance has to be registered to test load balancer for executing this test.
        testLb.registerInstance(testInstance);
        while(testLb.getBackendInstances().size() != 1 || !testLb.getBackendInstances().get(0).equals(testInstance)){
            testLb.deregisterInstances(testLb.getBackendInstances());
            testLb.registerInstance(testInstance);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        AWSELB elb = (AWSELB)AWS.get(AWS.BlockName.ELB);
        com.amazonaws.services.elasticloadbalancing.model.Instance source = elb.describeLoadBalancers(testLb.getName()).getLoadBalancerDescriptions().get(0).getInstances().get(0);
        com.amazonaws.services.elasticloadbalancing.model.Instance viewAsElbInstance = ((InstanceImpl)testInstance).asElbInstance();

        //two instances are equal but not the same.
        assertTrue(source.equals(viewAsElbInstance));
        assertFalse(source == viewAsElbInstance);
        
        assertEquals(source.getInstanceId(), viewAsElbInstance.getInstanceId());
    }

    /**
     * Test of asEc2Instance method, of class InstanceImpl.
     */
    @Test
    public void testAsEc2Instance() {
        
        System.out.println("asEc2Instance");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        
        AWSEC2 ec2 = (AWSEC2)AWS.get(AWS.BlockName.EC2);
        com.amazonaws.services.ec2.model.Instance source = ec2.getExistEc2Instance(testInstanceIds.get(0));
        com.amazonaws.services.ec2.model.Instance viewAsEc2Instance = ((InstanceImpl)testInstance).asEc2Instance();

        //two instances are equal but not the same.
        //in our library Instance class object does not have 'state' as propertiy. so its equal method cannot work.
        //assertTrue(viewAsEc2Instance.equals(source));
        assertFalse(viewAsEc2Instance == source);
        
        assertEquals(testInstanceIds.get(0),viewAsEc2Instance.getInstanceId());
        assertEquals(testImageId,viewAsEc2Instance.getImageId());
        assertEquals(testInstanceType,viewAsEc2Instance.getInstanceType());
        assertEquals(testInstanceLifeCycle,viewAsEc2Instance.getInstanceLifecycle());
        assertEquals(testPlacement,viewAsEc2Instance.getPlacement().toString());
    }

    /**
     * Test of getLoadBalancer method, of class InstanceImpl.
     */
    @Test
    public void testGetLoadBalancer() {
        
        System.out.println("getLoadBalancer");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        
        boolean thrown = false;
        try{
            testInstance.getLoadBalancer();
        }catch(UnsupportedOperationException e){
            thrown = true;
        }
        assertTrue(thrown);
    }

    /**
     * Test of getLoadBalancers method, of class InstanceImpl.
     */
    @Test
    public void testGetLoadBalancers() {
        
        System.out.println("getLoadBalancers");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        
        boolean thrown = false;
        try{
            testInstance.getLoadBalancers();
        }catch(UnsupportedOperationException e){
            thrown = true;
        }
        assertTrue(thrown);
    }

    /**
     * Test of getId method, of class InstanceImpl.
     */
    @Test
    public void testGetId() {
        
        System.out.println("getId");
        Instance equalInstance = new InstanceImpl.Builder().id(testInstanceIds.get(0)).get();
        assertEquals(testInstanceIds.get(0), equalInstance.getId());
    }

    /**
     * Test of registerWith method, of class InstanceImpl.
     */
    @Test
    public void testRegisterWith() {
        
        System.out.println("registerWith");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        
        //make sure test load balancer has no backends.
        while(!testLb.getBackendInstances().isEmpty()){
            testLb.deregisterInstances(testLb.getBackendInstances());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        //try register.
        testInstance.registerWith(testLb);
        assertEquals(testInstance, testLb.getBackendInstances().get(0));
    }

    /**
     * Test of deregisterFrom method, of class InstanceImpl.
     */
    @Test
    public void testDeregisterFrom() {
        
        System.out.println("deregisterFrom");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        
        //make sure test load balancer has test instance as  backend.
        while(testLb.getBackendInstances().size() != 1 || !testLb.getBackendInstances().get(0).equals(testInstance)){
            //deregister all backends.
            testLb.deregisterInstances(testLb.getBackendInstances());
            //then register test instance.
            testLb.registerInstance(testInstance);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        //try deregister.
        testInstance.deregisterFrom(testLb);
        assertEquals(0, testLb.getBackendInstances().size());
    }

    /**
     * Test of equals method, of class InstanceImpl.
     */
    @Test
    public void testEquals() {
        
        System.out.println("equals");
        
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        Instance equalInstance = new InstanceImpl.Builder().id(testInstanceIds.get(0)).get();
        Instance anotherInstance = testInstances.get(testInstanceIds.get(1));
        assertTrue(testInstance.equals(testInstance));
        assertTrue(testInstance.equals(equalInstance));
        assertFalse(testInstance.equals(anotherInstance));
        assertFalse(testInstance.equals(null));
    }

    /**
     * Test of hashCode method, of class InstanceImpl.
     */
    @Test
    public void testHashCode() {
        
        System.out.println("hashCode");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        Instance equalInstance = new InstanceImpl.Builder().id(testInstanceIds.get(0)).get();
        Instance anotherInstance = testInstances.get(testInstanceIds.get(1));
        
        assertTrue(testInstance.hashCode() == testInstance.hashCode());
        assertTrue(testInstance.hashCode() == equalInstance.hashCode());
        assertFalse(testInstance.hashCode() == anotherInstance.hashCode());
    }

    /**
     * Test of getState method, of class InstanceImpl.
     */
    @Test
    public void testGetState() {
        /*
        System.out.println("getState");
        fail("The test case is a prototype.");
        */
    }

    /**
     * Test of getStateFromLB method, of class InstanceImpl.
     */
    @Test
    public void testGetStateFromLB() {
        /*
        System.out.println("getStateFromLB");
        fail("The test case is a prototype.");
        */
    }

    /**
     * Test of toString method, of class InstanceImpl.
     */
    @Test
    public void testToString() {
        
        System.out.println("toString");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        
        String expectedStringExpression = ((InstanceImpl)testInstance).asEc2Instance().toString();
        assertEquals(expectedStringExpression, testInstance.toString());
    }

    /**
     * Test of start method, of class InstanceImpl.
     */
    @Test
    public void testStart() {
        
        System.out.println("start");
        Instance testInstance = instanceToTestStart;
        
        //assert it start normally when it is stopped.
        int counter = 0;
        //first, stop it for sure.
        while(!testInstance.isStopped()){
            testInstance.stop();
            if(++counter >= 30){
                fail("time out occurred before test instance was stopped.");
                break;
            }
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        //then, start it.
        testInstance.start();
        counter = 0;
        while(!testInstance.isStarted()){
            if(++counter >= 10){
                fail("time out occurred before test instance was started.");
                break;
            }
                
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        
        //assert it is running if start method called when it is running.
        counter = 0;
        //first, start it for sure.
        while(!testInstance.isStarted()){
            testInstance.start();
            if(++counter >= 10){
                fail("time out occurred before test instance was started.");
                break;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        //then, start it.
        testInstance.start();
        assertTrue(testInstance.isStarted());
    }

    /**
     * Test of stop method, of class InstanceImpl.
     */
    @Test
    public void testStop() {
        
        System.out.println("stop");
        Instance testInstance = instanceToTestStop;
        
        //assert it stop if stop method called when it is running.
        int counter = 0;
        //first, start it for sure.
        while(!testInstance.isStarted()){
            testInstance.start();
            if(++counter >= 10){
                fail("time out occurred before test instance was started.");
                break;
            }
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        //then, stop it.
        testInstance.stop();
        
        counter = 0;
        while(!testInstance.isStopped()){
            if(++counter >= 10){
                fail("time out occurred before test instance was stopped.");
                break;
            }
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }

        //assert it is stopped if stop method called when it is stopped.
        counter = 0;
        //first, stop it for sure.
        while(!testInstance.isStopped()){
            testInstance.stop();
            if(++counter >= 10){
                fail("time out occurred before test instance was stopped.");
                break;
            }
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        //then, stop it.
        testInstance.stop();
        assertTrue(testInstance.isStopped());
    }

    /**
     * Test of getPlacement method, of class InstanceImpl.
     */
    @Test
    public void testGetPlacement() {
        
        System.out.println("getPlacement");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        
        assertEquals(testPlacement, testInstance.getPlacement());
    }

    /**
     * Test of terminate method, of class InstanceImpl.
     */
    @Test
    public void testTerminate() {
        
        System.out.println("terminate");
        Instance testInstance1 = instanceToTestTerminate1;
        Instance testInstance2 = instanceToTestTerminate2;
        Instance testInstance3 = instanceToTestTerminate3;
        
        //assert it can be terminated soon after its creation.
        testInstance1.terminate();
        
        int counter = 0;
        //first, start test instance2 for sure.
        while(!testInstance2.isStarted()){
            testInstance2.start();
            if(++counter >= 10){
                fail("time out occurred before test instance was started.");
                break;
            }
            try{
                Thread.sleep(10000);
            }catch(InterruptedException e){
            }
        }
        
        counter = 0;
        //first, stop test instance2 for sure.
        while(!testInstance3.isStopped()){
            testInstance3.stop();
            if(++counter >= 10){
                fail("time out occurred before test instance was stopped.");
                break;
            }
            try{
                Thread.sleep(10000);
            }catch(InterruptedException e){
            }
        }

        counter = 0;
        //first, terminate test instance 3 for sure.
        while(!testInstance1.isTerminated()){
            testInstance1.terminate();
            if(++counter >= 10){
                fail("time out occurred before test instance was terminated.");
                break;
            }
            try{
                Thread.sleep(10000);
            }catch(InterruptedException e){
            }            
        }
        
        //assert it can be terminated when it is running.
        testInstance2.terminate();
        //assert it can be terminated when it is stopped.
        testInstance3.terminate();
        //assert it can be terminated when it is already teminated.
        testInstance1.terminate();
        
        try{
            //wait for a while.
            Thread.sleep(3000);
        }catch(InterruptedException e){
        }
        
        assertTrue("testInstance1 state : " + testInstance1.getStateName(), testInstance1.isTerminated() || testInstance1.getStateName().equals("shutting-down") || testInstance1.getStateName().equals("Unknown state"));
        assertTrue("testInstance2 state : " + testInstance2.getStateName(), testInstance2.isTerminated() || testInstance2.getStateName().equals("shutting-down") || testInstance2.getStateName().equals("Unknown state"));
        assertTrue("testInstance3 state : " + testInstance3.getStateName(), testInstance3.isTerminated() || testInstance3.getStateName().equals("shutting-down") || testInstance3.getStateName().equals("Unknown state"));
    }
}
