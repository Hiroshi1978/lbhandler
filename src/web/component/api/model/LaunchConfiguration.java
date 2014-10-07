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
public interface LaunchConfiguration extends Comparable<LaunchConfiguration>{
    
    public String getName();
    public boolean getAssociatePublicIpAddress();
    public boolean getEbsOptimized();
    public String getIamInstanceProfile();
    public String getImageId();
    public String getInstanceType();
    public String getKernelId();
    public String getKeyName();
    public String getPlacementTenancy();
    public String getRamdiskId();
    public List<String> getSecurityGroups();
    public String getSpotPrice();
    public String getUserData();
    
    public void delete();
}
