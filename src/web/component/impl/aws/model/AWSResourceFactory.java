/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import java.util.List;
import java.util.stream.Collectors;
import web.component.api.model.AutoScalingGroup;
import web.component.api.model.Instance;
import web.component.api.model.LaunchConfiguration;
import web.component.api.model.LoadBalancer;
import web.component.api.model.ResourceFactory;
import web.component.api.model.Subnet;
import web.component.api.model.VPC;
import web.component.api.model.Zone;
import web.component.impl.aws.AWS;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author Hiroshi
 */
public class AWSResourceFactory extends ResourceFactory{
 
   /*
    * instance
    */
    @Override
    public List<Instance> getInstances(){
        
        return AWS.access().ec2().getExistEc2Instances().stream()
                .map(ec2Instance -> new InstanceImpl.Builder().id(ec2Instance.getInstanceId()).get())
                .sorted()
                .collect(toList());
    }
    
    @Override
    public Instance getInstance(String id){
        return new InstanceImpl.Builder().id(id).get();
    }
    
    public Instance createDefaultInstance(){
        
        String instanceId = AWS.access().ec2().createDefaultInstance().getInstanceId();
        return new InstanceImpl.Builder().id(instanceId).get();
    }

   /*
    * zone
    */
    @Override
    public List<Zone> getZones(){
        
        return AWS.access().ec2()
                .getExistEc2AvailabilityZones().stream()
                .map(ec2Zone -> new ZoneImpl.Builder().name(ec2Zone.getZoneName()).build())
                .collect(Collectors.toList());
    }
    
    @Override
    public Zone getZone(String name){
        return new ZoneImpl.Builder().name(name).build();
    }
    
   /*
    * vpc
    */
    @Override
    public List<VPC> getVPCs(){

        return AWS.access().ec2()
                .getExistEc2Vpcs().stream()
                .map(ec2Vpc -> new VPCImpl.Builder().id(ec2Vpc.getVpcId()).get())
                .collect(Collectors.toList());
    }
    
    @Override
    public VPC getVPC(String id){
        return new VPCImpl.Builder().id(id).get();
    }
    
   /*
    * subnet
    */
    @Override
    public List<Subnet> getSubnets(){

        return AWS.access().ec2()
                .getExistEc2Subnets().stream()
                .map(ec2Subnet -> new SubnetImpl.Builder().id(ec2Subnet.getSubnetId()).get())
                .collect(Collectors.toList());
    }
    
    @Override
    public Subnet getSubnet(String id){
        return new SubnetImpl.Builder().id(id).get();
    }
    
   /*
    * load balancer
    */
    @Override
    public List<LoadBalancer> getLoadBalancers(){
        return LoadBalancerImpl.getExistLoadBalancers();
    }
    
    @Override
    public LoadBalancer getLoadBalancer(String name){
        return LoadBalancerImpl.getExistLoadBalancerByName(name);
    }
    
   /*
    * auto scaling group
    */
    @Override
    public List<AutoScalingGroup> getAutoScalingGroups(){
        
        return AWS.access().as()
                .getExistAutoScalingGroups().stream()
                .map(awsASGroup -> new AutoScalingGroupImpl.Builder().name(awsASGroup.getAutoScalingGroupName()).get())
                .collect(Collectors.toList());
    }
    
    @Override
    public AutoScalingGroup getAutoScalingGroup(String name){
        return new AutoScalingGroupImpl.Builder().name(name).get();
    }

    @Override
    public List<LaunchConfiguration> getLaunchConfigurations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LaunchConfiguration getLaunchConfiguration(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

