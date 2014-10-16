/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.api.model;

import java.util.List;

/**
 *
 * @author Hiroshi
 */
public abstract class ResourceFactory {
    
    abstract public List<Instance> getInstances();
    abstract public Instance getInstance(String id);
    abstract public List<Zone> getZones();
    abstract public Zone getZone(String name);
    abstract public List<VPC> getVPCs();
    abstract public VPC getVPC(String id);
    abstract public List<Subnet> getSubnets();
    abstract public Subnet getSubnet(String id);
    abstract public List<LoadBalancer> getLoadBalancers();
    abstract public LoadBalancer getLoadBalancer(String name);
    abstract public List<AutoScalingGroup> getAutoScalingGroups();
    abstract public AutoScalingGroup getAutoScalingGroup(String name);
    abstract public List<LaunchConfiguration> getLaunchConfigurations();
    abstract public LaunchConfiguration getLaunchConfiguration(String name);
    
    //retrieve ResourceFactory implementation class instance.
    public static ResourceFactory getInstance(){
        //this is only temporary.
        //to be refactored to be able to change dependency,
        //for example, using Spring ?
        return new web.component.impl.aws.model.AWSResourceFactory();
    }
    
}
