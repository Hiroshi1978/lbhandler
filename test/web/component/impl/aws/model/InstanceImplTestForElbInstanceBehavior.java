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
import web.component.api.model.LoadBalancer;
import web.component.impl.aws.AWS;
import web.component.impl.aws.elb.AWSELB;

/**
 *
 * @author Hiroshi
 */
public class InstanceImplTestForElbInstanceBehavior {
    
    private static final Map<String,Instance> testInstances = new HashMap<>();
    private static List<String> testInstanceIds;
    
    private static final Map<String, LoadBalancer> testLbs = new HashMap<>();
    private static List<String> testLbNames;
    
    private static final String testImageId = "";
    private static final String testInstanceType = "";
    private static final String testZoneName = "";
    
    public InstanceImplTestForElbInstanceBehavior() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        //if you use instances already launched in cloud.
        //getExistTestInstances();
        //if you newly create instances for test.
        createTestInstances();
            
        //if you use load balancers already launched in cloud.
        //getExistTestLbs();
        //if you newly create load balancers for test.
        createTestLbs();
        

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

        String[] existInstanceIds = {"specify","IDs","of","your","test","instances","here"};
        testInstanceIds = Arrays.asList(existInstanceIds);
        
        for(String testInstanceId : testInstanceIds){
            Instance newInstance = new InstanceImpl.Builder().id(testInstanceId).get();
            testInstances.put(newInstance.getId(), newInstance);
            System.out.println("get test instance [" + newInstance + "]");
        }
    }
    
    static private void createTestLbs(){
        
        String[] newLbNames = {"test-lb-1","test-lb-2"};
        testLbNames = Arrays.asList(newLbNames);
        
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
    }
    
    static void getExistTestLbs(){

        String[] newLbNames = {"test-lb-1","test-lb-2"};
        testLbNames = Arrays.asList(newLbNames);
        
        //use exist load balancers for test.
        for(String testLbName : testLbNames){
            LoadBalancer testLb = LoadBalancerImpl.getExistLoadBalancerByName(testLbName);
            testLbs.put(testLbName, testLb);
            System.out.println("add test load balancer [" + testLb.toString() + "]");
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
     * Test of getLoadBalancer method, of class InstanceImpl.
     */
    @Test
    public void testGetLoadBalancer() {
        
        System.out.println("getLoadBalancer");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        LoadBalancer testLb1 = testLbs.get(testLbNames.get(0));
        LoadBalancer testLb2 = testLbs.get(testLbNames.get(1));
        
        //make sure test load balancer1 has test instance as backend.
        while(!testLb1.getBackendInstances().contains(testInstance)){

            testLb1.registerInstance(testInstance);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        //make sure test load balancer2 does not have test instance as backend.
        while(testLb2.getBackendInstances().contains(testInstance)){

            testLb2.deregisterInstance(testInstance);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        //try test method.
        LoadBalancer actualLb = testInstance.getLoadBalancer();
        
        //restore the state before test.
        testLb1.deregisterInstance(testInstance);
        
        assertEquals(testLb1, actualLb);
    }

    /**
     * Test of getLoadBalancers method, of class InstanceImpl.
     */
    @Test
    public void testGetLoadBalancers() {
        
        System.out.println("getLoadBalancers");
        Instance testInstance = testInstances.get(testInstanceIds.get(0));
        LoadBalancer testLb1 = testLbs.get(testLbNames.get(0));
        LoadBalancer testLb2 = testLbs.get(testLbNames.get(1));
        
        //make sure test load balancer1 has test instance as backend.
        while(!testLb1.getBackendInstances().contains(testInstance)){

            testLb1.registerInstance(testInstance);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        //make sure test load balancer2 does not have test instance as backend.
        while(!testLb2.getBackendInstances().contains(testInstance)){

            testLb2.registerInstance(testInstance);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        List<LoadBalancer> actualLbs = testInstance.getLoadBalancers();
        
        //restore the state before test.
        testLb1.deregisterInstance(testInstance);
        testLb2.deregisterInstance(testInstance);
        
        assertTrue(actualLbs.contains(testLb1) && actualLbs.contains(testLb2));
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
        Instance actualInstance = testLb.getBackendInstances().get(0);
        
        //restore the state before test.
        testLb.deregisterInstance(testInstance);
        
        assertEquals(testInstance, actualInstance);
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
        while(!testLb.getBackendInstances().contains(testInstance)){
            testLb.registerInstance(testInstance);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        
        //try deregister.
        testInstance.deregisterFrom(testLb);
        
        assertTrue(testLb.getBackendInstances().isEmpty());
    }
}
