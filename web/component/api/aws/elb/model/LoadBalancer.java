/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.api.aws.elb.model;

import java.util.List;

/**
 * A load balancer instance. 
 * 
 * @author Hiroshi
 */
public interface LoadBalancer {
    
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
    
    
    public void registerInstances(List<BackendInstance> instances);
    public void deregisterInstances(List<BackendInstance> instances);
    public void registerInstance(BackendInstance instance);
    public void deregisterInstance(BackendInstance instance);
    public void enableZones(List<Zone> zones);
    public void enableZone(Zone zone);
    public void disableZones(List<Zone> zones);
    public void disableZone(Zone zone);
    public void delete();
    
    public List<BackendInstanceState> getInstanceStates();
    public List<BackendInstanceState> getInstanceStates(List<BackendInstance> backendInstances);
    public BackendInstanceState getInstanceState(BackendInstance backendInstance);
    
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
    public List<BackendInstance> getBackendInstances();
}
