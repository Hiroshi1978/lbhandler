/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.api.model;

import java.util.List;

/**
 * A load balancer instance. 
 * 
 * @author Hiroshi
 */
public interface LoadBalancer extends Comparable<LoadBalancer>{
    
    /**
     * Check if this load balancer is started, which means it is ready to serve.
     * @return true if the load balancing service can available through this load balancer. 
     */
    public boolean isStarted();
    
    /**
     * Create new listeners to this load balancers.
     * @param listeners to be created to this load balancer.
     */
    public void createListeners(List<LoadBalancerListener> listeners);
    
    /**
     * Delete listeners from this load balancer.
     * @param listeners to be deleted from this load balancer.
     */
    public void deleteListeners(List<LoadBalancerListener> listeners);

    /**
     * Create a new listener to this load balancer.
     * @param listener to be created to this load balancer. 
     */
    public void createListener(LoadBalancerListener listener);
    
    /**
     * Delete a listener from this load balancer.
     * @param listener to be deleted from this load balancer.
     */
    public void deleteListener(LoadBalancerListener listener);
    
    
    public void registerInstances(List<Instance> instances);
    public void deregisterInstances(List<Instance> instances);
    public void registerInstance(Instance instance);
    public void deregisterInstance(Instance instance);
    public void enableZones(List<Zone> zones);
    public void enableZone(Zone zone);
    public void disableZones(List<Zone> zones);
    public void disableZone(Zone zone);
    public void configureHealthCheck(HealthCheck healthCheck);
    public void delete();
    
    public List<BackendState> getInstanceStates();
    public List<BackendState> getInstanceStates(List<Instance> backendInstances);
    public List<BackendState> getInstanceStatesByInstanceId(List<String> backendInstanceIds);
    public BackendState getInstanceState(Instance backendInstance);
    public BackendState getInstanceState(String backendInstanceId);
    
    /**
     * Check if this load balancer is destroyed.
     * Once destroyed, the load balancer should not be available again. 
     * @return true if the load balancer is destroyed. 
     */
    public boolean isDestroyed();
    
    public String getName();
    public List<LoadBalancerListener> getListeners();
    public List<Zone> getZones();
    public List<Subnet> getSubnets(); 
    public List<Instance> getBackendInstances();
    
}
