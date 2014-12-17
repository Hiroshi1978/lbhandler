/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.List;
import java.util.function.UnaryOperator;
import static java.util.stream.Collectors.toList;
import web.component.api.model.AutoScalingGroup;
import web.component.api.model.Instance;
import web.component.api.model.LaunchConfiguration;
import web.component.api.model.LoadBalancer;
import web.component.api.model.Subnet;
import web.component.api.model.VPC;
import web.component.api.model.Zone;
import web.component.impl.aws.AWS;

/**
 *
 * @author Hiroshi
 */
public class AWSResourceFactory{
 
   /*
    * instance
    */
    public static List<Instance> getInstances(){
        
        return AWS.access().ec2().getExistEc2Instances().stream()
                .map(ec2Instance -> new InstanceImpl.Builder().id(ec2Instance.getInstanceId()).get())
                .sorted()
                .collect(toList());
    }
    
    public static Instance getInstance(String id){
        return new InstanceImpl.Builder().id(id).get();
    }
    
    public static Instance getNewInstance(UnaryOperator<InstanceImpl.Builder> block){
        return block.apply(new InstanceImpl.Builder()).create();
    }

   /*
    * zone
    */
    public static List<Zone> getZones(){
        
        return AWS.access().ec2()
                .getExistEc2AvailabilityZones().stream()
                .map(ec2Zone -> new ZoneImpl.Builder().name(ec2Zone.getZoneName()).build())
                .sorted()
                .collect(toList());
    }
    
    public static Zone getZone(String name){
        return new ZoneImpl.Builder().name(name).build();
    }
    
   /*
    * vpc
    */
    public static List<VPC> getVPCs(){
        return AWS.access().ec2()
                .getExistEc2Vpcs().stream()
                .map(ec2Vpc -> new VPCImpl.Builder().id(ec2Vpc.getVpcId()).get())
                .sorted()
                .collect(toList());
    }
    
    public static VPC getVPC(String id){
        return new VPCImpl.Builder().id(id).get();
    }
    
    public static VPC getNewVPC(UnaryOperator<VPCImpl.Builder> block){
        return block.apply(new VPCImpl.Builder()).create();
    }
   /*
    * subnet
    */
    public static List<Subnet> getSubnets(){

        return AWS.access().ec2()
                .getExistEc2Subnets().stream()
                .map(ec2Subnet -> new SubnetImpl.Builder().id(ec2Subnet.getSubnetId()).get())
                .sorted()
                .collect(toList());
    }
    
    public static Subnet getSubnet(String id){
        return new SubnetImpl.Builder().id(id).get();
    }
    
    public static Subnet getNewSubnet(UnaryOperator<SubnetImpl.Builder> block){
        return block.apply(new SubnetImpl.Builder()).create();
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
    
    public static LoadBalancer getNewLoadBalancer(String loadBalancerName, UnaryOperator<LoadBalancerImpl.Builder> block){
        return block.apply(new LoadBalancerImpl.Builder(loadBalancerName)).build();
    }
    
   /*
    * auto scaling group
    */
    public static List<AutoScalingGroup> getAutoScalingGroups(){     
        return AWS.access().as()
                .getExistAutoScalingGroups().stream()
                .map(awsASGroup -> new AutoScalingGroupImpl.Builder().name(awsASGroup.getAutoScalingGroupName()).get())
                .sorted()
                .collect(toList());
    }
    
    public static AutoScalingGroup getAutoScalingGroup(String name){
        return new AutoScalingGroupImpl.Builder().name(name).get();
    }

    public static AutoScalingGroup getNewAutoScalingGroup(UnaryOperator<AutoScalingGroupImpl.Builder> block){
        return block.apply(new AutoScalingGroupImpl.Builder()).create();
    }
    
    public static List<LaunchConfiguration> getLaunchConfigurations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static LaunchConfiguration getLaunchConfiguration(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
