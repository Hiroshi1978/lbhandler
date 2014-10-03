/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import web.component.impl.aws.AWS;
import web.component.impl.aws.autoscaling.AWSAutoScaling;
import web.component.impl.aws.ec2.AWSEC2;
import web.component.impl.aws.elb.AWSELB;

/**
 *
 * @author Hiroshi
 */
public abstract class AWSModelBase {
 
    protected final AWS aws = AWS.access();
    
   /* 
    * Returns AWS EC2 service interface.
    */
    protected final AWSEC2 ec2(){
        return (AWSEC2)aws.get(AWS.BlockName.EC2);
    }
    
   /* 
    * Returns AWS ELB service interface.
    */
    protected final AWSELB elb(){
        return (AWSELB)aws.get(AWS.BlockName.ELB);
    }

   /* 
    * Returns AWS AutoScaling service interface.
    */
    protected final AWSAutoScaling as(){
        return (AWSAutoScaling)aws.get(AWS.BlockName.AutoScaling);
    }
}
