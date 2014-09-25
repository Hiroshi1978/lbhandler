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
import web.component.api.model.LoadBalancer;
import web.component.api.model.LoadBalancerListener;

/** 
 * 
 * @author Hiroshi 
 */ 
public class LoadBalancerListenerImplTest { 
    
    private static final String testLbName1 = "lb-for-test-001"; 
    private static final String testLbName2 = "lb-for-test-002"; 
    private static final String testZoneName = ""; 
    private static final String testServiceProtocol = "HTTP"; 
    private static final String testInstanceProtocol = "HTTP"; 
    private static final String testSubnetId = "";
    private static final String testInstanceId1 = "";
    private static final String testInstanceId2 = "";
    
    private static final int testServicePort = 80; 
    private static final int testInstancePort = 80; 
    
    private static final List<LoadBalancer> testLBs = new ArrayList<>();

    public LoadBalancerListenerImplTest() { 
    } 
    
    @BeforeClass 
    public static void setUpClass() { 
        
        System.out.println("Create load balancer for tests.");
        LoadBalancerListener testListener = new LoadBalancerListenerImpl.Builder()
                .instancePort(testInstancePort)
                .instanceProtocol(testInstanceProtocol)
                .servicePort(testServicePort)
                .serviceProtocol(testServiceProtocol)
                .build();
        
        LoadBalancer testLb1 = new LoadBalancerImpl.Builder(testLbName1) 
                .listener(testListener) 
                .zone(testZoneName)
                .build(); 
        
        LoadBalancer testLb2 = new LoadBalancerImpl.Builder(testLbName2) 
                .listener(testListener) 
                .zone(testZoneName)
                .build();
        
        testLBs.add(testLb1);
        testLBs.add(testLb2);
        
        while(true){
            
            boolean isAllStarted = true;
            for(LoadBalancer testLb : testLBs){
                if(!testLb.isStarted())
                    isAllStarted = false;
            }
            
            if(isAllStarted)
                break;
            
            System.out.println("wait for test load balancers to be ready ..."); 
            try { 
                Thread.sleep(10000); 
            } catch (InterruptedException ex) { 
                //Re-throw exception and stop the test. 
                throw new RuntimeException(ex); 
            } 
        }

        System.out.println("--------------------------------------------"); 
        for(LoadBalancer testLb : testLBs){
            System.out.println( testLb.getName() + " is ready for the test."); 
            System.out.println("Load balancer name       : " + testLb.getName());
            for(int i=0; i<testLb.getListeners().size(); i++)
                System.out.println("Load balancer listener " + (i+1) + " : " + testLb.getListeners().get(i)); 
            for(int i=0; i<testLb.getZones().size(); i++)
                System.out.println("Enabled zones          " + (i+1) + " : " + testLb.getZones().get(i));
            System.out.println("--------------------------------------------"); 
        }
    } 
    
    @AfterClass 
    public static void tearDownClass() { 
        System.out.println("Delete load balancer for tests.");
        
        for(LoadBalancer testLb : testLBs)
            //testLb.delete();

        System.out.println(); 
        System.out.println("--------------------------------------------"); 
        for(LoadBalancer testLb : testLBs){
            System.out.println("Load balancer name       : " + testLb.getName());
            for(int i=0; i<testLb.getListeners().size(); i++)
                System.out.println("Load balancer listener " + (i+1) + " : " + testLb.getListeners().get(i)); 
            for(int i=0; i<testLb.getZones().size(); i++)
                System.out.println("Enabled zones          " + (i+1) + " : " + testLb.getZones().get(i));
            System.out.println("--------------------------------------------"); 
        }
    } 
    
    @Before 
    public void setUp() { 
        
        System.out.println(); 
        System.out.println("--------------------------------------------"); 
        for(LoadBalancer testLb : testLBs){
            if(testLb.isStarted() && !testLb.isDestroyed()){
                System.out.println("Load balancer name       : " + testLb.getName());
                for(int i=0; i<testLb.getListeners().size(); i++)
                    System.out.println("Load balancer listener " + (i+1) + " : " + testLb.getListeners().get(i)); 
                for(int i=0; i<testLb.getZones().size(); i++)
                    System.out.println("Enabled zones          " + (i+1) + " : " + testLb.getZones().get(i));
                System.out.println("--------------------------------------------"); 
            }else{
                throw new RuntimeException(testLb.getName() + " is not ready. Stop tests.");
            }
        }
    } 
    
    @After 
    public void tearDown() { 
    } 

    /** 
     * Test of create method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testCreate_ListenerDescription() { 
        System.out.println("create"); 
    } 

    /** 
     * Test of create method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testCreate_0args() { 
        System.out.println("create"); 
    } 

    @Test 
    public void testSetInstancePort() { 
        System.out.println("setInstancePort"); 
        int instancePort = 0; 
        LoadBalancer testLb = testLBs.get(0);
        LoadBalancerListenerImpl instance = (LoadBalancerListenerImpl)testLb.getListeners().get(0); 
        try{ 
            instance.setInstancePort(instancePort); 
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
        LoadBalancer testLb = testLBs.get(0);
        LoadBalancerListenerImpl instance = (LoadBalancerListenerImpl)testLb.getListeners().get(0); 
        try{ 
            instance.setServicePort(servciePort); 
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
        LoadBalancer testLb = testLBs.get(0);
        LoadBalancerListenerImpl instance = (LoadBalancerListenerImpl)testLb.getListeners().get(0); 
        int port = instance.getInstancePort(); 
        assertEquals(testInstancePort, port); 
    } 

    /** 
     * Test of getServicePort method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testGetServicePort() { 
        System.out.println("getServicePort"); 
        LoadBalancer testLb = testLBs.get(0);
        LoadBalancerListenerImpl instance = (LoadBalancerListenerImpl)testLb.getListeners().get(0); 
        int port = instance.getServicePort(); 
        assertEquals(testServicePort, port); 
    } 

    /** 
     * Test of setInstanceProtocol method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testSetInstanceProtocol() { 
        System.out.println("setInstanceProtocol"); 
        String instanceProtocol = "HTTP"; 
        LoadBalancer testLb = testLBs.get(0);
        LoadBalancerListenerImpl instance = (LoadBalancerListenerImpl)testLb.getListeners().get(0); 
        try{ 
            instance.setInstanceProtocol(instanceProtocol); 
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
        LoadBalancer testLb = testLBs.get(0);
        LoadBalancerListenerImpl instance = (LoadBalancerListenerImpl)testLb.getListeners().get(0); 
        try{ 
            instance.setServiceProtocol(serviceProtocol); 
        }catch(UnsupportedOperationException e){ 
            return; 
        }
        fail();
    } 

    /** 
     * Test of getInstanceProtocol method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testGetInstanceProtocol() { 
        System.out.println("getInstanceProtocol"); 
        LoadBalancer testLb = testLBs.get(0);
        LoadBalancerListenerImpl instance = (LoadBalancerListenerImpl)testLb.getListeners().get(0); 
        String protocol = instance.getInstanceProtocol(); 
        assertTrue(testInstanceProtocol.equalsIgnoreCase(protocol));
    } 

    /** 
     * Test of getServiceProtocol method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testGetServiceProtocol() { 
        System.out.println("getServiceProtocol"); 
        LoadBalancer testLb = testLBs.get(0);
        LoadBalancerListenerImpl instance = (LoadBalancerListenerImpl)testLb.getListeners().get(0); 
        String protocol = instance.getServiceProtocol(); 
        assertTrue(testServiceProtocol.equalsIgnoreCase(protocol));
    } 

    /** 
     * Test of setServerCertificate method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testSetServerCertificate() { 
        System.out.println("setServerCertificate"); 
        String certId = "test-cert-id";
        LoadBalancer testLb = testLBs.get(0);
        LoadBalancerListenerImpl instance = (LoadBalancerListenerImpl)testLb.getListeners().get(0);
        
        try{
            instance.setServerCertificate(certId);
        }catch(UnsupportedOperationException e){
            return;
        }
        fail();
    } 

    /** 
     * Test of getServerCertificate method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testGetServerCertificate() { 
        System.out.println("getServerCertificate"); 
    } 

    /** 
     * Test of getLoadBalancer method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testGetLoadBalancer() { 
        System.out.println("getLoadBalancer"); 
        LoadBalancer testLb = testLBs.get(0);
        LoadBalancerListenerImpl instance = (LoadBalancerListenerImpl)testLb.getListeners().get(0);
        
        try{
            instance.getLoadBalancer();
        }catch(UnsupportedOperationException e){
            return;
        }
        fail();
    } 

    /** 
     * Test of addTo method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testAddTo() { 
        System.out.println("addTo"); 
    } 

    /** 
     * Test of delete method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testDelete() { 
        System.out.println("delete"); 
    } 

    /** 
     * Test of equals method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testEquals() { 
        System.out.println("equals");
        LoadBalancer testLb = testLBs.get(0);
        LoadBalancerListenerImpl instance1 = (LoadBalancerListenerImpl)testLb.getListeners().get(0);
        LoadBalancerListenerImpl instance2 = (LoadBalancerListenerImpl)testLb.getListeners().get(0);
        assertEquals(instance1,instance2);
    } 

    /** 
     * Test of hashCode method, of class LoadBalancerListenerImpl. 
     */ 
    @Test 
    public void testHashCode() { 
        System.out.println("hashCode"); 
        LoadBalancer testLb1 = testLBs.get(0);
        LoadBalancer testLb2 = testLBs.get(0);
        LoadBalancerListenerImpl instance1_1 = (LoadBalancerListenerImpl)testLb1.getListeners().get(0);
        LoadBalancerListenerImpl instance1_2 = (LoadBalancerListenerImpl)testLb1.getListeners().get(0);
        LoadBalancerListenerImpl instance2_1 = (LoadBalancerListenerImpl)testLb2.getListeners().get(0);
        LoadBalancerListenerImpl instance2_2 = (LoadBalancerListenerImpl)testLb2.getListeners().get(0);
        LoadBalancerListenerImpl instance3   = (LoadBalancerListenerImpl)new LoadBalancerListenerImpl.Builder().instancePort(81).build();
        LoadBalancerListenerImpl instance4   = (LoadBalancerListenerImpl)new LoadBalancerListenerImpl.Builder().servicePort(81).build();
        LoadBalancerListenerImpl instance5   = (LoadBalancerListenerImpl)new LoadBalancerListenerImpl.Builder().instanceProtocol("TCP").build();
        LoadBalancerListenerImpl instance6   = (LoadBalancerListenerImpl)new LoadBalancerListenerImpl.Builder().serviceProtocol("TCP").build();
        LoadBalancerListenerImpl instance7   = (LoadBalancerListenerImpl)new LoadBalancerListenerImpl.Builder().instanceProtocol("http").build();
        LoadBalancerListenerImpl instance8   = (LoadBalancerListenerImpl)new LoadBalancerListenerImpl.Builder().serviceProtocol("http").build();
        assertTrue(instance1_1.hashCode() == instance1_1.hashCode());
        assertTrue(instance1_1.hashCode() == instance1_2.hashCode());
        assertTrue(instance1_1.hashCode() == instance2_1.hashCode());
        assertTrue(instance1_1.hashCode() == instance2_2.hashCode());
        assertTrue(instance1_1.hashCode() != instance3.hashCode());
        assertTrue(instance1_1.hashCode() != instance4.hashCode());
        assertTrue(instance1_1.hashCode() != instance5.hashCode());
        assertTrue(instance1_1.hashCode() != instance6.hashCode());
        assertTrue("instancePort HTTP and http",instance1_1.hashCode() == instance7.hashCode());
        assertTrue("servicePort HTTP and http",instance1_1.hashCode() == instance8.hashCode());
    } 
    
} 
