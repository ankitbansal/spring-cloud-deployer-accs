package oracle.paas.accs.deployer.spi.accs.model;

public class ApplicationStatus {
    private String identityDomain;
    private String name;
    private String status;
    private String type;
    private String currentOngoingActivity;
    private String webURL;
    private String[] message;

    public String getIdentityDomain() {
        return identityDomain;
    }

    public void setIdentityDomain(String identityDomain) {
        this.identityDomain = identityDomain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrentOngoingActivity() {
        return currentOngoingActivity;
    }

    public void setCurrentOngoingActivity(String currentOngoingActivity) {
        this.currentOngoingActivity = currentOngoingActivity;
    }

    public String getWebURL() {
        return webURL;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    public String[] getMessage() {
        return message;
    }

    public void setMessage(String[] message) {
        this.message = message;
    }
}
