/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.api.model;

/**
 *
 * @author Hiroshi
 */
public interface BackendInstance {
    
    /**
     * Returns instance of load balancer with which this backend instance is registered.
     * If this backend instance is not registered with any load balancer, this method returns null.
     * @return instance of LoadBalancer or null.
     */
    public LoadBalancer getLoadBalancer();
    public String getId();
    public void register(LoadBalancer lb);
    
    /*
    * If this backend instance has already been registered with any load balancer,
    * deregister from it and return the instance of the load balancer.
    * If failed to deregister or not been registered, return null.
    */
    public LoadBalancer deregister();
    public BackendInstanceState getBackendInstanceState();
}
