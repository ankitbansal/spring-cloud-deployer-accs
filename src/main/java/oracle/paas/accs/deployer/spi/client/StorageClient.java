package oracle.paas.accs.deployer.spi.client;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

public class StorageClient {
    private String username = "StorageEval03admin.Storageadmin";
    private String password = "Welcome1";
    private String uri = "https://storage.oraclecorp.com/v1/Storage-StorageEval03admin/_apaas";

    public void pushFileToStorage(File file) {
        System.out.println("File Path :" +file.getAbsolutePath());
        System.out.println("File Name :" +file.getName());

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config).register(MultiPartFeature.class);
        WebTarget webTarget = client.target(uri + "/" +file.getName());

        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("details", file,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multiPart.bodyPart(fileDataBodyPart);

        Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", authHeader())
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        System.out.println(response.getStatus() + " "
                + response.getStatusInfo() + " " + response);
    }


    public  String authHeader() {
        byte[] encodedBytes = Base64.encodeBase64((username + ":" + password).getBytes());
        return "Basic " + new String(encodedBytes);
    }
}
