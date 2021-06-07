import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;

public class MultipartUpload {
    private final static String bucketName = "qingkong";
    private final static String filePath   ="F:\\\\WORKS\\Eclipse\\2021-Big-data-development-main\\大数据怎么存\\";
    private final static String accessKey = "89189D3CBF9AB0869CAF";
    private final static String secretKey = "W0Y4RkVEOEE3M0FEMTc5RTNDMjI3OTY3MEM0RUJE";
    private final static String serviceEndpoint = "http://10.16.0.1:81";
    private final static String signingRegion = "";
    private static long partSize = 5 << 20;

    public static void main(String[] args) {
        final BasicAWSCredentials credentials =
                new BasicAWSCredentials(accessKey,secretKey);
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

        String keyName = Paths.get(filePath).getFileName().toString();

        ArrayList<PartETag> partETags = new ArrayList<PartETag>();
        File file = new File(filePath);
        long contentLength = file.length();
        String uploadId = null;

        try {

            InitiateMultipartUploadRequest initRequest =
                    new InitiateMultipartUploadRequest(bucketName, keyName);
            uploadId = s3.initiateMultipartUpload(initRequest).getUploadId();
            System.out.format("Created upload ID was %s\n", uploadId);


            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {

                partSize = Math.min(partSize, contentLength - filePosition);

                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(keyName)
                        .withUploadId(uploadId)
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);

                System.out.format("Uploading part %d\n", i);
                partETags.add(s3.uploadPart(uploadRequest).getPartETag());

                filePosition += partSize;
            }

            System.out.println("Completing upload");
            CompleteMultipartUploadRequest compRequest =
                    new CompleteMultipartUploadRequest(bucketName, keyName, uploadId, partETags);

            s3.completeMultipartUpload(compRequest);
        } catch (Exception e) {
            System.err.println(e.toString());
            if (uploadId != null && !uploadId.isEmpty()) {

                System.out.println("Aborting upload");
                s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, keyName, uploadId));
            }
            System.exit(1);
        }
        System.out.println("Done!");

    }
}
