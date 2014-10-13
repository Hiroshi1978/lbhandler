/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.ArrayList;
import java.util.List;
import web.component.api.model.Instance;
import web.component.api.model.LoadBalancer;
import web.component.api.model.Subnet;
import web.component.api.model.VPC;
import web.component.api.model.Zone;
import web.component.impl.aws.AWS;

/**
 *
 * @author Hiroshi
 */
public class AWSResouceFactory {
 
   /*
    * instance
    */
    public static List<Instance> getInstances(){
        
       List<com.amazonaws.services.ec2.model.Instance> ec2Instances = AWS.access().ec2().getExistEc2Instances();
        List<Instance> instances = new ArrayList<>();
        for(com.amazonaws.services.ec2.model.Instance ec2Instance : ec2Instances)
            instances.add(new InstanceImpl.Builder().id(ec2Instance.getInstanceId()).get());
        return instances;
    }
    public Instance getInstance(String id){
        return new InstanceImpl.Builder().id(id).get();
    }
    public static Instance createDefaultInstance(){
        
        String instanceId = AWS.access().ec2().createDefaultInstance().getInstanceId();
        return new InstanceImpl.Builder().id(instanceId).get();
    }

   /*
    * zone
    */
    public static List<Zone> getZones(){
        
        List<com.amazonaws.services.ec2.model.AvailabilityZone> ec2Zones  = AWS.access().ec2().getExistEc2AvailabilityZones();
        List<Zone> zones = new ArrayList<>();
        for(com.amazonaws.services.ec2.model.AvailabilityZone ec2Zone : ec2Zones)
            zones.add(new ZoneImpl.Builder().name(ec2Zone.getZoneName()).build());
        return zones;
    }
    public static Zone getZone(String name){
        return new ZoneImpl.Builder().name(name).build();
    }
    
   /*
    * vpc
    */
    public static List<VPC> getVPCs(){
        List<com.amazonaws.services.ec2.model.Vpc> ec2Vpcs = AWS.access().ec2().getExistEc2Vpcs();
        List<VPC> vpcs = new ArrayList<>();
        for(com.amazonaws.services.ec2.model.Vpc ec2Vpc : ec2Vpcs)
            vpcs.add(new VPCImpl.Builder().id(ec2Vpc.getVpcId()).get());
        return vpcs;
    }
    public static VPC getVPC(String id){
        return new VPCImpl.Builder().id(id).get();
    }
    
   /*
    * subnet
    */
    public static List<Subnet> getSubnets(){
        List<com.amazonaws.services.ec2.model.Subnet> ec2Subnets = AWS.access().ec2().getExistEc2Subnets();
        List<Subnet> subnets = new ArrayList<>();
        for(com.amazonaws.services.ec2.model.Subnet ec2Subnet : ec2Subnets)
            subnets.add(new SubnetImpl.Builder().id(ec2Subnet.getSubnetId()).get());
        return subnets;
    }
    public static Subnet getSubnet(String id){
        return new SubnetImpl.Builder().id(id).get();
    }
    
   /*
    * load balancer
    */
    public static List<LoadBalancer> getLoadBalancers(){
        return LoadBalancerImpl.getExistLoadBalancers();
    }
    public static LoadBalancer getLoadBalancer(String name){
        return LoadBalancerImpl.getExistLoadBalancerByName(name);
    }
}

