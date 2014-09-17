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
public interface Instance {
    
    /**
     * Returns instance of load balancer with which this backend instance is registered.
     * If this backend instance is not registered with any load balancer, this method returns null.
     * @return instance of LoadBalancer or null.
     */
    public LoadBalancer getLoadBalancer();

    /**
     * Returns List of all the load balancers with which this backend instance is registered.
     * If this backend instance is not registered with any load balancer, this method returns empty list.
     * @return list of load balacers with which this backend instance is registered.
     */
    public List<LoadBalancer> getLoadBalancers();
    public String getId();
    public String getPlacement();
    
    /*
    * Register this backend instance with the specified load balancer.
    */
    public void registerWith(LoadBalancer lb);
    
    /*
    * Deregister this backend instance from the specified load balancer.
    */
    public void deregisterFrom(LoadBalancer lb);

   /*
    * Return the state of the VM in cloud.
    */
    public InstanceState getInstanceState();
   /*
    * Return the state as the backend server of the specified load balancer.
    */
    public BackendState getBackendStateOf(LoadBalancer lb);
    
    public Integer getStateCode();
    public String getStateName();
    public String getStateTransitionReason();
    public String getPublicIpAddress();
    public String getSubnetId();
    public String getVpcId();
    public String getImageId();
    public String getInstanceType();
    public String getLifeCycle();
    public String getZoneName();
    
    public void start();
    public void stop();
    public void terminate();
    
    public boolean isStarted();
    public boolean isStopped();
    public boolean isTerminated();
}
