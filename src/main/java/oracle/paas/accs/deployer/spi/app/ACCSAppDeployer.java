package oracle.paas.accs.deployer.spi.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import oracle.paas.accs.deployer.spi.client.ACCSClient;
import oracle.paas.accs.deployer.spi.client.Application;
import oracle.paas.accs.deployer.spi.client.ApplicationStatus;
import oracle.paas.accs.deployer.spi.client.StorageClient;
import oracle.paas.accs.deployer.spi.util.ACCSUtil;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.app.AppStatus;
import org.springframework.cloud.deployer.spi.app.DeploymentState;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.deployer.spi.core.RuntimeEnvironmentInfo;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
            File zipFile = ACCSUtil.convertToZipFile(file);
            System.out.println("Created zip file : " +zipFile.getAbsolutePath());
            StorageClient storageClient = new StorageClient();
            storageClient.pushFileToStorage(zipFile);

            ACCSClient client = new ACCSClient();
            String appName = ACCSUtil.getSanitizedApplicationName(deploymentId);
            client.createApplication(Application.from(appDeploymentRequest, appName, zipFile.getName(), file.getName()));
        }
    }

    private Map<String, String> getApplicationProperties(String deploymentId, AppDeploymentRequest request) {
        Map<String, String> applicationProperties = getSanitizedApplicationProperties(deploymentId, request);

        if (!useSpringApplicationJson(request)) {
            return applicationProperties;
        }

//        try {
//            return Collections.singletonMap("SPRING_APPLICATION_JSON", OBJECT_MAPPER.writeValueAsString(applicationProperties));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

    private Map<String, String> getCommandLineArguments(AppDeploymentRequest request) {
        if (request.getCommandlineArguments().isEmpty()) {
            return Collections.emptyMap();
        }

        String argumentsAsString = request.getCommandlineArguments().stream()
                .collect(Collectors.joining(" "));
        return null;
//        String yaml = new Yaml().dump(Collections.singletonMap("arguments", argumentsAsString));
//
//        return Collections.singletonMap("JBP_CONFIG_JAVA_MAIN", yaml);
    }


    private boolean useSpringApplicationJson(AppDeploymentRequest request) {
//        return Optional.ofNullable(request.getDeploymentProperties().get(USE_SPRING_APPLICATION_JSON_KEY))
//                .map(Boolean::valueOf)
//                .orElse(this.deploymentProperties.isUseSpringApplicationJson());
        return false;
    }


    private Map<String, String> getEnvironmentVariables(String deploymentId, AppDeploymentRequest request) {
        Map<String, String> envVariables = new HashMap<String, String>();
        envVariables.putAll(getApplicationProperties(deploymentId, request));
        envVariables.putAll(getCommandLineArguments(request));
        String group = request.getDeploymentProperties().get(AppDeployer.GROUP_PROPERTY_KEY);
        if (group != null) {
            envVariables.put("SPRING_CLOUD_APPLICATION_GROUP", group);
        }
        envVariables.put("SPRING_CLOUD_APPLICATION_GUID", "${vcap.application.name}:${vcap.application.instance_index}");
        envVariables.put("SPRING_APPLICATION_INDEX", "${vcap.application.instance_index}");
        return envVariables;
    }

    private Map<String, String> getSanitizedApplicationProperties(String deploymentId, AppDeploymentRequest request) {
        Map<String, String> applicationProperties = new HashMap<String, String>(request.getDefinition().getProperties());
        applicationProperties.remove("server.port");

        return applicationProperties;
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
