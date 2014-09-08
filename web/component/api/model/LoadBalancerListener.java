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
public interface LoadBalancerListener {
    public void setInstancePort(int instancePort);
    public void setServicePort(int servciePort);
    public Integer getInstancePort();
    public Integer getServicePort();
    public void setInstanceProtocol(String instanceProtocol);
    public void setServiceProtocol(String serviceProtocol);
    public String getInstanceProtocol();
    public String getServiceProtocol();
    public void setServerCertificate(String serverCertificateId);
    public String getServerCertificate();
    public LoadBalancer getLoadBalancer();
    
   /*
    * Add this load balancer listener to load balancer.
    */
    public void addTo(LoadBalancer lb);
    
   /*
    * Delete this load balancer listener from load balancer, and return the load balancer instance.
    * If this listener is not attached to any load balancer, return null.
    */
    public LoadBalancer delete();
}
