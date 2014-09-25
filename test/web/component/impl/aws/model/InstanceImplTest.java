/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.ArrayList;
import java.util.List;
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
    
    private static Instance testInstance;
    private static final List<Instance> testInstances = new ArrayList<>();
    private static final String testImageId = "";
    private static final String testInstanceType = "";
    private static final String testInstanceLifeCycle = "";
    private static final String testPlacement = "";
    private static final String testZoneName = "";
    private static String testInstanceId;
    //initialized with testInstace as backend server.
    private static LoadBalancer testLb1;
    //initialized without any backend server.
    private static LoadBalancer testLb2;
    private static final List<LoadBalancer> testLbs = new ArrayList<>();
    private static String testStringExpression;
    
    public InstanceImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        //build instance from create method to obtain reference to the object of the newly created instance.
        testInstance = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).create();
        testInstanceId = testInstance.getId();
        testInstances.add(testInstance);
        
        testLb1 = new LoadBalancerImpl.Builder("testLb1").defaultHttpListener().zone(testZoneName).build();
        testLbs.add(testLb1);
        testLb2 = new LoadBalancerImpl.Builder("testLb2").defaultHttpListener().zone(testZoneName).build();
        testLbs.add(testLb2);
        
        for(LoadBalancer testLb : testLbs){
            while(!testLb.isStarted()){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                }
            }
        }
        testLb1.registerInstance(testInstance);
    }
    
    @AfterClass
    public static void tearDownClass() {
        
        for(LoadBalancer testLb : testLbs)
            testLb.delete();
        
        //stop and terminate the test instances.
        for(Instance toDelete : testInstances){
            System.out.println("stop test instance [" + toDelete + "]");
            toDelete.stop();
            while(!toDelete.isStopped()){
                try{
                    Thread.sleep(3000);
                }catch(InterruptedException e){
                }
            }
            System.out.println("terminate test instance [" + toDelete + "]");
            toDelete.terminate();
        }
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
        
        AWSELB elb = (AWSELB)AWS.get(AWS.BlockName.ELB);
        com.amazonaws.services.elasticloadbalancing.model.Instance source = elb.describeLoadBalancers(testLb1.getName()).getLoadBalancerDescriptions().get(0).getInstances().get(0);
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
        
        AWSEC2 ec2 = (AWSEC2)AWS.get(AWS.BlockName.EC2);
        com.amazonaws.services.ec2.model.Instance source = ec2.getExistEc2Instance(testInstanceId);
        com.amazonaws.services.ec2.model.Instance viewAsEc2Instance = ((InstanceImpl)testInstance).asEc2Instance();

        //two instances are equal but not the same.
        assertTrue(viewAsEc2Instance.equals(source));
        assertFalse(viewAsEc2Instance == source);
        
        assertEquals(testInstanceId,viewAsEc2Instance.getInstanceId());
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
        
        int counter = 0;
        for(Instance testInstance : testInstances){
            try{
                testInstance.getLoadBalancer();
            }catch(UnsupportedOperationException e){
                counter++;
            }
        }
        assertEquals(testInstances.size(), counter);
    }

    /**
     * Test of getLoadBalancers method, of class InstanceImpl.
     */
    @Test
    public void testGetLoadBalancers() {
        System.out.println("getLoadBalancers");
        
        int counter = 0;
        for(Instance testInstance : testInstances){
            try{
                testInstance.getLoadBalancers();
            }catch(UnsupportedOperationException e){
                counter++;
            }
        }
        assertEquals(testInstances.size(), counter);
    }

    /**
     * Test of getId method, of class InstanceImpl.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        Instance equalInstance = new InstanceImpl.Builder().id(testInstanceId).get();
        assertEquals(testInstanceId, equalInstance.getId());
    }

    /**
     * Test of registerWith method, of class InstanceImpl.
     */
    @Test
    public void testRegisterWith() {
        
        System.out.println("registerWith");
        testInstance.registerWith(testLb2);
        assertEquals(testInstance, testLb2.getBackendInstances().get(0));
    }

    /**
     * Test of deregisterFrom method, of class InstanceImpl.
     */
    @Test
    public void testDeregisterFrom() {

        System.out.println("deregisterFrom");
        testInstance.deregisterFrom(testLb2);
        assertEquals(0, testLb2.getBackendInstances().size());
    }

    /**
     * Test of equals method, of class InstanceImpl.
     */
    @Test
    public void testEquals() {
        
        System.out.println("equals");
        Instance equalInstance = new InstanceImpl.Builder().id(testInstanceId).get();
        Instance anotherInstance = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
        testInstances.add(anotherInstance);
        
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
        Instance equalInstance = new InstanceImpl.Builder().id(testInstanceId).get();
        Instance anotherInstance = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
        
        assertTrue(testInstance.hashCode() == testInstance.hashCode());
        assertTrue(testInstance.hashCode() == equalInstance.hashCode());
        assertFalse(testInstance.hashCode() == anotherInstance.hashCode());
    }

    /**
     * Test of getState method, of class InstanceImpl.
     */
    @Test
    public void testGetState() {
        System.out.println("getState");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStateFromLB method, of class InstanceImpl.
     */
    @Test
    public void testGetStateFromLB() {
        System.out.println("getStateFromLB");
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class InstanceImpl.
     */
    @Test
    public void testToString() {
        
        System.out.println("toString");
        assertEquals(testStringExpression, testInstance.toString());
    }

    /**
     * Test of start method, of class InstanceImpl.
     */
    @Test
    public void testStart() {
        
        System.out.println("start");
        
        //assert it start normally when it is stopped.
        testInstance.stop();
        while(!testInstance.isStopped()){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        testInstance.start();
        int counter = 0;
        while(!testInstance.isStarted()){
            if(++counter >= 10){
                fail();
                break;
            }
                
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        //assert it is running if start method called when it is running.
        while(!testInstance.isStarted()){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        testInstance.start();
        assertTrue(testInstance.isStarted());
    }

    /**
     * Test of stop method, of class InstanceImpl.
     */
    @Test
    public void testStop() {
        
        System.out.println("stop");
        //assert it stop if stop method called when it is running.
        while(!testInstance.isStarted()){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        testInstance.stop();
        
        int counter = 0;
        while(!testInstance.isStopped()){
            
            if(++counter >= 10){
                fail();
                break;
            }
            
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }

        //assert it is stopped if stop method called when it is stopped.
        while(!testInstance.isStopped()){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        testInstance.stop();
        assertTrue(testInstance.isStopped());
    }

    /**
     * Test of getPlacement method, of class InstanceImpl.
     */
    @Test
    public void testGetPlacement() {
        System.out.println("getPlacement");
        
        for(Instance testInstance : testInstances)
            assertEquals(testPlacement, testInstance.getPlacement());
    }

    /**
     * Test of terminate method, of class InstanceImpl.
     */
    @Test
    public void testTerminate() {
        
        System.out.println("terminate");
        Instance toTerminate1 = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
        Instance toTerminate2 = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
        Instance toTerminate3 = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
        
        //assert it can be terminated soon after its creation.
        toTerminate1.terminate();
        
        while(!toTerminate2.isStarted() || !toTerminate3.isStarted()){
            try{
                Thread.sleep(3000);
            }catch(InterruptedException e){
            }
        }
        
        toTerminate2.start();
        toTerminate3.stop();
        
        while(!toTerminate2.isStarted() || !toTerminate3.isStopped()){
            try{
                Thread.sleep(3000);
            }catch(InterruptedException e){
            }
        }
        
        //assert it can be terminated when it is running.
        toTerminate2.terminate();
        //assert it can be terminated when it is stopped.
        toTerminate3.terminate();
        //assert it can be terminated when it is already teminated.
        toTerminate1.terminate();

        assertEquals("Unknown state", toTerminate1.getStateName());
        assertEquals("Unknown state", toTerminate2.getStateName());
        assertEquals("Unknown state", toTerminate3.getStateName());
    }
}
