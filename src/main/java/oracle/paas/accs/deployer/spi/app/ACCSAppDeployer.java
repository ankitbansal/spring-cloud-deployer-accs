package oracle.paas.accs.deployer.spi.app;

import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.app.AppStatus;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.deployer.spi.core.RuntimeEnvironmentInfo;

public class ACCSAppDeployer implements AppDeployer {
    private RuntimeEnvironmentInfo runtimeEnvironmentInfo;

    public ACCSAppDeployer(RuntimeEnvironmentInfo runtimeEnvironmentInfo) {
        this.runtimeEnvironmentInfo = runtimeEnvironmentInfo;
    }

    public String deploy(AppDeploymentRequest appDeploymentRequest) {
        System.out.println(String.format("Entered deploy: Deploying AppDeploymentRequest: AppDefinition = {%s}, Resource = {%s}, Deployment Properties = {%s}",
                appDeploymentRequest.getDefinition(), appDeploymentRequest.getResource(), appDeploymentRequest.getDeploymentProperties()));
//        String deploymentId = deploymentId(appDeploymentRequest);
//
//        logger.trace("deploy: Getting Status for Deployment Id = {}", deploymentId);
        return null;
    }

    public void undeploy(String s) {

    }

    public AppStatus status(String s) {
        return null;
    }

    public RuntimeEnvironmentInfo environmentInfo() {
        return runtimeEnvironmentInfo;
    }
}
