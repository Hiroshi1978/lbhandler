/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

import web.component.impl.Cloud;

/**
 *
 * @author Hiroshi
 */
public interface AWS extends Cloud{
    
    public AWSAutoScaling as();
    public AWSEC2 ec2();
    public AWSELB elb();

    public static AWS access(){
        return AWSImpl.access();
    }
}
