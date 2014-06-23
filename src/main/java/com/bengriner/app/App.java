package com.bengriner.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;

public class App 
{
    public static void main( String[] args )
    {
        File file = new File("/Users/bengriner/Downloads/AwsCredentials.properties");
        FileInputStream fis = null;
        AWSCredentials credentials;
        AmazonEC2Client client;

        try {
            fis = new FileInputStream(file);
            credentials = new PropertiesCredentials(fis);
            client = new AmazonEC2Client(credentials);

            RunInstancesRequest runInstancesRequest =
                    new RunInstancesRequest();

            runInstancesRequest.withImageId("ami-00a00000")
                    .withInstanceType("t1.micro")
                    .withMinCount(1)
                    .withMaxCount(1)
                    .withKeyName("bpg-keyname")
                    .withSecurityGroups("bpg-securitygroup");

            RunInstancesResult runInstancesResult =
                    client.runInstances(runInstancesRequest);

            // add name to instance
            List<Instance> instances = runInstancesResult.getReservation().getInstances();
            int idx = 0;
            for (Instance instance : instances) {
                CreateTagsRequest createTagsRequest = new CreateTagsRequest();
                createTagsRequest.withResources(instance.getInstanceId())
                        .withTags(new Tag("Name", "bpg-test"));
                client.createTags(createTagsRequest);

                idx++;
            }
            System.out.println("Tagged " + idx + " instances.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
