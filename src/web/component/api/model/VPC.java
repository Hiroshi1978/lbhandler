package web.component.api.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hiroshi
 */
public interface VPC {
    
    public String getCidrBlock();
    public String getDhcpOptionsId();
    public String getInstanceTenancy();
    public Boolean getIsDefault();
    public String getState();
    public String getId();
    
    public void delete();
}
