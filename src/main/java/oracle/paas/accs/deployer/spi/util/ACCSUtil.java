package oracle.paas.accs.deployer.spi.util;

public class ACCSUtil {

    public static String getSanitizedApplicationName(String name) {
       return name.replaceAll("[^A-Za-z0-9]", "");
    }
}

