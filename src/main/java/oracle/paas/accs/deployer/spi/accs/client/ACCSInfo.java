package oracle.paas.accs.deployer.spi.accs.client;

public class ACCSInfo {
    private String platformType = "ACCS";
    private String version = "17.4.6";
    private String apiVersion = "17.4.6";
    private String hostVersion = "17.4.6";
    private String endPoint = "http://slc06cig.us.oracle.com:7001";

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getHostVersion() {
        return hostVersion;
    }

    public void setHostVersion(String hostVersion) {
        this.hostVersion = hostVersion;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
}
