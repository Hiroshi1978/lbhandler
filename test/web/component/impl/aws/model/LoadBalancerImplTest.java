/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import com.amazonaws.services.elasticloadbalancing.model.ListenerDescription;
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
import web.component.api.model.LoadBalancerListener;
import web.component.api.model.Subnet;
import web.component.api.model.Zone;
import web.component.impl.aws.AWS;
import web.component.impl.aws.elb.AWSELB;

/**
 *
 * @author ksc
 */
public class LoadBalancerImplTest {

    private static final Map<String, LoadBalancer> testLbs = new HashMap<>();
    private static List<String> testLbNames;

    private static final Map<String, Instance> testBackendInstances = new HashMap<>();
    private static List<String> testBackendInstanceIds;
    private static final String testImageId = "";
    private static final String testInstanceType = "";
    private static final String testZoneName = "";
    
    public LoadBalancerImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        getExistTestLbs();
        //createTestLbs();
        
        getExistTestBackendInstances();
        //createTestBackendInstances();
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

        String[] newLbNames = {""};
        testLbNames = Arrays.asList(newLbNames);
        
        //use exist load balancers for test.
        for(String testLbName : testLbNames){
            LoadBalancer testLb = LoadBalancerImpl.getExistLoadBalancerByName(testLbName);
            testLbs.put(testLbName, testLb);
            System.out.println("add test load balancer [" + testLb.toString() + "]");
        }
    }
    
    static void createTestBackendInstances(){
        int testInstanceCount = 2;
        testBackendInstanceIds = new ArrayList<>();
        
        for(int i=0; i<testInstanceCount;i++){
            Instance newInstance = new InstanceImpl.Builder().imageId(testImageId).type(testInstanceType).zoneName(testZoneName).create();
            //build instance from create method to obtain reference to the object of the newly created instance.
            testBackendInstanceIds.add(newInstance.getId());
            testBackendInstances.put(newInstance.getId(), newInstance);
            System.out.println("test instance created [" + newInstance + "]");
        }         
    }
    
    static void getExistTestBackendInstances(){
        String[] existInstanceIds = {"",""};
        testBackendInstanceIds = Arrays.asList(existInstanceIds);
        
        for(String testInstanceId : testBackendInstanceIds){
            Instance newInstance = new InstanceImpl.Builder().id(testInstanceId).get();
            testBackendInstances.put(newInstance.getId(), newInstance);
            System.out.println("get test instance [" + newInstance + "]");
        }        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of isStarted method, of class LoadBalancerImpl.
     */
    @Test
    public void testIsStarted() {
        
        System.out.println("isStarted");
        fail("The test case is a prototype.");
    }

    /**
     * Test of createListeners method, of class LoadBalancerImpl.
     */
    @Test
    public void testCreateListeners() {
        
        System.out.println("createListeners");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        
        LoadBalancerListener testListener1 = new LoadBalancerListenerImpl.Builder()
                .instancePort(80)
                .instanceProtocol("HTTP")
                .servicePort(8088)
                .serviceProtocol("HTTP")
                .build();
        LoadBalancerListener testListener2 = new LoadBalancerListenerImpl.Builder()
                .instancePort(80)
                .instanceProtocol("HTTP")
                .servicePort(8089)
                .serviceProtocol("HTTP")
                .build();
        
        List<LoadBalancerListener> testListeners = new ArrayList<>();
        testListeners.add(testListener1);
        testListeners.add(testListener2);
        
        //make sure load balancer does not have listeners with test port.
        for(LoadBalancerListener testListener : testListeners)
            testListener.deleteFrom(testLb);
        
        //try create listeners.
        testLb.createListeners(testListeners);
        
        //get snapshot of the attached listeners.
        List<LoadBalancerListener> results = new ArrayList<>(testLb.getListeners());
        
        //restore the state before test.
        for(LoadBalancerListener testListener : testListeners)
            testListener.deleteFrom(testLb);
        
        assertTrue(results.contains(testListener1) && results.contains(testListener2));
    }

    /**
     * Test of deleteListeners method, of class LoadBalancerImpl.
     */
    @Test
    public void testDeleteListeners() {
        
        System.out.println("deleteListeners");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        
        LoadBalancerListener testListener1 = new LoadBalancerListenerImpl.Builder()
                .instancePort(80)
                .instanceProtocol("HTTP")
                .servicePort(8088)
                .serviceProtocol("HTTP")
                .build();
        LoadBalancerListener testListener2 = new LoadBalancerListenerImpl.Builder()
                .instancePort(80)
                .instanceProtocol("HTTP")
                .servicePort(8089)
                .serviceProtocol("HTTP")
                .build();
        
        List<LoadBalancerListener> testListeners = new ArrayList<>();
        testListeners.add(testListener1);
        testListeners.add(testListener2);
        
        //make sure load balancer does not have listeners with test port.
        for(LoadBalancerListener testListener : testListeners){
            testListener.deleteFrom(testLb);
            testListener.addTo(testLb);
        }
        
        //try delete listeners.
        testLb.deleteListeners(testListeners);
        
        //get snapshot of the attached listeners.
        List<LoadBalancerListener> results = new ArrayList<>(testLb.getListeners());
        
        //restore the state before test if needed.
        for(LoadBalancerListener testListener : testListeners)
            testListener.deleteFrom(testLb);
        
        assertTrue(!results.contains(testListener1) && !results.contains(testListener2));
    }

    /**
     * Test of createListener method, of class LoadBalancerImpl.
     */
    @Test
    public void testCreateListener() {
        
        System.out.println("createListener");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        
        LoadBalancerListener testListener = new LoadBalancerListenerImpl.Builder()
                .instancePort(80)
                .instanceProtocol("HTTP")
                .servicePort(8088)
                .serviceProtocol("HTTP")
                .build();
        
        //make sure test load balancer does not have tes listener.
        testListener.deleteFrom(testLb);
        
        //try create listener.
        testLb.createListener(testListener);
        
        //get snapshot of the attached listeners.
        List<LoadBalancerListener> results = new ArrayList<>(testLb.getListeners());
        
        //restore the state before test.
        testListener.deleteFrom(testLb);
        
        assertTrue(results.contains(testListener));
    }

    /**
     * Test of deleteListener method, of class LoadBalancerImpl.
     */
    @Test
    public void testDeleteListener() {
        
        System.out.println("deleteListener");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        
        LoadBalancerListener testListener = new LoadBalancerListenerImpl.Builder()
                .instancePort(80)
                .instanceProtocol("HTTP")
                .servicePort(8088)
                .serviceProtocol("HTTP")
                .build();
        
        //make sure test load balancer does not have tes listener.
        testListener.deleteFrom(testLb);
        testListener.addTo(testLb);
        
        //try delete listener.
        testLb.deleteListener(testListener);
        
        //get snapshot of the attached listeners.
        List<LoadBalancerListener> results = new ArrayList<>(testLb.getListeners());
        
        //restore the state before test.
        testListener.deleteFrom(testLb);
        
        assertTrue(!results.contains(testListener));
    }

    /**
     * Test of registerInstances method, of class LoadBalancerImpl.
     */
    @Test
    public void testRegisterInstances() {
        
        System.out.println("registerInstances");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        Instance testInstance1 = testBackendInstances.get(testBackendInstanceIds.get(0));
        Instance testInstance2 = testBackendInstances.get(testBackendInstanceIds.get(1));
        List<Instance> testInstances = new ArrayList<>();
        testInstances.add(testInstance1);
        testInstances.add(testInstance2);
        
        //make sure test load balancer does not have test instances as backend.
        for(Instance testInstance : testInstances)
            testInstance.deregisterFrom(testLb);
        
        testLb.registerInstances(testInstances);
        
        //get snapshot of the registered instances.
        List<Instance> results = new ArrayList<>(testLb.getBackendInstances());
        
        //restore the state before test.
        for(Instance testInstance : testInstances)
            testInstance.deregisterFrom(testLb);
        
        assertTrue(results.contains(testInstance1) && results.contains(testInstance2));
    }

    /**
     * Test of deregisterInstances method, of class LoadBalancerImpl.
     */
    @Test
    public void testDeregisterInstances() {
        
        System.out.println("deregisterInstances");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        Instance testInstance1 = testBackendInstances.get(testBackendInstanceIds.get(0));
        Instance testInstance2 = testBackendInstances.get(testBackendInstanceIds.get(1));
        List<Instance> testInstances = new ArrayList<>();
        testInstances.add(testInstance1);
        testInstances.add(testInstance2);
        
        //make sure test load balancer has test instances as backend.
        for(Instance testInstance : testInstances){
            testInstance.deregisterFrom(testLb);
            testInstance.registerWith(testLb);
        }
        
        testLb.deregisterInstances(testInstances);
        
        //get snapshot of the registered instances.
        List<Instance> results = new ArrayList<>(testLb.getBackendInstances());
        
        //restore the state before test if needed.
        for(Instance testInstance : testInstances)
            testInstance.deregisterFrom(testLb);
        
        assertTrue(!results.contains(testInstance1) && !results.contains(testInstance2));
    }

    /**
     * Test of registerInstance method, of class LoadBalancerImpl.
     */
    @Test
    public void testRegisterInstance() {
        
        System.out.println("registerInstance");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        Instance testInstance = testBackendInstances.get(testBackendInstanceIds.get(0));
        
        //make sure test load balancer does not have test instances as backend.
        testInstance.deregisterFrom(testLb);
        
        testLb.registerInstance(testInstance);
        
        //get snapshot of the registered instances.
        List<Instance> results = new ArrayList<>(testLb.getBackendInstances());
        
        //restore the state before test.
        testInstance.deregisterFrom(testLb);
        
        assertTrue(results.contains(testInstance));
    }

    /**
     * Test of deregisterInstance method, of class LoadBalancerImpl.
     */
    @Test
    public void testDeregisterInstance() {
        
        System.out.println("deregisterInstance");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        Instance testInstance = testBackendInstances.get(testBackendInstanceIds.get(0));
        
        //make sure test load balancer has test instances as backend.
        testInstance.deregisterFrom(testLb);
        testInstance.registerWith(testLb);
        
        testLb.deregisterInstance(testInstance);
        
        //get snapshot of the registered instances.
        List<Instance> results = new ArrayList<>(testLb.getBackendInstances());
        
        //restore the state before test if needed.
        testInstance.deregisterFrom(testLb);
        
        assertTrue(!results.contains(testInstance));
    }

    /**
     * Test of enableZones method, of class LoadBalancerImpl.
     */
    @Test
    public void testEnableZones() {
        
        System.out.println("enableZones");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        String testAddedZoneName1 = "";
        String testAddedZoneName2 = "";
        Zone testAddedZone1 = new ZoneImpl.Builder().name(testAddedZoneName1).build();
        Zone testAddedZone2 = new ZoneImpl.Builder().name(testAddedZoneName2).build();
        List<Zone> testAddedZones = new ArrayList<>();
        testAddedZones.add(testAddedZone1);
        testAddedZones.add(testAddedZone2);
        
        //make sure test zones are not enabled with test load balancer.
        testLb.disableZones(testAddedZones);
        if(testLb.getZones().contains(testAddedZone1) || testLb.getZones().contains(testAddedZone2))
            fail("test load balancer should not have Availability Zones for test enabled.");
        testLb.enableZones(testAddedZones);
        
        List<Zone> results = new ArrayList<>(testLb.getZones());
        System.out.println("---------------------------");
        System.out.println(testAddedZones);
        System.out.println(results);
        System.out.println("---------------------------");
        
        //restore the state before test.
        testLb.disableZones(testAddedZones);
        
        assertTrue(results.containsAll(testAddedZones));
    }

    /**
     * Test of disableZones method, of class LoadBalancerImpl.
     */
    @Test
    public void testDisableZones() {
        
        System.out.println("disableZones");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        String testAddedZoneName1 = "";
        String testAddedZoneName2 = "";
        Zone testAddedZone1 = new ZoneImpl.Builder().name(testAddedZoneName1).build();
        Zone testAddedZone2 = new ZoneImpl.Builder().name(testAddedZoneName2).build();
        List<Zone> testAddedZones = new ArrayList<>();
        testAddedZones.add(testAddedZone1);
        testAddedZones.add(testAddedZone2);
        
        //make sure three zones are enabled with test load balancer.
        testLb.enableZones(testAddedZones);
        if(testLb.getZones().size() < 3)
            fail("test load balancer should have more than two Availability Zones enabled.");
        
        //try disable zones.
        testLb.disableZones(testAddedZones);
        
        List<Zone> results = new ArrayList<>(testLb.getZones());
        
        //restore the state before test.
        if(testLb.getZones().contains(testAddedZone1))
                testLb.disableZone(testAddedZone1);
        if(testLb.getZones().contains(testAddedZone2))
                testLb.disableZone(testAddedZone2);
        
        assertTrue(!results.contains(testAddedZone1) && !results.contains(testAddedZone2));
    }

    /**
     * Test of enableZone method, of class LoadBalancerImpl.
     */
    @Test
    public void testEnableZone() {
        
        System.out.println("enableZone");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        String testAddedZoneName = "";
        Zone testAddedZone = new ZoneImpl.Builder().name(testAddedZoneName).build();
        
        //make sure test zones are not enabled with test load balancer.
        testLb.disableZone(testAddedZone);
        if(testLb.getZones().contains(testAddedZone))
            fail("test load balancer should not have Availability Zones for test enabled.");
        
        testLb.enableZone(testAddedZone);
        
        List<Zone> results = new ArrayList<>(testLb.getZones());
        
        //restore the state before test.
        testLb.disableZone(testAddedZone);
        
        assertTrue(results.contains(testAddedZone));
    }

    /**
     * Test of disableZone method, of class LoadBalancerImpl.
     */
    @Test
    public void testDisableZone() {
        
        System.out.println("disableZone");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        String testAddedZoneName = "";
        Zone testAddedZone = new ZoneImpl.Builder().name(testAddedZoneName).build();
        
        //make sure two zones are enabled with test load balancer.
        testLb.enableZone(testAddedZone);
        if(testLb.getZones().size() < 2)
            fail("test load balancer should have more than two Availability Zones enabled.");
        
        //try disable zones.
        testLb.disableZone(testAddedZone);
        
        List<Zone> results = new ArrayList<>(testLb.getZones());
        
        //restore the state before test.
        if(testLb.getZones().contains(testAddedZone))
            testLb.disableZone(testAddedZone);
        
        assertTrue(!results.contains(testAddedZone));
    }

    /**
     * Test of delete method, of class LoadBalancerImpl.
     */
    @Test
    public void testDelete() {
        
        System.out.println("delete");
        fail("The test case is a prototype.");
    }

    /**
     * Test of isDestroyed method, of class LoadBalancerImpl.
     */
    @Test
    public void testIsDestroyed() {
        
        System.out.println("isDestroyed");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getExistLoadBalancerByName method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetExistLoadBalancerByName() {
        
        System.out.println("getExistLoadBalancerByName");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getExistLoadBalancers method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetExistLoadBalancers() {
        
        System.out.println("getExistLoadBalancers");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetName() {
        
        System.out.println("getName");
       fail("The test case is a prototype.");
    }

    /**
     * Test of getListeners method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetListeners() {
        
        System.out.println("getListeners");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        
        AWSELB elb = (AWSELB)AWS.get(AWS.BlockName.ELB);
        List<ListenerDescription> descs = elb.describeLoadBalancers(testLb.getName()).getLoadBalancerDescriptions().get(0).getListenerDescriptions();
        List<LoadBalancerListener> expectedList = new ArrayList<>();
        for(ListenerDescription desc : descs)
            expectedList.add(new LoadBalancerListenerImpl.Builder().description(desc).build());

        List<LoadBalancerListener> actualList = testLb.getListeners();
        
        assertEquals(expectedList, actualList);
    }

    /**
     * Test of getZones method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetZones() {
        
        System.out.println("getZones");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        AWSELB elb = (AWSELB)AWS.get(AWS.BlockName.ELB);
        List<String> expectedZoneNames = elb.describeLoadBalancers(testLb.getName()).getLoadBalancerDescriptions().get(0).getAvailabilityZones();
        
        List<Zone> expectedZones = new ArrayList<>();
        for(String expectedZoneName : expectedZoneNames)
            expectedZones.add(new ZoneImpl.Builder().name(expectedZoneName).build());

        assertEquals(expectedZones, testLb.getZones());
    }

    /**
     * Test of getSubnets method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetSubnets() {
        
        System.out.println("getSubnets");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        
        AWSELB elb = (AWSELB)AWS.get(AWS.BlockName.ELB);
        List<String> expectedSubnetIds = elb.describeLoadBalancers(testLb.getName()).getLoadBalancerDescriptions().get(0).getSubnets();
        
        List<Subnet> expectedSubnets = new ArrayList<>();
        for(String expectedSubnetId : expectedSubnetIds){
            expectedSubnets.add(new SubnetImpl.Builder().id(expectedSubnetId).get());
        }
        
        assertEquals(expectedSubnets, testLb.getSubnets());
    }

    /**
     * Test of getBackendInstances method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetBackendInstances() {
        
        System.out.println("getBackendInstances");
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        Instance testInstance1 = testBackendInstances.get(testBackendInstanceIds.get(0));
        Instance testInstance2 = testBackendInstances.get(testBackendInstanceIds.get(1));
        List<Instance> testInstances = new ArrayList<>();
        testInstances.add(testInstance1);
        testInstances.add(testInstance2);
        
        //deregister all the registered instances.
        testLb.deregisterInstances(testLb.getBackendInstances());
        if(!testLb.getBackendInstances().isEmpty())
            fail("test load balancer  shold not have any registered backend instance.");
        
        testLb.registerInstances(testInstances);
        
        List<Instance> results = new ArrayList<>(testLb.getBackendInstances());
        
        //restore the state before.
        testLb.deregisterInstances(testInstances);
        
        //results should contain only test instances.
        assertEquals(testInstances, results);
    }

    /**
     * Test of getInstanceStates method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetInstanceStates_0args() {
        
        System.out.println("getInstanceStates");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInstanceStates method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetInstanceStates_List() {
        
        System.out.println("getInstanceStates");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInstanceStatesByInstanceId method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetInstanceStatesByInstanceId() {
        
        System.out.println("getInstanceStatesByInstanceId");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInstanceState method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetInstanceState_String() {
        
        System.out.println("getInstanceState");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInstanceState method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetInstanceState_Instance() {
        
        System.out.println("getInstanceState");
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllLoadBalancerDescriptions method, of class LoadBalancerImpl.
     */
    @Test
    public void testGetAllLoadBalancerDescriptions() {
        
        System.out.println("getAllLoadBalancerDescriptions");
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class LoadBalancerImpl.
     */
    @Test
    public void testEquals() {
        
        System.out.println("equals");
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class LoadBalancerImpl.
     */
    @Test
    public void testHashCode() {
        
        System.out.println("hashCode");
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class LoadBalancerImpl.
     */
    @Test
    public void testToString() {
        
        System.out.println("toString");
        fail("The test case is a prototype.");
    }
    
}
