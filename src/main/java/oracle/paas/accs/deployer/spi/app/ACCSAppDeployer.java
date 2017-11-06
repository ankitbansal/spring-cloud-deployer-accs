package oracle.paas.accs.deployer.spi.app;

import oracle.paas.accs.deployer.spi.accs.client.ACCSClient;
import oracle.paas.accs.deployer.spi.accs.client.StorageClient;
import oracle.paas.accs.deployer.spi.accs.model.Application;
import oracle.paas.accs.deployer.spi.accs.model.ApplicationStatus;
import oracle.paas.accs.deployer.spi.accs.util.ACCSUtil;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.app.AppStatus;
import org.springframework.cloud.deployer.spi.app.DeploymentState;
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

    public AppStatus status(String deploymentId) {
        String appName = ACCSUtil.getSanitizedApplicationName(deploymentId);
        ACCSClient client = new ACCSClient();
        ApplicationStatus application = client.getApplication(appName);
        if(application == null) {
            return AppStatus.of(deploymentId).generalState(DeploymentState.failed).build();
        }

        if(application.getCurrentOngoingActivity() != null) {
            return AppStatus.of(deploymentId).generalState(DeploymentState.deploying).build();
        }

        if(application.getStatus().equalsIgnoreCase("RUNNING")) {
            return AppStatus.of(deploymentId).generalState(DeploymentState.deployed).build();
        }
        return AppStatus.of(deploymentId).generalState(DeploymentState.unknown).build();
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
            AppBuilder appBuilder = new AppBuilder(appDeploymentRequest, deploymentId);
            String command = appBuilder.buildCommand(file.getName());

            File zipFile = ACCSUtil.convertToZipFile(file, command);
            System.out.println("Created zip file : " +zipFile.getAbsolutePath());
            StorageClient storageClient = new StorageClient();
            storageClient.pushFileToStorage(zipFile);
            Application application = appBuilder.getApplicationData(zipFile.getName());

            ACCSClient client = new ACCSClient();
            client.createApplication(application);
        }
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
