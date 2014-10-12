/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.ArrayList;
import java.util.List;
import web.component.api.model.Instance;
import web.component.api.model.Subnet;
import web.component.api.model.VPC;
import web.component.api.model.Zone;
import web.component.impl.aws.AWS;
import web.component.impl.aws.AWSAutoScaling;
import web.component.impl.aws.AWSEC2;
import web.component.impl.aws.AWSELB;

/**
 *
 * @author Hiroshi
 */
public class AWSResouceFactory {
 
    private static final AWSEC2 ec2 = AWS.access().ec2();
    private static final AWSELB elb = AWS.access().elb();
    private static final AWSAutoScaling as = AWS.access().as();
    
    public static List<Instance> getExistInstances(){
        
       List<com.amazonaws.services.ec2.model.Instance> ec2Instances = ec2.getExistEc2Instances();
        List<Instance> instances = new ArrayList<>();
        for(com.amazonaws.services.ec2.model.Instance ec2Instance : ec2Instances)
            instances.add(new InstanceImpl.Builder().id(ec2Instance.getInstanceId()).get());
        return instances;
    }

    public static Instance createDefaultInstance(){
        
        String instanceId = ec2.createDefaultInstance().getInstanceId();
        return new InstanceImpl.Builder().id(instanceId).get();
    }

    public static List<Zone> getExistZones(){
        
        List<com.amazonaws.services.ec2.model.AvailabilityZone> ec2Zones  = ec2.getExistEc2AvailabilityZones();
        List<Zone> zones = new ArrayList<>();
        for(com.amazonaws.services.ec2.model.AvailabilityZone ec2Zone : ec2Zones)
            zones.add(new ZoneImpl.Builder().name(ec2Zone.getZoneName()).build());
        return zones;
    }
    
    public static List<VPC> getExistVPCs(){
        List<com.amazonaws.services.ec2.model.Vpc> ec2Vpcs = ec2.getExistEc2Vpcs();
        List<VPC> vpcs = new ArrayList<>();
        for(com.amazonaws.services.ec2.model.Vpc ec2Vpc : ec2Vpcs)
            vpcs.add(new VPCImpl.Builder().id(ec2Vpc.getVpcId()).get());
        return vpcs;
    }
    
    public static List<Subnet> getExistSubnets(){
        List<com.amazonaws.services.ec2.model.Subnet> ec2Subnets = ec2.getExistEc2Subnets();
        List<Subnet> subnets = new ArrayList<>();
        for(com.amazonaws.services.ec2.model.Subnet ec2Subnet : ec2Subnets)
            subnets.add(new SubnetImpl.Builder().id(ec2Subnet.getSubnetId()).get());
        return subnets;
    }
}
