/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import web.component.api.model.HealthCheck;

/**
 *
 * @author Hiroshi
 */
public class HealthCheckImplTest {
    
    public HealthCheckImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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
     * Test of asElbHealthCheck method, of class HealthCheckImpl.
     */
    @Test
    public void testAsElbHealthCheck() {
        
        System.out.println("asElbHealthCheck");
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        
        com.amazonaws.services.elasticloadbalancing.model.HealthCheck elbHealthCheck
                = ((HealthCheckImpl)hc).asElbHealthCheck();
        
        assertEquals(hc.getHealthyThreshold(),(int)elbHealthCheck.getHealthyThreshold());
        assertEquals(hc.getInterval(),(int)elbHealthCheck.getInterval());
        assertEquals(hc.getTarget(),elbHealthCheck.getTarget());
        assertEquals(hc.getTimeout(),(int)elbHealthCheck.getTimeout());
        assertEquals(hc.getUnhealthyThreshold(),(int)elbHealthCheck.getUnhealthyThreshold());
        
        com.amazonaws.services.elasticloadbalancing.model.HealthCheck elbHealthCheck2
                = ((HealthCheckImpl)hc).asElbHealthCheck();
        
        assertEquals(elbHealthCheck2,elbHealthCheck);
        assertFalse(elbHealthCheck2 == elbHealthCheck);
    }

    /**
     * Test of getHealthyThreshold method, of class HealthCheckImpl.
     */
    @Test
    public void testGetHealthyThreshold() {
        
        System.out.println("getHealthyThreshold");
        int testHealthThresHold = 10;
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(testHealthThresHold)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        
        assertEquals(testHealthThresHold, hc.getHealthyThreshold());
    }

    /**
     * Test of getInterval method, of class HealthCheckImpl.
     */
    @Test
    public void testGetInterval() {
        
        System.out.println("getInterval");
        int testInterval = 30;
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(testInterval)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        
        assertEquals(testInterval, hc.getInterval());
    }
    
    /**
     * Test of getTarget method, of class HealthCheckImpl.
     */
    @Test
    public void testGetTarget() {

        System.out.println("getTarget");
        String testTarget = "http:80/index.html";
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target(testTarget)
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        
        assertEquals(testTarget, hc.getTarget());
    }

    /**
     * Test of getTimeout method, of class HealthCheckImpl.
     */
    @Test
    public void testGetTimeout() {
        
        System.out.println("getTimeout");
        int testTimeout = 30;
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(testTimeout)
                            .unhealthyThreshold(2)
                            .build();
        
        assertEquals(testTimeout, hc.getTimeout());
    }

    /**
     * Test of getUnhealthyThreshold method, of class HealthCheckImpl.
     */
    @Test
    public void testGetUnhealthyThreshold() {
        
        System.out.println("getUnhealthyThreshold");
        int testUNhealthyThreshold = 2;
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(testUNhealthyThreshold)
                            .build();
        
        assertEquals(testUNhealthyThreshold, hc.getUnhealthyThreshold());
    }

    /**
     * Test of setHealthyThreshold method, of class HealthCheckImpl.
     */
    @Test
    public void testSetHealthyThreshold() {
        
        System.out.println("setHealthyThreshold");
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        
        int testHealthyThreshold = 8;
        hc.setHealthyThreshold(testHealthyThreshold);
        assertEquals(testHealthyThreshold, hc.getHealthyThreshold());
    }

    /**
     * Test of setInterval method, of class HealthCheckImpl.
     */
    @Test
    public void testSetInterval() {
        
        System.out.println("setInterval");
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        
        int testInterval = 40;
        hc.setInterval(testInterval);
        assertEquals(testInterval, hc.getInterval());
    }

    /**
     * Test of setTarget method, of class HealthCheckImpl.
     */
    @Test
    public void testSetTarget() {
        
        System.out.println("setTarget");
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        
        String testTarget = "https:8088/index2.html";
        hc.setTarget(testTarget);
        assertEquals(testTarget, hc.getTarget());
    }

    /**
     * Test of setTimeout method, of class HealthCheckImpl.
     */
    @Test
    public void testSetTimeout() {
        
        System.out.println("setTimeout");
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        
        int testTimeout = 40;
        hc.setTimeout(testTimeout);
        assertEquals(testTimeout, hc.getTimeout());
    }

    /**
     * Test of setUnhealthyThreshold method, of class HealthCheckImpl.
     */
    @Test
    public void testSetUnhealthyThreshold() {
        
        System.out.println("setUnhealthyThreshold");
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        
        int testUnhealthyThreshold = 4;
        hc.setUnhealthyThreshold(testUnhealthyThreshold);
        assertEquals(testUnhealthyThreshold, hc.getUnhealthyThreshold());
    }

    /**
     * Test of toString method, of class HealthCheckImpl.
     */
    @Test
    public void testToString() {
        
        System.out.println("toString");
        HealthCheck hc = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        
        com.amazonaws.services.elasticloadbalancing.model.HealthCheck elbHealthCheck =
                ((HealthCheckImpl)hc).asElbHealthCheck();
        
        assertEquals(elbHealthCheck.toString(), hc.toString());
    }

    /**
     * Test of equals method, of class HealthCheckImpl.
     */
    @Test
    public void testEquals() {
        
        System.out.println("equals");
        HealthCheck hc1 = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        HealthCheck hc2 = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        HealthCheck hc3 = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(1)
                            .build();
        
        assertTrue(hc1.equals(hc1));
        assertTrue(hc1.equals(hc2));
        assertFalse(hc1.equals(hc3));
        assertTrue(hc2.equals(hc1));
        assertTrue(hc2.equals(hc2));
        assertFalse(hc2.equals(hc3));
        assertFalse(hc3.equals(hc1));
        assertFalse(hc3.equals(hc2));
        assertTrue(hc3.equals(hc3));
    }

    /**
     * Test of hashCode method, of class HealthCheckImpl.
     */
    @Test
    public void testHashCode() {
        
        System.out.println("hashCode");
        HealthCheck hc1 = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        HealthCheck hc2 = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(2)
                            .build();
        HealthCheck hc3 = new HealthCheckImpl.Builder()
                            .healthyThreshold(10)
                            .interval(30)
                            .target("http:80/index.html")
                            .timeout(30)
                            .unhealthyThreshold(1)
                            .build();

        assertTrue(hc1.hashCode() == hc1.hashCode());
        assertTrue(hc1.hashCode() == hc2.hashCode());
        assertTrue(hc1.hashCode() != hc3.hashCode());
        assertTrue(hc2.hashCode() == hc1.hashCode());
        assertTrue(hc2.hashCode() == hc2.hashCode());
        assertTrue(hc2.hashCode() != hc3.hashCode());
        assertTrue(hc3.hashCode() != hc1.hashCode());
        assertTrue(hc3.hashCode() != hc2.hashCode());
        assertTrue(hc3.hashCode() == hc3.hashCode());
    }
    
}
