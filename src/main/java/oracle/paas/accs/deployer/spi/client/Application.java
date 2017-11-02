package oracle.paas.accs.deployer.spi.client;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;

import javax.imageio.spi.ServiceRegistry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    public static Application from(AppDeploymentRequest appDeploymentRequest, String name, String storageFilename, String jarName) {
        Application  application = new Application();
        application.name = name;
        application.notes = "App created using accs dataflow server";
        application.runtime = "Java";
        application.manifest = Manifest.from(appDeploymentRequest, jarName);
        application.deployment = Deployment.from(appDeploymentRequest);
        application.archiveURL = "_apaas/" +storageFilename;
        application.archiveFileName = storageFilename;
        application.subscription = "HOURLY";

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
        private static Manifest from(AppDeploymentRequest appDeploymentRequest, String jarName) {
            Manifest manifest = new Manifest();
            manifest.type = "web";
            Map<String, String> properties = appDeploymentRequest.getDefinition().getProperties();
            String streamGroup = properties.get("spring.cloud.dataflow.stream.name");
            manifest.command = "java -jar " +jarName + " " +
                    "--spring.cloud.stream.kafka.binder.brokers=10.252.239.69:6667 " +
                    "--spring.cloud.stream.kafka.binder.zkNodes=10.252.239.69:2181 " +
                    "--spring.cloud.stream.bindings.input.destination=" + streamGroup + " " +
                    "--spring.cloud.stream.bindings.output.destination=" + streamGroup;;
            return manifest;
        }
    }

    static class Deployment {
        private Integer instances;
        private String memory;
        private List<ServiceBinding> services = new ArrayList<ServiceBinding>();
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

        public List<ServiceBinding> getServices() {
            return services;
        }

        private static Deployment from(AppDeploymentRequest appDeploymentRequest) {
            Deployment deployment = new Deployment();
            deployment.memory = "2G";
            deployment.instances = 1;

            ServiceBinding serviceBinding = new ServiceBinding();
            serviceBinding.type = "OEHPCS";
            serviceBinding.name = "Kafka";
            deployment.services.add(serviceBinding);
            return deployment;
        }

        static  class ServiceBinding {
            private String name;
            private String type;

            public String getName() {
                return name;
            }

            public String getType() {
                return type;
            }
        }
    }

}



