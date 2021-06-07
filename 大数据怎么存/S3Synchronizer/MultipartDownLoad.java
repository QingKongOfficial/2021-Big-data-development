import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class MultipartDownLoad {
	private final static String bucketName = "qingkong";
    private final static String keyName   = "F:\\WORKS\\Eclipse\\2021-Big-data-development-main\\大数据怎么存\\hello.txt";
    private final static String savePath  = "F:\\\\WORKS\\Eclipse\\2021-Big-data-development-main\\大数据怎么存\\";
    private final static String accessKey = "89189D3CBF9AB0869CAF";
    private final static String secretKey = "W0Y4RkVEOEE3M0FEMTc5RTNDMjI3OTY3MEM0RUJE";
    private final static String serviceEndpoint = "http://10.16.0.1:81";
    private final static String signingRegion = "";
    private static long partSize = 5 << 20;
    public static void main(String[] args) {
        final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
        final ClientConfiguration ccfg = new ClientConfiguration().
                withUseExpectContinue(true);

        final EndpointConfiguration endpoint =
                new EndpointConfiguration(serviceEndpoint, signingRegion);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(ccfg)
                .withEndpointConfiguration(endpoint)
                .withPathStyleAccessEnabled(true)
                .build();

        final String filePath = Paths.get(savePath, keyName).toString();

        File file = new File(filePath);

        S3Object o = null;

        S3ObjectInputStream s3is = null;
        FileOutputStream fos = null;

        try {
  
            ObjectMetadata oMetaData = s3.getObjectMetadata(bucketName, keyName);
            final long contentLength = oMetaData.getContentLength();
            final GetObjectRequest downloadRequest =
                    new GetObjectRequest(bucketName, keyName);

            fos = new FileOutputStream(file);

            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {

                partSize = Math.min(partSize, contentLength - filePosition);

                downloadRequest.setRange(filePosition, filePosition + partSize);
                o = s3.getObject(downloadRequest);

                System.out.format("Downloading part %d\n", i);

                filePosition += partSize+1;
                s3is = o.getObjectContent();
                byte[] read_buf = new byte[64 * 1024];
                int read_len = 0;
                while ((read_len = s3is.read(read_buf)) > 0) {
                    fos.write(read_buf, 0, read_len);
                }
            }

            System.out.println("Completing download");

            System.out.format("save %s to %s\n", keyName, filePath);
        } catch (Exception e) {
            System.err.println(e.toString());

            System.exit(1);
        } finally {
            if (s3is != null) try { s3is.close(); } catch (IOException e) { }
            if (fos != null) try { fos.close(); } catch (IOException e) { }
        }
        System.out.println("Done!");
    }
}

