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
}
