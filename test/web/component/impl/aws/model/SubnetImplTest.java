/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import web.component.api.model.Subnet;
import web.component.api.model.VPC;

/**
 *
 * @author Hiroshi
 */
public class SubnetImplTest {

    static final String expectedCidrBlock = "10.1.1.0/24";
    static final String expectedTenancy = "";
    static final String expectedState = "";
    static final String expectedDhcpOptionsId = "";
    static VPC testVPC;
    static String expectedStringExpression;
    static Set<Subnet> testInstances = new HashSet<>();
    
    public SubnetImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        System.out.println("create test vpc.");
        testVPC = new VPCImpl.Builder().cidr("10.1.0.0/16").tenancy(expectedTenancy).create();
        
        while(!testVPC.getState().equals("available")){
            System.out.println("wait for test vpc to be ready ...");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        System.out.println("test vpc is now available.");
        
        System.out.println("create test subnets");
        Subnet testInstance = new SubnetImpl.Builder().cidrBlock(expectedCidrBlock).vpcId(testVPC.getId()).create();
        while(!testInstance.getState().equals("available")){
            System.out.println("wait for test subnet to be ready ...");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        testInstances.add(testInstance);
        System.out.println("test vpc is now available.");
    }
    
    @AfterClass
    public static void tearDownClass() {
        
        for(Subnet toDelete : testInstances){
            System.out.println("Delete test subnet [" + toDelete.toString() + "]");
            toDelete.delete();
        }
        
        for(Subnet toDelete : testInstances){
            while(toDelete.getState().equals("available")){
                System.out.println("wait for test subnet to be deleted ...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                }
            }
        }
        
        System.out.println("Delete test vpc [" + testVPC.toString() + "]");
        testVPC.delete();
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of asEc2Subnet method, of class SubnetImpl.
     */
    @Test
    public void testAsEc2Subnet() {
        System.out.println("asEc2Subnet");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getId method, of class SubnetImpl.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        SubnetImpl instance = null;
        String expResult = "";
        String result = instance.getId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class SubnetImpl.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object toBeCompared = null;
        SubnetImpl instance = null;
        boolean expResult = false;
        boolean result = instance.equals(toBeCompared);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class SubnetImpl.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        SubnetImpl instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class SubnetImpl.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        SubnetImpl instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getZone method, of class SubnetImpl.
     */
    @Test
    public void testGetZone() {
        System.out.println("getZone");
        SubnetImpl instance = null;
        String expResult = "";
        String result = instance.getZone();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAvailableIpAddressCount method, of class SubnetImpl.
     */
    @Test
    public void testGetAvailableIpAddressCount() {
        System.out.println("getAvailableIpAddressCount");
        SubnetImpl instance = null;
        Integer expResult = null;
        Integer result = instance.getAvailableIpAddressCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCidrBlock method, of class SubnetImpl.
     */
    @Test
    public void testGetCidrBlock() {
        System.out.println("getCidrBlock");
        SubnetImpl instance = null;
        String expResult = "";
        String result = instance.getCidrBlock();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDefaultForAz method, of class SubnetImpl.
     */
    @Test
    public void testGetDefaultForAz() {
        System.out.println("getDefaultForAz");
        SubnetImpl instance = null;
        boolean expResult = false;
        boolean result = instance.getDefaultForAz();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMapPublicIpOnLaunch method, of class SubnetImpl.
     */
    @Test
    public void testGetMapPublicIpOnLaunch() {
        System.out.println("getMapPublicIpOnLaunch");
        SubnetImpl instance = null;
        boolean expResult = false;
        boolean result = instance.getMapPublicIpOnLaunch();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getState method, of class SubnetImpl.
     */
    @Test
    public void testGetState() {
        System.out.println("getState");
        SubnetImpl instance = null;
        String expResult = "";
        String result = instance.getState();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getVpcId method, of class SubnetImpl.
     */
    @Test
    public void testGetVpcId() {
        System.out.println("getVpcId");
        SubnetImpl instance = null;
        String expResult = "";
        String result = instance.getVpcId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of delete method, of class SubnetImpl.
     */
    @Test
    public void testDelete() {
        System.out.println("delete");
        SubnetImpl instance = null;
        instance.delete();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
