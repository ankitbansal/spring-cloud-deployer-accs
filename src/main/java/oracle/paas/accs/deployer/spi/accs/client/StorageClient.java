package oracle.paas.accs.deployer.spi.accs.client;

import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Map;

public class StorageClient {
    private String username = "ramya.paruchuri@oracle.com";
    private String password = "Welc0me1#";
    private String uri = "https://Storage-dea2325c3b1f45f2b9c8861188087b23.dv1.opcstorage.com/v1/Storage-dea2325c3b1f45f2b9c8861188087b23/_apaas";

    public StorageClient(String username, String password, String uri) {
        this.username = username;
        this.password = password;
        this.uri = uri;
    }

    public String pushFileToStorage(File file) {
        System.out.println("File Path :" +file.getAbsolutePath());
        System.out.println("File Name :" +file.getName());

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config).register(MultiPartFeature.class);
        String storagePath = uri + "/" + file.getName();
        WebTarget webTarget = client.target(storagePath);

        Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", authHeader())
                .put(Entity.entity(file, "application/zip"));

        System.out.println(response.getStatus() + " "
                + response.getStatusInfo() + " " + response);
        return storagePath;
    }


    private  String authHeader() {
        byte[] encodedBytes = Base64.encodeBase64((username + ":" + password).getBytes());
        return "Basic " + new String(encodedBytes);
    }
}
