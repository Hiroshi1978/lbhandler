/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import web.component.impl.aws.AWS;
import web.component.impl.aws.AWSAutoScaling;
import web.component.impl.aws.AWSEC2;
import web.component.impl.aws.AWSELB;

/**
 *
 * @author Hiroshi
 */
public abstract class AWSModelBase {
 
   /* 
    * Returns AWS EC2 service interface.
    */
    protected final AWSEC2 ec2(){
        return (AWSEC2)AWS.access().get(AWS.BlockName.EC2);
    }
    
   /* 
    * Returns AWS ELB service interface.
    */
    protected final AWSELB elb(){
        return (AWSELB)AWS.access().get(AWS.BlockName.ELB);
    }

   /* 
    * Returns AWS AutoScaling service interface.
    */
    protected final AWSAutoScaling as(){
        return (AWSAutoScaling)AWS.access().get(AWS.BlockName.AutoScaling);
    }
}
