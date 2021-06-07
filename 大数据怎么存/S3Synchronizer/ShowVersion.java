import java.text.SimpleDateFormat;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;

public class ShowVersion {
	private final static String bucketName = "qingkong";
    private final static String keyName   = "F:\\\\WORKS\\Eclipse\\2021-Big-data-development-main\\大数据怎么存\\hello.txt";
    private final static String accessKey = "89189D3CBF9AB0869CAF";
    private final static String secretKey = "W0Y4RkVEOEE3M0FEMTc5RTNDMjI3OTY3MEM0RUJE";
    private final static String serviceEndpoint = "http://10.16.0.1:81";
    private final static String signingRegion = "";
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

        ListVersionsRequest listVersionsRequest = new ListVersionsRequest()
                .withBucketName(bucketName)
                .withMaxResults(200);
        SimpleDateFormat dateFormate=new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
        try {
            s3.listVersions(listVersionsRequest);
            VersionListing versionListing = s3.listVersions(listVersionsRequest);
            int numVersions = 1;
            System.out.println("Key\tLastModified\tSize\tVersizeId");
            while (true) {
                for (S3VersionSummary objectSummary : versionListing.getVersionSummaries()) {
                    if (!objectSummary.getKey().equals(keyName)) {
                        continue;
                    }
                    System.out.format("'revision #:%d'\t'%s'\t%d\t'%s'\n",
                            numVersions,
                            dateFormate.format(objectSummary.getLastModified()),
                            objectSummary.getSize(),
                            objectSummary.getVersionId()
                    );
                    numVersions++;
                }
                if (versionListing.isTruncated()) {
                    versionListing = s3.listNextBatchOfVersions(versionListing);
                } else {
                    break;
                }
            }
        }
        catch (AmazonServiceException e) {
            System.err.println(e.toString());
            System.exit(1);
        } catch (SdkClientException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
