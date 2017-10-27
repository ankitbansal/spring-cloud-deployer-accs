package oracle.paas.accs.deployer.spi.app;

import oracle.paas.accs.deployer.spi.client.ACCSClient;
import oracle.paas.accs.deployer.spi.client.Application;
import oracle.paas.accs.deployer.spi.client.StorageClient;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.app.AppStatus;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.deployer.spi.core.RuntimeEnvironmentInfo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ACCSAppDeployer implements AppDeployer {
    private RuntimeEnvironmentInfo runtimeEnvironmentInfo;

    public ACCSAppDeployer(RuntimeEnvironmentInfo runtimeEnvironmentInfo) {
        this.runtimeEnvironmentInfo = runtimeEnvironmentInfo;
    }

    public String deploy(AppDeploymentRequest appDeploymentRequest) {
        System.out.println(String.format("Entered deploy: Deploying AppDeploymentRequest: AppDefinition = {%s}, Resource = {%s}, Deployment Properties = {%s}",
                appDeploymentRequest.getDefinition(), appDeploymentRequest.getResource(), appDeploymentRequest.getDeploymentProperties()));
        String deploymentId = deploymentId(appDeploymentRequest);
        System.out.println(String.format("deploy: Getting Status for Deployment Id = {%s}", deploymentId));

        deployApplication(appDeploymentRequest, deploymentId);


        System.out.println(String.format("Exiting deploy().  Deployment Id = {%s}", deploymentId));
        return deploymentId;
    }

    public void undeploy(String s) {

    }

    public AppStatus status(String s) {
        return null;
    }

    public RuntimeEnvironmentInfo environmentInfo() {
        return runtimeEnvironmentInfo;
    }

    private void deployApplication(AppDeploymentRequest appDeploymentRequest, String deploymentId) {
        File file = null;
        try {
            file = appDeploymentRequest.getResource().getFile();
        } catch (IOException e) {
            System.out.println("Exception while retrieving file " + e.getMessage());
            e.printStackTrace();
        }

        if(file != null) {
            StorageClient storageClient = new StorageClient();
            storageClient.pushFileToStorage(file);

            ACCSClient client = new ACCSClient();
            client.createApplication(Application.from(appDeploymentRequest, deploymentId, file.getName()));
        }
        //deployApp
    }

    private String deploymentId(AppDeploymentRequest request) {
        if(request.getDefinition() != null) {
            Map<String, String> properties = request.getDefinition().getProperties();
            String appLabel = properties.get("spring.cloud.dataflow.stream.app.label");
            String streamGroup = properties.get("spring.cloud.dataflow.stream.name");
            return streamGroup + "-" + appLabel;
        }
        throw new RuntimeException("Request invalid");
    }
}
