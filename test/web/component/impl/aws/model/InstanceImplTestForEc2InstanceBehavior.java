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
import web.component.api.model.Instance;
import web.component.impl.aws.AWS;
import web.component.impl.aws.ec2.AWSEC2;

/**
 *
 * @author Hiroshi
 */
public class InstanceImplTestForEc2InstanceBehavior {
    
    private static final Map<String,Instance> testInstances = new HashMap<>();
    private static List<String> testInstanceIds;
    
    private static  Instance instanceToTestStart;
    private static  Instance instanceToTestStop;
    private static  Instance instanceToTestTerminate1;
    private static  Instance instanceToTestTerminate2;
    private static  Instance instanceToTestTerminate3;
    
    private static final String testImageId = "";
    private static final String testInstanceType = "";
    private static final String testInstanceLifeCycle = null;
    private static final String testInstanceTenancy = "";
    private static final String testZoneName = "";
    private static final String testPlacement = "{AvailabilityZone: " + testZoneName + ",GroupName: ,Tenancy: " + testInstanceTenancy + "}";
    
    public InstanceImplTestForEc2InstanceBehavior() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        getExistTestInstances();
        //createTestInstances();
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

        String[] existInstanceIds = {"",""};
        testInstanceIds = Arrays.asList(existInstanceIds);
        
        for(String testInstanceId : testInstanceIds){
            Instance newInstance = new InstanceImpl.Builder().id(testInstanceId).get();
            testInstances.put(newInstance.getId(), newInstance);
            System.out.println("get test instance [" + newInstance + "]");
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
        /*
        //stop test instances.
        for(Instance toDelete : testInstances.values()){
            System.out.println("stop test instance [" + toDelete + "]");
            toDelete.stop();
        }
        */
    }
    
    @Before
    public void setUp() {
        

        
    }
    
    @After
    public void tearDown() {
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
     * Test of getId method, of class InstanceImpl.
     */
    @Test
    public void testGetId() {
        
        System.out.println("getId");
        Instance equalInstance = new InstanceImpl.Builder().id(testInstanceIds.get(0)).get();
        assertEquals(testInstanceIds.get(0), equalInstance.getId());
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
    //@Test
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
    //@Test
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
    //@Test
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
    
    @Test
    public void testGetImageId(){
    
        System.out.println("getImageId");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        assertEquals(testImageId, testInstance.getImageId());
        
    }

    @Test
    public void testGetInstanceType(){
    
        System.out.println("getInstanceType");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        assertEquals(testInstanceType, testInstance.getInstanceType());
        
    }

    @Test
    public void testGetLifeCycle(){
    
        System.out.println("getLifeCycle");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        assertEquals(testInstanceLifeCycle, testInstance.getLifeCycle());
        
    }

    @Test
    public void testGetPublicIpAddress(){
    
        System.out.println("getPublicIpAddress");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        assertEquals(((InstanceImpl)testInstance).asEc2Instance().getPublicIpAddress(), testInstance.getPublicIpAddress());
        
    }
    
    @Test
    public void testGetZoneName(){
    
        System.out.println("getZoneName");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        assertEquals(testZoneName, testInstance.getZoneName());
        
    }
    
    @Test
    public void testGetStateName(){
    
        System.out.println("getStateName");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        
        AWSEC2 ec2 = (AWSEC2)AWS.get(AWS.BlockName.EC2);
        String expectedStateName = ec2.getInstanceState(testInstance.getId()).getName();
        assertEquals(expectedStateName, testInstance.getStateName());
        
    }
    
    @Test
    public void testGetStateCode(){
    
        System.out.println("getStateCode");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        
        AWSEC2 ec2 = (AWSEC2)AWS.get(AWS.BlockName.EC2);
        int expectedStateCode = ec2.getInstanceState(testInstance.getId()).getCode();
        assertEquals(expectedStateCode, (int)testInstance.getStateCode());
        
    }

    @Test
    public void testGetStateTransitionReason() {

        System.out.println("getStateTransitionReason");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        
        AWSEC2 ec2 = (AWSEC2)AWS.get(AWS.BlockName.EC2);
        String expectedStateTransitionReason = ec2.getInstanceStateTransitionReason(testInstance.getId());
        assertEquals(expectedStateTransitionReason, testInstance.getStateTransitionReason());        
    }
}
