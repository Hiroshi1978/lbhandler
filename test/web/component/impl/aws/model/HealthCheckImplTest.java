/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
        HealthCheckImpl instance = null;
        HealthCheck expResult = null;
        HealthCheck result = instance.asElbHealthCheck();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHealthyThreshold method, of class HealthCheckImpl.
     */
    @Test
    public void testGetHealthyThreshold() {
        System.out.println("getHealthyThreshold");
        HealthCheckImpl instance = null;
        int expResult = 0;
        int result = instance.getHealthyThreshold();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInterval method, of class HealthCheckImpl.
     */
    @Test
    public void testGetInterval() {
        System.out.println("getInterval");
        HealthCheckImpl instance = null;
        int expResult = 0;
        int result = instance.getInterval();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTarget method, of class HealthCheckImpl.
     */
    @Test
    public void testGetTarget() {
        System.out.println("getTarget");
        HealthCheckImpl instance = null;
        String expResult = "";
        String result = instance.getTarget();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTimeout method, of class HealthCheckImpl.
     */
    @Test
    public void testGetTimeout() {
        System.out.println("getTimeout");
        HealthCheckImpl instance = null;
        int expResult = 0;
        int result = instance.getTimeout();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnhealthyThreshold method, of class HealthCheckImpl.
     */
    @Test
    public void testGetUnhealthyThreshold() {
        System.out.println("getUnhealthyThreshold");
        HealthCheckImpl instance = null;
        int expResult = 0;
        int result = instance.getUnhealthyThreshold();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setHealthyThreshold method, of class HealthCheckImpl.
     */
    @Test
    public void testSetHealthyThreshold() {
        System.out.println("setHealthyThreshold");
        int healthyThreshold = 0;
        HealthCheckImpl instance = null;
        instance.setHealthyThreshold(healthyThreshold);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setInterval method, of class HealthCheckImpl.
     */
    @Test
    public void testSetInterval() {
        System.out.println("setInterval");
        int interval = 0;
        HealthCheckImpl instance = null;
        instance.setInterval(interval);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTarget method, of class HealthCheckImpl.
     */
    @Test
    public void testSetTarget() {
        System.out.println("setTarget");
        String target = "";
        HealthCheckImpl instance = null;
        instance.setTarget(target);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTimeout method, of class HealthCheckImpl.
     */
    @Test
    public void testSetTimeout() {
        System.out.println("setTimeout");
        int timeout = 0;
        HealthCheckImpl instance = null;
        instance.setTimeout(timeout);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setUnhealthyThreshold method, of class HealthCheckImpl.
     */
    @Test
    public void testSetUnhealthyThreshold() {
        System.out.println("setUnhealthyThreshold");
        int unhealthyThreshold = 0;
        HealthCheckImpl instance = null;
        instance.setUnhealthyThreshold(unhealthyThreshold);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class HealthCheckImpl.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        HealthCheckImpl instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class HealthCheckImpl.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        HealthCheckImpl instance = null;
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class HealthCheckImpl.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        HealthCheckImpl instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
