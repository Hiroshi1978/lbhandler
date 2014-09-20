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
import web.component.api.model.VPC;

/**
 *
 * @author ksc
 */
public class VPCImplTest {
    
    static final String expectedCidrBlock = "10.1.0.0/16";
    static final String expectedTenancy = "";
    static final String expectedState = "";
    static final String expectedDhcpOptionsId = "";
    static VPCImpl testInstance;
    static String expectedStringExpression;
    static Set<VPC> testInstances = new HashSet<>();
    
    public VPCImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        testInstance = (VPCImpl) new VPCImpl.Builder().cidr(expectedCidrBlock).tenancy(expectedTenancy).create();
        testInstances.add(testInstance);
        
        while(!testInstance.getState().equals("available")){
            System.out.println("wait for test vpc to be ready ...");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        
        expectedStringExpression = "{VpcId: " + testInstance.getId() + ",CidrBlock: " + expectedCidrBlock + ",DhcpOptionsId: " + expectedDhcpOptionsId + ",Tags: [],InstanceTenancy: " + expectedTenancy + ",}";
        System.out.println("Vpc for test is now available.");
    }
    
    @AfterClass
    public static void tearDownClass() {
        
        for(VPC toDelete : testInstances){
            System.out.println("Delete test vpc [" + toDelete.toString() + "]");
            toDelete.delete();
        }
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getCidrBlock method, of class VPCImpl.
     */
    @Test
    public void testGetCidrBlock() {

        System.out.println("getCidrBlock");
        assertEquals(expectedCidrBlock, testInstance.getCidrBlock());
    }

    /**
     * Test of getDhcpOptionsId method, of class VPCImpl.
     */
    @Test
    public void testGetDhcpOptionsId() {
        
        System.out.println("getDhcpOptionsId");
        assertEquals(expectedDhcpOptionsId, testInstance.getDhcpOptionsId());
    }

    /**
     * Test of getInstanceTenancy method, of class VPCImpl.
     */
    @Test
    public void testGetInstanceTenancy() {
        
        System.out.println("getInstanceTenancy");
        assertEquals(expectedTenancy, testInstance.getInstanceTenancy());
    }

    /**
     * Test of getIsDefault method, of class VPCImpl.
     */
    @Test
    public void testGetIsDefault() {
        
        System.out.println("getIsDefault");
        assertEquals(true, testInstance.getIsDefault());
    }

    /**
     * Test of getState method, of class VPCImpl.
     */
    @Test
    public void testGetState() {
        
        System.out.println("getState");
        assertEquals(expectedState, testInstance.getState());
    }

    /**
     * Test of getId method, of class VPCImpl.
     */
    @Test
    public void testGetId() {
        
        System.out.println("getId");
        VPC existVpc = new VPCImpl.Builder().id(testInstance.getId()).get();
        assertEquals(testInstance.getId(), existVpc.getId());
    }

    /**
     * Test of delete method, of class VPCImpl.
     */
    @Test
    public void testDelete() {
        System.out.println("delete");
        
        VPC toBeDeleted = new VPCImpl.Builder().cidr(expectedCidrBlock).tenancy(expectedTenancy).create();
        
        while(!toBeDeleted.getState().equals("available")){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        
        testInstances.add(toBeDeleted);
        String deletedId = toBeDeleted.getId();
        toBeDeleted.delete();
        
        //wait for deletion to be completed.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
        }
        VPC shouldHaveBeenDeleted = new VPCImpl.Builder().id(deletedId).get();
        assertEquals("Unknown state", shouldHaveBeenDeleted.getState());
    }

    /**
     * Test of equals method, of class VPCImpl.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        VPC equalInstance = new VPCImpl.Builder().id(testInstance.getId()).get();
        VPC anotherInstance = new VPCImpl.Builder().cidr(expectedCidrBlock).tenancy(expectedTenancy).create();
        testInstances.add(anotherInstance);
        
        while(!anotherInstance.getState().equals("available")){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        assertTrue(testInstance.equals(equalInstance));
        assertFalse(testInstance.equals(anotherInstance));
    }

    /**
     * Test of hashCode method, of class VPCImpl.
     */
    @Test
    public void testHashCode() {
        
        System.out.println("hashCode");
        
        VPC equalInstance = new VPCImpl.Builder().id(testInstance.getId()).get();
        VPC anotherInstance = new VPCImpl.Builder().cidr(expectedCidrBlock).tenancy(expectedTenancy).create();
        testInstances.add(anotherInstance);
        
        while(!anotherInstance.getState().equals("available")){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        assertTrue(testInstance.hashCode() == equalInstance.hashCode());
        assertTrue(testInstance.hashCode() != anotherInstance.hashCode());
    }

    /**
     * Test of toString method, of class VPCImpl.
     */
    @Test
    public void testToString() {
        
        System.out.println("toString");
        assertEquals(expectedStringExpression, testInstance.toString());
    }
    
}
