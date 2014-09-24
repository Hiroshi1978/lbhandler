/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.io.IOException;
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
    private static LoadBalancer testLb;
    
    public InstanceImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        //build instance from create method to obtain reference to the object of the newly created instance.
        testInstance = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).create();
        testInstanceId = testInstance.getId();
        testInstances.add(testInstance);
        
        testLb = new LoadBalancerImpl.Builder("testLb").defaultHttpListener().zone(testZoneName).build();
        while(!testLb.isStarted()){
            try{
                Thread.sleep(10000);
            }catch(IOException e){
                throw new RuntimeException("failed to create test load balancer.");
            }
        }
        testLb.registerInstance(testInstance);
    }
    
    @AfterClass
    public static void tearDownClass() {
        
        testLb.deregisterInstance(testInstance);
        testLb.delete();
        
        //stop and terminate the test instances.
        for(Instance toDelete : testInstances){
            System.out.println("stop test instance [" + toDelete + "]");
            toDelete.stop();
            while(!toDelete.isStopped()){
                try{
                    Thread.sleep(3000);
                }catch(IOException e){
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
        com.amazonaws.services.elasticloadbalancing.model.Instance source = elb.describeLoadBalancers(testLb.getName()).getLoadBalancerDescriptions().get(0).getInstances().get(0);
        com.amazonaws.services.elasticloadbalancing.model.Instance viewAsElbInstance = ((InstanceImpl)testInstance).asElbInstance();

        //two instances are equal but not the same.
        assertTrue(source.equals(viewAwsElbInstance));
        assertFalse(source == viewAsElbInstance);
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
        assertEquals(testInstanceId, testInstances.get(1).getId());
    }

    /**
     * Test of registerWith method, of class InstanceImpl.
     */
    @Test
    public void testRegisterWith() {
        System.out.println("registerWith");
        fail("The test case is a prototype.");
    }

    /**
     * Test of deregisterFrom method, of class InstanceImpl.
     */
    @Test
    public void testDeregisterFrom() {
        System.out.println("deregisterFrom");
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class InstanceImpl.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class InstanceImpl.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        fail("The test case is a prototype.");
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
        fail("The test case is a prototype.");
    }

    /**
     * Test of start method, of class InstanceImpl.
     */
    @Test
    public void testStart() {
        System.out.println("start");
        fail("The test case is a prototype.");
    }

    /**
     * Test of stop method, of class InstanceImpl.
     */
    @Test
    public void testStop() {
        System.out.println("stop");
        fail("The test case is a prototype.");
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
        fail("The test case is a prototype.");
    }
}
