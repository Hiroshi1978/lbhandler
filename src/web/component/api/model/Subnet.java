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
public interface Subnet {
    public String getId();
    public String getZone();
    public Integer getAvailableIpAddressCount();
    public String getCidrBlock();
    public boolean getDefaultForAz();
    public boolean getMapPublicIpOnLaunch();
    public String getState();
    public String getVpcId();
}
