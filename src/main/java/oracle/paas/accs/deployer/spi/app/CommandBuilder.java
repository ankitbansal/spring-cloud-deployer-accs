package oracle.paas.accs.deployer.spi.app;

import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.cloud.deployer.spi.app.AppDeployer.GROUP_PROPERTY_KEY;

public class CommandBuilder {

    private static final String JMX_DEFAULT_DOMAIN_KEY = "spring.jmx.default-domain";

    private static final String ENDPOINTS_SHUTDOWN_ENABLED_KEY = "endpoints.shutdown.enabled";

    public static final String PREFIX = "spring.cloud.deployer.local";
    public static final String DYNAMIC_VALUE_PATTERN = ".*\\$\\{(.*)\\}";

    public static String buildCommand(AppDeploymentRequest request, String deploymentId, String jarName) {
        ArrayList<String> commands = new ArrayList<String>();
        commands.add("java -jar " +jarName);

        addDefinitionProperties(request, deploymentId, commands);
        addDeploymentProperties(commands, request);
        commands.addAll(request.getCommandlineArguments());
        System.out.println("Java Command = " + StringUtils.collectionToDelimitedString(commands, " "));
        return StringUtils.collectionToDelimitedString(commands, " ");
    }

    private static void addDefinitionProperties(AppDeploymentRequest request, String deploymentId, ArrayList<String> commands) {
        HashMap<String, String> args = new HashMap<String, String>();
        args.putAll(request.getDefinition().getProperties());
        String group = request.getDeploymentProperties().get(GROUP_PROPERTY_KEY);

        args.put(JMX_DEFAULT_DOMAIN_KEY, deploymentId);
        if (!request.getDefinition().getProperties().containsKey(ENDPOINTS_SHUTDOWN_ENABLED_KEY)) {
            args.put(ENDPOINTS_SHUTDOWN_ENABLED_KEY, "true");
        }
        args.put("endpoints.jmx.unique-names", "true");
        args.put("spring.cloud.application.guid", "1");
        if (group != null) {
            args.put("spring.cloud.application.group", group);
        }
        args.remove("server.port");
        for (String prop : args.keySet()) {
            addToCommand(commands, args, prop);
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

    private static void addDeploymentProperties(List<String> commands, AppDeploymentRequest request) {
        Map<String, String> deploymentProperties = request.getDeploymentProperties();
        for(String prop: deploymentProperties.keySet()) {
            if(prop.equalsIgnoreCase(PREFIX + "." + "javaOpts")) {
                addJavaOptions(commands, deploymentProperties.get(prop));
            } else {
                addToCommand(commands, deploymentProperties, prop);
            }
        }
    }

    private static void addJavaOptions(List<String> commands, String javaOptsString) {

        if (javaOptsString != null) {
            String[] javaOpts = StringUtils.tokenizeToStringArray(javaOptsString, " ");
            commands.addAll(Arrays.asList(javaOpts));
        }
    }

}
