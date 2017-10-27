package oracle.paas.accs.deployer.spi.util;

import oracle.paas.accs.deployer.spi.app.ACCSAppDeployer;
import oracle.paas.accs.deployer.spi.app.ACCSTaskLauncher;
import oracle.paas.accs.deployer.spi.client.ACCSInfo;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.core.RuntimeEnvironmentInfo;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.deployer.spi.util.RuntimeVersionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@EnableConfigurationProperties
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class ACCSAutoConfiguration {

    private RuntimeEnvironmentInfo runtime(Class spiClass, Class implementationClass) {
        ACCSInfo client = new ACCSInfo();
        return new RuntimeEnvironmentInfo.Builder()
                .implementationName(implementationClass.getSimpleName())
                .spiClass(spiClass)
                .implementationVersion(RuntimeVersionUtils.getVersion(ACCSAppDeployer.class))
                .platformType(client.getPlatformType())
                .platformClientVersion(client.getVersion())
                .platformApiVersion(client.getApiVersion())
                .platformHostVersion(client.getHostVersion())
                .addPlatformSpecificInfo("API Endpoint", client.getEndPoint())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(TaskLauncher.class)
    public TaskLauncher taskLauncher() {
        return new ACCSTaskLauncher(runtime(TaskLauncher.class, ACCSTaskLauncher.class));
    }

    @Bean
    @ConditionalOnMissingBean(AppDeployer.class)
    public AppDeployer appDeployer() {
        return new ACCSAppDeployer(runtime(AppDeployer.class, ACCSAppDeployer.class));
    }
}
