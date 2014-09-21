/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import com.amazonaws.services.ec2.model.AvailabilityZone;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import web.component.api.model.Zone;
import web.component.impl.aws.AWS;
import web.component.impl.aws.ec2.AWSEC2;

/**
 *
 * @author ksc
 */
public class ZoneImplTest {
    
    static final String expectedRegionName = "";
    static final String expectedZoneName = "";
    static final String expectedState = "";
    static final Zone testInstance = new ZoneImpl.Builder().name(expectedZoneName).build();
    static final String expectedStringExpression = "{ZoneName: " + testInstance.getName() + ", RegionName: " + testInstance.getRegionName() + "}";

    public ZoneImplTest() {
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
     * Test of asEc2Zone method, of class ZoneImpl.
     */
    @Test
    public void testAsEc2Zone() {
        
        System.out.println("asEc2Zone");
        
        AWSEC2 ec2 = (AWSEC2)AWS.get(AWS.BlockName.EC2);
        AvailabilityZone source = ec2.getExistEc2AvailabilityZone(expectedZoneName);
        AvailabilityZone viewAsEc2Zone = ((ZoneImpl)testInstance).asEc2Zone();

        //two instances should be equal, but not the same.
        assertEquals(source, viewAsEc2Zone);
        assertFalse(source == viewAsEc2Zone);
        
        assertEquals(expectedRegionName,viewAsEc2Zone.getRegionName());
        assertEquals(expectedZoneName,viewAsEc2Zone.getZoneName());
        assertEquals(new ArrayList<>(),viewAsEc2Zone.getMessages());
        assertEquals(null,viewAsEc2Zone.getState());

    }

    /**
     * Test of getMessages method, of class ZoneImpl.
     */
    @Test
    public void testGetMessages() {
        System.out.println("getMessages");
        
        List<String> expectedMessages = new ArrayList<>();
        assertEquals(expectedMessages, testInstance.getMessages());
    }

    /**
     * Test of getName method, of class ZoneImpl.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        assertEquals(expectedZoneName, testInstance.getName());
    }

    /**
     * Test of getRegionName method, of class ZoneImpl.
     */
    @Test
    public void testGetRegionName() {
        System.out.println("getRegionName");
        assertEquals(expectedRegionName, testInstance.getRegionName());
    }

    /**
     * Test of getState method, of class ZoneImpl.
     */
    @Test
    public void testGetState() {
        System.out.println("getState");
        assertEquals(expectedState, testInstance.getState());
    }

    /**
     * Test of equals method, of class ZoneImpl.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
    	String equalZoneName = "";
    	String anotherZoneName = "";
        ZoneImpl equalZone    = (ZoneImpl)new ZoneImpl.Builder().name(equalZoneName).build();
        ZoneImpl anotherZone = (ZoneImpl)new ZoneImpl.Builder().name(anotherZoneName).build();
        assertTrue(testInstance.equals(equalZone));
        assertFalse(testInstance.equals(anotherZone));

    }

    /**
     * Test of hashCode method, of class ZoneImpl.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        String equalZoneName = "";
    	String anotherZoneName = "";
        ZoneImpl equalZone    = (ZoneImpl)new ZoneImpl.Builder().name(equalZoneName).build();
        ZoneImpl anotherZone = (ZoneImpl)new ZoneImpl.Builder().name(anotherZoneName).build();
        assertEquals(testInstance.hashCode(), equalZone.hashCode());
        assertTrue(testInstance.hashCode() != anotherZone.hashCode());
    }

    /**
     * Test of toString method, of class ZoneImpl.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals(expectedStringExpression, testInstance.toString());
    }
    
}
