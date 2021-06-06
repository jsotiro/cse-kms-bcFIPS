package xyz.leansecurity.crypto.bcfips;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DataKeySpec;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.kms.model.GenerateDataKeyRequest;
import software.amazon.awssdk.services.kms.model.GenerateDataKeyResponse;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class KMSDataKey implements DataKey{
    private Region region;
    private String masterKeyArnId;
    byte[] plainTextDataKey = null;
    byte[] encrtyptedDataKey = null;


    KMSDataKey(AwsCredentials credentials, Region region, String cmkId){
        this.masterKeyArnId = cmkId;
        this.region = region;
        this.kmsClient = KmsClient.builder().credentialsProvider(StaticCredentialsProvider.create(credentials)).region(region).build();
    }


    @Override
    public String getEncryptedDataKey() {
        return  Base64.getEncoder().encodeToString(this.encrtyptedDataKey);
    }

    @Override
    public byte[] getPlainTextDataKey() {
        return this.plainTextDataKey;
    }

    KmsClient kmsClient;

    @Override
    public void generateDataKey()  {
            GenerateDataKeyRequest generateDataKeyRequest = GenerateDataKeyRequest.builder().keyId(masterKeyArnId)
                    .keySpec(DataKeySpec.AES_256).build();
        GenerateDataKeyResponse generateDataKeyResponse = this.kmsClient.generateDataKey(generateDataKeyRequest);
        this.plainTextDataKey = generateDataKeyResponse.plaintext().asByteArray();
        this.encrtyptedDataKey = generateDataKeyResponse.ciphertextBlob().asByteArray();
      }


    public SdkBytes deCryptDataKey(SdkBytes encryptedObject) {
        DecryptRequest decryptRequest = DecryptRequest.builder().ciphertextBlob(encryptedObject).build();
        DecryptResponse decryptResponse = this.kmsClient.decrypt(decryptRequest);
        return decryptResponse.plaintext();
    }

    @Override
    public void fromExistingEncryptedDataKey(String base64EncDataKey) {
        this.encrtyptedDataKey = Base64.getDecoder().decode(base64EncDataKey.getBytes());
        SdkBytes dataKey = SdkBytes.fromByteArray(this.encrtyptedDataKey);
        this.plainTextDataKey = deCryptDataKey(dataKey).asByteArray();
    }
}
