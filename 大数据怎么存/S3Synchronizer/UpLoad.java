import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
import java.nio.file.Paths;

public class UpLoad {
	private final static String bucketName = "qingkong";
    private final static String filePath  = "F:\\\\WORKS\\Eclipse\\2021-Big-data-development-main\\大数据怎么存\\";
    private final static String accessKey = "89189D3CBF9AB0869CAF";
    private final static String secretKey = "W0Y4RkVEOEE3M0FEMTc5RTNDMjI3OTY3MEM0RUJE";
    private final static String serviceEndpoint = "http://10.16.0.1:81";
    private final static String signingRegion = "";

    public static void main(String[] args) {
        final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        final ClientConfiguration ccfg = new ClientConfiguration().
                withUseExpectContinue(false);

        final EndpointConfiguration endpoint = new EndpointConfiguration(serviceEndpoint, signingRegion);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(ccfg)
                .withEndpointConfiguration(endpoint)
                .withPathStyleAccessEnabled(true)
                .build();

        System.out.format("Uploading %s to S3 bucket %s...\n", filePath, bucketName);
        final String keyName = Paths.get(filePath).getFileName().toString();
        final File file = new File(filePath);

        for (int i = 0; i < 2; i++) {
            try {
                s3.putObject(bucketName, keyName, file);
                break;
            } catch (AmazonServiceException e) {
                if (e.getErrorCode().equalsIgnoreCase("NoSuchBucket")) {
                    s3.createBucket(bucketName);
                    continue;
                }

                System.err.println(e.toString());
                System.exit(1);
            } catch (AmazonClientException e) {
                try {

                    s3.getBucketAcl(bucketName);
                } catch (AmazonServiceException ase) {
                    if (ase.getErrorCode().equalsIgnoreCase("NoSuchBucket")) {
                        s3.createBucket(bucketName);
                        continue;
                    }
                } catch (Exception ignore) {
                }

                System.err.println(e.toString());
                System.exit(1);
            }
        }

        System.out.println("Done!");
    }
}
