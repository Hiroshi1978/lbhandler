/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import web.component.api.model.Subnet;
import web.component.api.model.VPC;
import web.component.impl.aws.AWS;
import web.component.impl.aws.ec2.AWSEC2;

/**
 *
 * @author Hiroshi
 */
public class SubnetImplTest {

    static final String expectedCidrBlock = "10.1.1.0/24";
    static final String expectedState = "";
    static final String expectedDhcpOptionsId = "";
    static String expectedId;
    static String expectedAvailabilityZone;
    static Integer expectedAvailableIpAddressCount;
    static Subnet testInstance;
    static VPC testVPC;
    static String expectedStringExpression;
    static Set<Subnet> testInstances = new HashSet<>();
    
    public SubnetImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        System.out.println("create test vpc.");
        testVPC = new VPCImpl.Builder().cidr("10.1.0.0/16").tenancy("default").create();
        
        while(!testVPC.getState().equals("available")){
            System.out.println("wait for test vpc to be ready ...");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        System.out.println("test vpc is now available.");
        
        System.out.println("create test subnets");
        testInstance = new SubnetImpl.Builder().cidr(expectedCidrBlock).vpc(testVPC.getId()).create();
        while(!testInstance.getState().equals("available")){
            System.out.println("wait for test subnet to be ready ...");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
            }
        }
        expectedId = testInstance.getId();
        expectedStringExpression = "{}";
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
        
        AWSEC2 ec2 = (AWSEC2) AWS.access().get(AWS.BlockName.EC2);
        com.amazonaws.services.ec2.model.Subnet source = ec2.getExistEc2Subnet(expectedId);
        com.amazonaws.services.ec2.model.Subnet viewAsEc2Subnet = ((SubnetImpl)testInstance).asEc2Subnet();
        
        //two instances should be equal, but not the same.
        assertEquals(source, viewAsEc2Subnet);
        assertFalse(source == viewAsEc2Subnet);
        
        assertEquals(expectedAvailabilityZone, viewAsEc2Subnet.getAvailabilityZone());
        assertEquals(expectedAvailableIpAddressCount, viewAsEc2Subnet.getAvailableIpAddressCount());
        assertEquals(expectedCidrBlock, viewAsEc2Subnet.getCidrBlock());
        assertEquals(false, viewAsEc2Subnet.getDefaultForAz());
        assertEquals(false, viewAsEc2Subnet.getMapPublicIpOnLaunch());
        assertEquals(null, viewAsEc2Subnet.getState());
        assertEquals(expectedId, viewAsEc2Subnet.getSubnetId());
        assertEquals(new ArrayList<>(), viewAsEc2Subnet.getTags());
        assertEquals(testVPC.getId(), viewAsEc2Subnet.getVpcId());
    }

    /**
     * Test of getId method, of class SubnetImpl.
     */
    @Test
    public void testGetId() {
        
        System.out.println("getId");
        
        Subnet equalInstance = new SubnetImpl.Builder().id(expectedId).get();
        assertEquals(expectedId, equalInstance.getId());
    }

    /**
     * Test of equals method, of class SubnetImpl.
     */
    @Test
    public void testEquals() {
        
        System.out.println("equals");
        
        Subnet equalInstance = new SubnetImpl.Builder().id(expectedId).get();
        Subnet anotherInstance = new SubnetImpl.Builder().vpc(testVPC.getId()).cidr("10.1.2.0/24").create();
        while(!anotherInstance.getState().equals("available")){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        testInstances.add(anotherInstance);
        assertTrue(testInstance.equals(equalInstance));
        assertFalse(testInstance.equals(anotherInstance));
    }

    /**
     * Test of hashCode method, of class SubnetImpl.
     */
    @Test
    public void testHashCode() {

        System.out.println("hashCode");

        Subnet equalInstance = new SubnetImpl.Builder().id(expectedId).get();
        Subnet anotherInstance = new SubnetImpl.Builder().vpc(testVPC.getId()).cidr("10.1.3.0/24").create();
        while(!anotherInstance.getState().equals("available")){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
        }
        testInstances.add(anotherInstance);
        assertTrue(testInstance.hashCode() == equalInstance.hashCode());
        assertFalse(testInstance.hashCode() == anotherInstance.hashCode());
    }

    /**
     * Test of toString method, of class SubnetImpl.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals(expectedStringExpression, testInstance.toString());
    }

    /**
     * Test of getZone method, of class SubnetImpl.
     */
    @Test
    public void testGetZone() {

        System.out.println("getZone");
        assertEquals(expectedAvailabilityZone, testInstance.getZone());
    }

    /**
     * Test of getAvailableIpAddressCount method, of class SubnetImpl.
     */
    @Test
    public void testGetAvailableIpAddressCount() {

        System.out.println("getAvailableIpAddressCount");
        assertEquals(expectedAvailableIpAddressCount, testInstance.getAvailableIpAddressCount());
    }

    /**
     * Test of getCidrBlock method, of class SubnetImpl.
     */
    @Test
    public void testGetCidrBlock() {

        System.out.println("getCidrBlock");
        assertEquals(expectedCidrBlock, testInstance.getCidrBlock());
    }

    /**
     * Test of getDefaultForAz method, of class SubnetImpl.
     */
    @Test
    public void testGetDefaultForAz() {

        System.out.println("getDefaultForAz");
        assertEquals(false, testInstance.getDefaultForAz());
    }

    /**
     * Test of getMapPublicIpOnLaunch method, of class SubnetImpl.
     */
    @Test
    public void testGetMapPublicIpOnLaunch() {
        
        System.out.println("getMapPublicIpOnLaunch");
        assertEquals(false, testInstance.getMapPublicIpOnLaunch());
    }

    /**
     * Test of getState method, of class SubnetImpl.
     */
    @Test
    public void testGetState() {

        System.out.println("getState");
        assertEquals(expectedState, testInstance.getState());

    }

    /**
     * Test of getVpcId method, of class SubnetImpl.
     */
    @Test
    public void testGetVpcId() {
        
        System.out.println("getVpcId");
        assertEquals(testVPC.getId(), testInstance.getVpcId());

    }

    /**
     * Test of delete method, of class SubnetImpl.
     */
    @Test
    public void testDelete() {
        
        System.out.println("delete");

        Subnet toBeDeleted = new SubnetImpl.Builder().vpc(testVPC.getId()).cidr("10.1.4.0/24").create();
        
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
        Subnet shouldHaveBeenDeleted = new SubnetImpl.Builder().id(deletedId).get();
        
        AWSEC2 ec2 = (AWSEC2) AWS.access().get(AWS.BlockName.EC2);
        assertEquals(null, ec2.getExistEc2Subnet(deletedId));
        assertEquals("Unknown state", shouldHaveBeenDeleted.getState());
    }

    /**
     * Test of compareTo method, of class SubnetImpl.
     */
    @Test
    public void testCompareTo() {
        
        System.out.println("compareTo");
        VPC testVpc = new VPCImpl.Builder().cidr("10.1.0.0/16").create();
        List<String> testSubnetIds = new ArrayList<>();
        
        for(int i=0; i<3; i++)
            testSubnetIds.add(new SubnetImpl.Builder().cidr("10.1." + (i+1) + ".0/24").vpc(testVpc.getId()).create().getId());
        
        Collections.sort(testSubnetIds);

        Subnet testSubnet1 = new SubnetImpl.Builder().id(testSubnetIds.get(0)).get();
        Subnet testSubnet2 = new SubnetImpl.Builder().id(testSubnetIds.get(1)).get();
        Subnet testSubnet3 = new SubnetImpl.Builder().id(testSubnetIds.get(2)).get();
        
        assertTrue(testSubnet1.compareTo(testSubnet1) == 0);
        assertTrue(testSubnet1.compareTo(testSubnet2) < 0);
        assertTrue(testSubnet1.compareTo(testSubnet3) < 0);
        assertTrue(testSubnet2.compareTo(testSubnet1) > 0);
        assertTrue(testSubnet2.compareTo(testSubnet2) == 0);
        assertTrue(testSubnet2.compareTo(testSubnet3) < 0);
        assertTrue(testSubnet3.compareTo(testSubnet1) > 0);
        assertTrue(testSubnet3.compareTo(testSubnet2) > 0);
        assertTrue(testSubnet3.compareTo(testSubnet3) == 0); 
        
        testVpc.delete();
    }
    
}
