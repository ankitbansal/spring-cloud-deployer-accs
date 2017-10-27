package oracle.paas.accs.deployer.spi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ACCSUtil {

    public static String getSanitizedApplicationName(String name) {
       return name.replaceAll("[^A-Za-z0-9]", "");
    }

    public static File convertToZipFile(File file) {
        if(file.exists()) {
            String zipName = file.getName().replace(".jar", ".zip");
            ZipOutputStream zos = null;
            try {
                String name = file.getName();
                zos = new ZipOutputStream(new FileOutputStream(zipName));

                ZipEntry entry = new ZipEntry(name);
                zos.putNextEntry(entry);

                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    byte[] byteBuffer = new byte[1024];
                    int bytesRead = -1;
                    while ((bytesRead = fis.read(byteBuffer)) != -1) {
                        zos.write(byteBuffer, 0, bytesRead);
                    }
                } finally {
                    try {
                        fis.close();
                    } catch (Exception e) {
                    }
                }
                zos.closeEntry();
            } catch(Exception e) {
                System.out.println("Unable to create zip : " +e.getMessage());
                throw new RuntimeException(e);
            }finally {
                try {
                    zos.close();
                } catch (Exception e) {
                }
            }
            return new File(zipName);
        }
        return null;
    }
}

