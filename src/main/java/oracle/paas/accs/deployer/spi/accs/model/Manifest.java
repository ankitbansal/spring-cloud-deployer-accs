package oracle.paas.accs.deployer.spi.accs.model;

import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;

public class Manifest {
    private String command;
    private String type = "web";

    public String getCommand() {
        return command;
    }

    public String getType() {
        return type;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setType(String type) {
        this.type = type;
    }
}
