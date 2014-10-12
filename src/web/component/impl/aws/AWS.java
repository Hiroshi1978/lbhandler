/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import java.io.File;
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

    private final String CONF_DIR_PROP_KEY = "aws.config.file.dir";
    private final String CONF_FILE_NAME = "aws_config.txt";
    
    private AWS(){     

        try {
            
            StringBuilder confPath = 
                    new StringBuilder(System.getProperty(CONF_DIR_PROP_KEY, System.getProperty("user.dir")));
            if(!confPath.toString().endsWith(File.separator))
                confPath.append(File.separator);
            String confFile = confPath.append(CONF_FILE_NAME).toString();
            System.out.println("Loading aws client configuration from [" + confFile + "] ...");
            AWS_CONFIG.load(AWS.class.getResourceAsStream(confFile));
            
        } catch (IOException | RuntimeException ex) {
            System.out.println("failed to load aws client configuration.");
            ex.printStackTrace();
        }

        String awsKey = AWS_CONFIG.getProperty("aws.key");
        String secretKey = AWS_CONFIG.getProperty("aws.secret");
        
        AWS_CREDENTIALS = new BasicAWSCredentials(awsKey,secretKey);
    }
    
    //interface to access cloud.
    public static AWS access(){
        return INSTANCE;
    }
    
    private final CloudBlock get(BlockName name){
        
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
    
    public AWSAutoScaling as(){
        return (AWSAutoScaling)get(BlockName.AutoScaling);
    }
    public AWSEC2 ec2(){
        return (AWSEC2)get(BlockName.EC2);
    }
    public AWSELB elb(){
        return (AWSELB)get(BlockName.ELB);
    }
    
    private static enum BlockName{
        EC2,ELB,AutoScaling;
    }
}
