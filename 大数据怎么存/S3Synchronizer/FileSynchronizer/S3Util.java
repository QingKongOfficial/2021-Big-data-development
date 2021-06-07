package FileSynchronizer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

 class S3Util {

	private final static String bucketName = "qingkong";
	private final static String accessKey = "89189D3CBF9AB0869CAF";
	private final static String secretKey = "W0Y4RkVEOEE3M0FEMTc5RTNDMjI3OTY3MEM0RUJE";
	private final static String serviceEndpoint = "http://10.16.0.1:81";
	private final static String signingRegion = "";
    private final static BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    private final static ClientConfiguration ccfg = new ClientConfiguration().withUseExpectContinue(false);
    private final static AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, signingRegion);
    private final static AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withClientConfiguration(ccfg)
            .withEndpointConfiguration(endpoint)
            .withPathStyleAccessEnabled(true)
            .build();

    public static Boolean UpLoadFile(String filePath) {
        final String keyName = Paths.get(filePath).getFileName().toString();
        final File file = new File(filePath);
        if(file.isDirectory()){
            System.out.println("new folder "+keyName+" ,uploading");
            ObjectMetadata Metadata=new ObjectMetadata();
            Metadata.setContentLength(0);
            InputStream empty=new ByteArrayInputStream(new byte[0]);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,keyName+"/",empty,Metadata);
            s3.putObject(putObjectRequest );
            System.out.println("successful create in" + bucketName+ "with" +keyName);
            return true;
        }
        System.out.println("begin to upload to " + bucketName + keyName);
        if(file.length() > 20 << 20) {
            return MultipartUpLoad(keyName, file);
        }else {
            try {
                s3.putObject(bucketName, keyName, file);

            } catch (AmazonServiceException e) {
                if (e.getErrorCode().equalsIgnoreCase("cant find the bucket " + bucketName)) {
                    s3.createBucket(bucketName);
                }

                System.err.println(e.toString());
       
                return false;
            } catch (AmazonClientException e) {
                try {
                    s3.getBucketAcl(bucketName);
                } catch (AmazonServiceException ase) {
                    if (ase.getErrorCode().equalsIgnoreCase("cant find the bucket " + bucketName)) {
                        s3.createBucket(bucketName);
                    }
                } catch (Exception ignore) {
                }

                System.err.println(e.toString());
                return false;
            }
        }
        return true;
    }

    public static Boolean MultipartUpLoad(String keyName,File file){
        long partSize = 5 << 20;
        ArrayList<PartETag> partETags = new ArrayList<PartETag>();
        long contentLength = file.length();
        String uploadId = null;
        try {
            InitiateMultipartUploadRequest initRequest =
                    new InitiateMultipartUploadRequest(bucketName, keyName);
            uploadId = s3.initiateMultipartUpload(initRequest).getUploadId();
            System.out.println("upload ID:"+uploadId);

            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++){
                partSize = Math.min(partSize, contentLength - filePosition);


                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(keyName)
                        .withUploadId(uploadId)
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);

                System.out.println("uploading"+i+"part");
                partETags.add(s3.uploadPart(uploadRequest).getPartETag());

                filePosition += partSize;
            }

            CompleteMultipartUploadRequest compRequest =
                    new CompleteMultipartUploadRequest(bucketName, keyName, uploadId, partETags);

            s3.completeMultipartUpload(compRequest);

        } catch (Exception e) {
            System.err.println(e.toString());
            if (uploadId != null && !uploadId.isEmpty()) {
                System.out.println("Upload interrupt");
                s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, keyName, uploadId));
            }
         
            return false;
        }
        return true;
    }
    public static Boolean DeleteFile(String filePath) {
        final String keyName = Paths.get(filePath).getFileName().toString();
        System.out.println("from S3 "+bucketName+" delete "+keyName);
        try {
            s3.deleteObject(bucketName, keyName);
        } catch (AmazonServiceException e) {
            try {
                s3.getBucketAcl(bucketName);
            } catch (AmazonServiceException ase) {
                if (ase.getErrorCode().equalsIgnoreCase("cant find the bucket "+bucketName)) {
                    s3.createBucket(bucketName);
                }
            } catch (Exception ignore) { }
            System.err.println(e.toString());
   
            return false;
        }
        return true;
    }

    public static Boolean DownLoadFile(String filePath) {
        ListObjectsV2Result result = s3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();

        S3ObjectInputStream s3is = null;
        FileOutputStream fos = null;
        FileInputStream fis=null;
        String keyName = null;
        System.out.println("Synchronizing");
        for (S3ObjectSummary object : objects) {
            keyName = object.getKey();
            System.out.println("开始同步" + keyName);
            File file = new File(filePath + keyName);
            try {
                S3Object o = s3.getObject(bucketName, keyName);
                s3is = o.getObjectContent();
                if(keyName.endsWith("/")){
                    if(!file.exists()){
                        file.mkdir();
                    }
                    continue;
                }
                ObjectMetadata oMetaData = s3.getObjectMetadata(bucketName, keyName);
                final long contentLength = oMetaData.getContentLength();
                if(file.exists()){
                    fis=new FileInputStream(file);
                    if(file.length()==contentLength&& DigestUtils.md5Hex(fis).equals(DigestUtils.md5Hex(s3is))) {
                        System.out.println("file" + filePath + keyName + "is latest");
                        continue;
                    }
                    else{
                        int i=1;
                        String[] param=keyName.split("\\.");
                        String fileName;
                        do{
                            fileName=param[0]+"("+ i++ +").";
                            for(int j=1;j<param.length;j++){
                                fileName+=param[j];
                            }
                            file = new File(filePath + fileName);
                        }while(file.exists());
                        keyName=fileName;
                    }
                }
                fos = new FileOutputStream(new File(filePath+keyName));
                if(contentLength> 5<<20){
                    if( MultipartDownLoad(filePath, keyName, contentLength, s3is, fos, o))
                        System.out.println("file "+filePath+keyName+" Synchronized successful");
                    else
                        System.out.println("file "+filePath+keyName+" Synchronized failed");
                }else{
                    byte[] read_buf = new byte[1024 * 1024];
                    int read_len = 0;
                    while ((read_len = s3is.read(read_buf)) > 0) {
                        fos.write(read_buf, 0, read_len);
                    }
                    System.out.println("file "+filePath+keyName+" Synchronized successful");
                }
            } catch (AmazonServiceException e) {
                System.err.println(e.toString());
          
                return false;
            } catch (IOException e) {
                System.err.println(e.getMessage());
         
                return false;
            } finally {
                if (s3is != null) try { s3is.close(); } catch (IOException e) { }
                if (fos != null) try { fos.close(); } catch (IOException e) { }
                if (fis !=null) try { fis.close(); } catch (IOException e) {}

            }
        }
        System.out.println(" Synchronized successful");
        return true;
    }
    public static Boolean MultipartDownLoad(String filePath,String keyName, long contentLength, S3ObjectInputStream s3is, FileOutputStream fos,S3Object o){
        try {
            final GetObjectRequest downloadRequest =
                    new GetObjectRequest(bucketName, keyName);
            long partSize=5<<20;

            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                partSize = Math.min(partSize, contentLength - filePosition);

                downloadRequest.setRange(filePosition, filePosition + partSize);
                o = s3.getObject(downloadRequest);

                System.out.println("downloading"+i+"part");

                filePosition += partSize+1;
                s3is = o.getObjectContent();
                byte[] read_buf = new byte[1024 * 1024];
                int read_len = 0;
                while ((read_len = s3is.read(read_buf)) > 0) {
                    fos.write(read_buf, 0, read_len);
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());

    
            return false;
        }
        return true;
    }

}
