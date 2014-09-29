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
import web.component.api.model.LoadBalancer;
import web.component.api.model.LoadBalancerListener;

/** 
 * 
 * @author Hiroshi 
 */ 
public class LoadBalancerListenerImplTest { 

    private static LoadBalancerListener testInstance;
    
    private static final Map<String, LoadBalancer> testLbs = new HashMap<>();
    private static List<String> testLbNames;
    
    private static final String testZoneName = ""; 
    private static final String testServiceProtocol = "HTTP"; 
    private static final String testInstanceProtocol = "HTTP"; 
    
    private static final int testServicePort = 80; 
    private static final int testInstancePort = 80; 
    
    private static final String testCertificateId = null;
    
    public LoadBalancerListenerImplTest() { 
    } 
    
    @BeforeClass 
    public static void setUpClass() { 
        
        //test instance without any load balancer attached.
        testInstance = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .instanceProtocol(testInstanceProtocol)
                .servicePort(testServicePort)
                .serviceProtocol(testServiceProtocol)
                .certificateId(testCertificateId)
                .build();
        
        getExistTestLbs();
        //createTestLbs();

        
    }
    
    static private void createTestLbs(){
        
        String[] newLbNames = {"test-lb-1","test-lb-2"};
        testLbNames = Arrays.asList(newLbNames);
        
        LoadBalancerListener testListener = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .instanceProtocol(testInstanceProtocol)
                .servicePort(testServicePort)
                .serviceProtocol(testServiceProtocol)
                .build();
        
        for(String testLbName : testLbNames){
            LoadBalancer testLb = new LoadBalancerImpl.Builder(testLbName).listener(testListener).zone(testZoneName).build();
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

        String[] newLbNames = {"test-lb"};
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
    } 
    
    @Before 
    public void setUp() { 
    } 
    
    @After 
    public void tearDown() { 
    } 

    @Test 
    public void testSetInstancePort() {
        
        System.out.println("setInstancePort"); 
        
        int instancePort = 0; 
        
        try{ 
            testInstance.setInstancePort(instancePort); 
        }catch(UnsupportedOperationException e){ 
            return; 
        } 
        fail(); 
    } 

    /** 
     * Test of setServicePort method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testSetServicePort() { 
        System.out.println("setServicePort");
        
        int servciePort = 0; 
        
        try{ 
            testInstance.setServicePort(servciePort); 
        }catch(UnsupportedOperationException e){ 
            return; 
        } 
        fail(); 
    } 

    /** 
     * Test of setInstanceProtocol method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testSetInstanceProtocol() { 

        System.out.println("setInstanceProtocol"); 
        
        String instanceProtocol = "HTTP"; 
        
        try{ 
            testInstance.setInstanceProtocol(instanceProtocol); 
        }catch(UnsupportedOperationException e){ 
            return; 
        }
        fail();
    } 

    /** 
     * Test of setServiceProtocol method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testSetServiceProtocol() {
        
        System.out.println("setServiceProtocol"); 
        
        String serviceProtocol = "HTTP"; 
        
        try{ 
            testInstance.setServiceProtocol(serviceProtocol); 
        }catch(UnsupportedOperationException e){ 
            return; 
        }
        fail();
    } 

    /** 
     * Test of setServerCertificate method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testSetServerCertificate() { 

        System.out.println("setServerCertificate"); 
        
        String certId = "test-cert-id";
        
        try{
            testInstance.setServerCertificate(certId);
        }catch(UnsupportedOperationException e){
            return;
        }
        fail();
    } 


    /** 
     * Test of getInstancePort method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testGetInstancePort() { 
        System.out.println("getInstancePort"); 
        
        int port = testInstance.getInstancePort(); 
        assertEquals(testInstancePort, port); 
    } 

    /** 
     * Test of getServicePort method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testGetServicePort() { 
        System.out.println("getServicePort"); 
        
        int port = testInstance.getServicePort(); 
        assertEquals(testServicePort, port); 
    } 

    /** 
     * Test of getInstanceProtocol method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testGetInstanceProtocol() { 
        
        System.out.println("getInstanceProtocol"); 
        
        String protocol = testInstance.getInstanceProtocol(); 
        assertTrue(testInstanceProtocol.equalsIgnoreCase(protocol));
    } 

    /** 
     * Test of getServiceProtocol method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testGetServiceProtocol() { 
        
        System.out.println("getServiceProtocol"); 
        
        String protocol = testInstance.getServiceProtocol(); 
        assertTrue(testServiceProtocol.equalsIgnoreCase(protocol));
    } 


    /** 
     * Test of getServerCertificate method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testGetServerCertificate() { 
        
        System.out.println("getServerCertificate");
        assertEquals("", testInstance.getServerCertificate());
    } 

    /** 
     * Test of addTo method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testAddTo() { 
        
        System.out.println("addTo"); 
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        LoadBalancerListener testListener = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .servicePort(8088)
                .instanceProtocol(testInstanceProtocol)
                .serviceProtocol(testServiceProtocol)
                .build();
        
        testListener.addTo(testLb);
        
        //get snapshot of the attached listeners.
        List<LoadBalancerListener> results = new ArrayList<>(testLb.getListeners());
        
        //restore the state before test.
        testLb.deleteListener(testListener);
        
        assertTrue(results.contains(testListener));
    } 

    /** 
     * Test of delete method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testDeleteFrom() { 
        
        System.out.println("deleteFrom"); 
        LoadBalancer testLb = testLbs.get(testLbNames.get(0));
        LoadBalancerListener testListener = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .servicePort(8088)
                .instanceProtocol(testInstanceProtocol)
                .serviceProtocol(testServiceProtocol)
                .build();
        testLb.createListener(testListener);
        if(!testLb.getListeners().contains(testListener))
            fail("could not create test listener to test load balancer.");
        
        //try delete.
        testListener.deleteFrom(testLb);
        
        //get snapshot of the attached listeners.
        List<LoadBalancerListener> results = new ArrayList<>(testLb.getListeners());
        
        //restore the state before test if needed.
        if(testLb.getListeners().contains(testListener))
            testLb.deleteListener(testListener);
        
        assertTrue(!results.contains(testListener));
    } 

    /** 
     * Test of equals method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testEquals() { 
        
        System.out.println("equals");
        
        LoadBalancerListener equalInstance = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .instanceProtocol(testInstanceProtocol)
                .servicePort(testServicePort)
                .serviceProtocol(testServiceProtocol)
                .build();
        LoadBalancerListener equalInstanceWithLowerCase = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .instanceProtocol("http")
                .servicePort(testServicePort)
                .serviceProtocol("http")
                .build();        
        LoadBalancerListener instanceWithDifferentInstancePort = new LoadBalancerListenerImpl.Builder()
                .instancePort(1024)
                .instanceProtocol(testInstanceProtocol)
                .servicePort(testServicePort)
                .serviceProtocol(testServiceProtocol)
                .build();
        LoadBalancerListener instanceWithDifferentInstanceProtocol = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .instanceProtocol("TCP")
                .servicePort(testServicePort)
                .serviceProtocol(testServiceProtocol)
                .build();
        LoadBalancerListener instanceWithDifferentServicePort = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .instanceProtocol(testInstanceProtocol)
                .servicePort(1024)
                .serviceProtocol(testServiceProtocol)
                .build();
        LoadBalancerListener instanceWithDifferentServiceProtocol = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .instanceProtocol(testInstanceProtocol)
                .servicePort(testServicePort)
                .serviceProtocol("TCP")
                .build();
        
        assertTrue(testInstance.equals(equalInstance));
        assertTrue(testInstance.equals(equalInstanceWithLowerCase));
        assertFalse(testInstance.equals(instanceWithDifferentInstancePort));
        assertFalse(testInstance.equals(instanceWithDifferentInstanceProtocol));
        assertFalse(testInstance.equals(instanceWithDifferentServicePort));
        assertFalse(testInstance.equals(instanceWithDifferentServiceProtocol));
    } 

    /** 
     * Test of hashCode method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testHashCode() { 
        
        System.out.println("hashCode"); 
        
        LoadBalancerListener equalInstance = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .instanceProtocol(testInstanceProtocol)
                .servicePort(testServicePort)
                .serviceProtocol(testServiceProtocol)
                .build();
        LoadBalancerListener equalInstanceWithLowerCase = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .instanceProtocol("http")
                .servicePort(testServicePort)
                .serviceProtocol("http")
                .build();        
        LoadBalancerListener instanceWithDifferentInstancePort = new LoadBalancerListenerImpl.Builder()
                .instancePort(1024)
                .instanceProtocol(testInstanceProtocol)
                .servicePort(testServicePort)
                .serviceProtocol(testServiceProtocol)
                .build();
        
        assertTrue(testInstance.hashCode() == equalInstance.hashCode());
        assertTrue(testInstance.hashCode() == equalInstanceWithLowerCase.hashCode());
        assertTrue(testInstance.hashCode() != instanceWithDifferentInstancePort.hashCode());
    } 
} 
