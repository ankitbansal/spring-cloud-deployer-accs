package oracle.paas.accs.deployer.spi.client;

import oracle.paas.accs.deployer.spi.util.ACCSUtil;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;

import java.util.LinkedHashMap;
import java.util.Map;

public class Application {
    private String name;
    private String runtime;
    private String notes;
    private Manifest manifest;
    private Deployment deployment;
    private String archiveURL;
    private String archiveFileName;

    public String getSubscription() {
        return subscription;
    }

    private String subscription;

    public String getName() {
        return name;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getNotes() {
        return notes;
    }

    public Manifest getManifest() {
        return manifest;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public String getArchiveURL() {
        return archiveURL;
    }

    public String getArchiveFileName() {
        return archiveFileName;
    }

    public static Application from(AppDeploymentRequest appDeploymentRequest, String name, String storageFilename) {
        Application  application = new Application();
        application.name = ACCSUtil.getSanitizedApplicationName(name);
        application.notes = "App created using accs dataflow server";
        application.runtime = "Java";
        application.manifest = Manifest.from(appDeploymentRequest, name);
        application.deployment = Deployment.from(appDeploymentRequest);
        application.archiveURL = "_apaas/" +storageFilename;
        application.archiveFileName = storageFilename;
        application.subscription = "MONTHLY";

        return application;
    }


    static class Manifest {
        private String command;
        private String type;

        public String getCommand() {
            return command;
        }

        public String getType() {
            return type;
        }
        private static Manifest from(AppDeploymentRequest appDeploymentRequest, String name) {
            Manifest manifest = new Manifest();
            manifest.type = "worker";
            manifest.command = "java -jar " +name;
            return manifest;
        }
    }

    static class Deployment {
        private Integer instances;
        private String memory;
        private Map<String, String> environment = new LinkedHashMap<String, String>();

        public Integer getInstances() {
            return instances;
        }

        public String getMemory() {
            return memory;
        }

        public Map<String, String> getEnvironment() {
            return environment;
        }

        private static Deployment from(AppDeploymentRequest appDeploymentRequest) {
            Deployment deployment = new Deployment();
            deployment.memory = "1G";
            deployment.instances = 1;
            return deployment;
        }
    }
}



