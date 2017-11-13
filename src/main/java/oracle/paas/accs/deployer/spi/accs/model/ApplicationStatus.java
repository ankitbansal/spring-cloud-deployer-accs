package oracle.paas.accs.deployer.spi.accs.model;

public class ApplicationStatus {
    private String identityDomain;
    private String name;
    private String status;
    private String type;
    private String currentOngoingActivity;
    private String webURL;
    private String[] message;
    private LastestDeployment lastestDeployment;

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

    public LastestDeployment getLastestDeployment() {
        return lastestDeployment;
    }

    public void setLastestDeployment(LastestDeployment lastestDeployment) {
        this.lastestDeployment = lastestDeployment;
    }

    public boolean isLastDeploymentFailed() {
        if(this.getLastestDeployment() == null || this.getLastestDeployment().getDeploymentStatus() == null) {
            return false;
        }
        String deploymentStatus = this.getLastestDeployment().getDeploymentStatus();
        if(!deploymentStatus.equalsIgnoreCase("ERROR") && !deploymentStatus.equalsIgnoreCase("FAILED")) {
            return false;
        }

        return true;
    }

    public static class LastestDeployment {
        private String deploymentId;
        private String deploymentStatus;

        public String getDeploymentId() {
            return deploymentId;
        }

        public void setDeploymentId(String deploymentId) {
            this.deploymentId = deploymentId;
        }

        public String getDeploymentStatus() {
            return deploymentStatus;
        }

        public void setDeploymentStatus(String deploymentStatus) {
            this.deploymentStatus = deploymentStatus;
        }
    }
}
