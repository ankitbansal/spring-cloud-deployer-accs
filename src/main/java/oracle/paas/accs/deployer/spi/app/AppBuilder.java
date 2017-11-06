package oracle.paas.accs.deployer.spi.app;

import oracle.paas.accs.deployer.spi.client.Application;
import oracle.paas.accs.deployer.spi.util.ACCSUtil;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.cloud.deployer.spi.app.AppDeployer.GROUP_PROPERTY_KEY;

public class AppBuilder {

    private static final String JMX_DEFAULT_DOMAIN_KEY = "spring.jmx.default-domain";

    private static final String ENDPOINTS_SHUTDOWN_ENABLED_KEY = "endpoints.shutdown.enabled";

    public static final String PREFIX = "spring.cloud.deployer.local";
    public static final String DYNAMIC_VALUE_PATTERN = ".*\\$\\{(.*)\\}";
    private AppDeploymentRequest appDeploymentRequest;
    private String deploymentId;
    private Map<String, String> appDefinitionProperties = new HashMap<String, String>();

    public AppBuilder(AppDeploymentRequest appDeploymentRequest, String deploymentId) {

        this.appDeploymentRequest = appDeploymentRequest;
        this.deploymentId = deploymentId;
        initialize();
    }

    private void initialize() {
        HashMap<String, String> appDefinitionProperties = new HashMap<String, String>();
        appDefinitionProperties.putAll(appDeploymentRequest.getDefinition().getProperties());
        String group = appDeploymentRequest.getDeploymentProperties().get(GROUP_PROPERTY_KEY);

        appDefinitionProperties.put(JMX_DEFAULT_DOMAIN_KEY, deploymentId);
        if (!appDeploymentRequest.getDefinition().getProperties().containsKey(ENDPOINTS_SHUTDOWN_ENABLED_KEY)) {
            appDefinitionProperties.put(ENDPOINTS_SHUTDOWN_ENABLED_KEY, "true");
        }
        appDefinitionProperties.put("endpoints.jmx.unique-names", "true");
        appDefinitionProperties.put("spring.cloud.application.guid", "1");
        if (group != null) {
            appDefinitionProperties.put("spring.cloud.application.group", group);
        }
        appDefinitionProperties.remove("server.port");

    }

    public Application getApplicationData(String zipName) {
        String appName = ACCSUtil.getSanitizedApplicationName(deploymentId);
        Map<String, String> envVariables = new HashMap<String, String>();
        application.manifest = Application.Manifest.from(appDeploymentRequest, jarName);
        application.deployment = Application.Deployment.from(appDeploymentRequest, envVariables);
        return Application.from(appDeploymentRequest, appName, zipName, ACCSUtil.APP_RUNNER, envVariables);
    }

    public String buildCommand(String jarName) {
        ArrayList<String> commands = new ArrayList<String>();
        commands.add("java -jar " +jarName);

        addDefinitionProperties(commands);
        addDeploymentProperties(commands);
        commands.addAll(appDeploymentRequest.getCommandlineArguments());
        System.out.println("Java Command = " + StringUtils.collectionToDelimitedString(commands, " "));
        return StringUtils.collectionToDelimitedString(commands, " ");
    }

    private void addDefinitionProperties(List<String> commands) {
        for (String prop : appDefinitionProperties.keySet()) {
            addToCommand(commands, appDefinitionProperties, prop);
        }
    }

    private static void addToCommand(List<String> commands, Map<String, String> args, String prop) {
        if(isDynamicValue(args, prop)) {

            commands.add(String.format("--%s=%s", prop, replaceDynamicValue(args, prop)));
        } else {
            commands.add(String.format("--%s=%s", prop, args.get(prop)));
        }
    }

    private static String replaceDynamicValue(Map<String, String> args, String prop) {
        String value = args.get(prop);

        Pattern pattern = Pattern.compile(DYNAMIC_VALUE_PATTERN);
        Matcher matcher = pattern.matcher(args.get(prop));
        int count=1;
        while(matcher.find()) {
            String match = matcher.group(count);
            System.out.println("Match : " +match);
            if(args.get(match) != null) {
                value = value.replace("${"  + match + "}", args.get(match));
            } else {
                value = value.replace("${"  + match + "}", "");
            }
            count++;
        }
        System.out.println("Finval value : " +value);
        return value;
    }

    private static boolean isDynamicValue(Map<String, String> args, String prop) {
        Pattern pattern = Pattern.compile(DYNAMIC_VALUE_PATTERN);
        Matcher matcher = pattern.matcher(args.get(prop));

        if(matcher.find()) {
            return true;
        }

            return false;
    }

    private void addDeploymentProperties(List<String> commands) {
        Map<String, String> deploymentProperties = appDeploymentRequest.getDeploymentProperties();
        for(String prop: deploymentProperties.keySet()) {
            if(prop.equalsIgnoreCase(PREFIX + "." + "javaOpts")) {
                addJavaOptions(commands, deploymentProperties.get(prop));
            } else {
                addToCommand(commands, deploymentProperties, prop);
            }
        }
    }

    private void addJavaOptions(List<String> commands, String javaOptsString) {

        if (javaOptsString != null) {
            String[] javaOpts = StringUtils.tokenizeToStringArray(javaOptsString, " ");
            commands.addAll(Arrays.asList(javaOpts));
        }
    }

}
