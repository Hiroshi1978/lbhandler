/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import web.component.impl.Cloud;
import web.component.impl.CloudBlock;

/**
 *
 * @author Hiroshi
 */
public class AWS implements Cloud{
    
    private static final AWS INSTANCE = new AWS();

    private final Properties AWS_CONFIG = new Properties();
    
    private final AWSCredentials AWS_CREDENTIALS;
    
    private final Map<BlockName, CloudBlock> components = new HashMap<>();

    private AWS(){     

        try {
            AWS_CONFIG.load(AWS.class.getResourceAsStream("./aws_config.txt"));
        } catch (IOException ex) {
            System.out.println("configuration file not found.");
            throw new RuntimeException(ex);
        }

        String awsKey = AWS_CONFIG.getProperty("aws.key");
        String secretKey = AWS_CONFIG.getProperty("aws.secret");
        
        AWS_CREDENTIALS = new BasicAWSCredentials(awsKey,secretKey);
    }
    
    //interface to access cloud.
    public static AWS access(){
        return INSTANCE;
    }
    
    public final CloudBlock get(BlockName name){
        
        CloudBlock cb = components.get(name);
        
        if(cb == null){
        
            if(BlockName.EC2.equals(name))
                cb = AWSEC2.get();
            if(BlockName.ELB.equals(name))
                cb = AWSELB.get();
            if(BlockName.AutoScaling.equals(name))
                cb = AWSAutoScaling.get();
            
            components.put(name, cb);
        }
        return cb;
    }
    
   /*
    * returns a copy instancd of AWS client config.
    */
    Properties conf(){

        Properties copyConf = new Properties();
        
        List<Object> keys = new ArrayList<>(AWS_CONFIG.keySet());
        for(Object key : keys){
            String keyString = (String)key;
            copyConf.setProperty(keyString, AWS_CONFIG.getProperty(keyString));
        }
        
        return copyConf;
    }
    
   /*
    * returns a copy instancd of AWS client credentials.
    */
    AWSCredentials credentials(){
        
        String awsKey = AWS_CREDENTIALS.getAWSAccessKeyId();
        String secretKey = AWS_CREDENTIALS.getAWSSecretKey();
        
        //return a copy instance.
        return new BasicAWSCredentials(awsKey, secretKey);
    }
    
    public static enum BlockName{
        EC2,ELB,AutoScaling;
    }
}
