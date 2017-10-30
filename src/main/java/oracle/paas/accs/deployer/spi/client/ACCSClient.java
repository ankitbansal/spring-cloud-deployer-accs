package oracle.paas.accs.deployer.spi.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;

import static oracle.paas.accs.deployer.spi.util.GsonUtil.gson;

public class ACCSClient {
    private final static String username = "weblogic";
    private final static String password = "welcome1";
    private final static String identityDomain = "apaasuser";
    public final static String uri="http://slc06cig.us.oracle.com:7001/paas/service/apaas/api/v1.1/apps";

    public void createApplication(Application application) {
        System.out.println("Inside createApplication");
        Client client = getClient();
        Response response = null;
        InputStream is = null;
        try {
            FormDataMultiPart uploadform = new FormDataMultiPart();
            uploadform.field("name", application.getName());
            uploadform.field("runtime", application.getRuntime());
            uploadform.field("notes", application.getNotes());
            uploadform.field("subscription", application.getSubscription());
            uploadform.field("archiveURL", application.getArchiveURL());
            uploadform.field("archiveFileName", application.getArchiveFileName());

            if (application.getManifest() != null) {
                System.out.println("Upload manifest.json file");

                File manifestFile = new File("manifest.json");
                FileUtils.writeStringToFile(manifestFile, gson().toJson(application.getManifest()));
                uploadform.bodyPart(new FileDataBodyPart("manifest", manifestFile, MediaType.APPLICATION_OCTET_STREAM_TYPE));
            }
            if (application.getDeployment() != null) {
                System.out.println("Upload deployment.json file.");
                File deploymentFile = new File("deployment.json");
                FileUtils.writeStringToFile(deploymentFile, gson().toJson(application.getDeployment()));
                uploadform.bodyPart(new FileDataBodyPart("deployment", deploymentFile, MediaType.APPLICATION_OCTET_STREAM_TYPE));
            }


            WebTarget webTarget = client.target(uri + "/" + identityDomain);
            response = webTarget.request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", authHeader())
                    .header("X-ID-TENANT-NAME", identityDomain)
                    .post(Entity.entity(uploadform, MediaType.MULTIPART_FORM_DATA_TYPE), Response.class);
            System.out.println(response.getStatus() + " "
                    + response.getStatusInfo() + " " + response);
            if (!response.getStatusInfo().toString().equals(Response.Status.ACCEPTED.toString())) {
                String outputString = response.readEntity(String.class);
                System.out.println("Post Application provisioning failed with status: " +
                        response.getStatusInfo() +
                        " and message: " + outputString);
            }
        } catch (Exception je) {
            System.out.println("Application creation failed :" + je.getMessage());
            je.printStackTrace();
            throw new RuntimeException(je);
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (Exception ex) {
                System.out.println("Exception while cleaning up" + ex.getMessage());
            }
        }
    }

    private  String authHeader() {
        byte[] encodedBytes = Base64.encodeBase64((username + ":" + password).getBytes());
        return "Basic " + new String(encodedBytes);
    }

    private Client getClient() {
        final ClientConfig config = new ClientConfig().register(JacksonJsonProvider.class);
        Client client = ClientBuilder.newClient(config).register(MultiPartFeature.class);

//        config.connectorProvider(new ApacheConnectorProvider());
//        config.property(ClientProperties.PROXY_URI, "http://www-proxy.us.oracle.com:80");
        return client;
    }
}
