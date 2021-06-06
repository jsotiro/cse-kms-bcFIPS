package xyz.leansecurity.crypto.bcfips;

import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Base64;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class FipsCseClient {
    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(FipsCseClient.class);
        Security.addProvider(new BouncyCastleFipsProvider());
        logger.info("Initialising BouncyCastle FIPS-certified Provider");
        boolean bStrictMode = CryptoServicesRegistrar.isInApprovedOnlyMode();
        logger.info("Approved Algorithms Mode Only Enabled:" + bStrictMode);
        Map<String, String> env = System.getenv();
        String regionId = env.get("AWS_REGION");
        Region kmsRegion = Region.of(regionId);
        String cmkArnId = env.get("AWS_KMS_CMK_ARN");
        ProfileCredentialsProvider provider = ProfileCredentialsProvider.create();
        AwsCredentials awsCreds = provider.resolveCredentials();
        logger.info("Creating a KMS Client for Data Key Management");
        DataKey kmsDatakey = new KMSDataKey(awsCreds, kmsRegion,cmkArnId);
        FipsCse cse = new FipsCse();
        cse.setDataKey(kmsDatakey);
        logger.info("Encrypting plaintext provided in CLI paramteter:"+args[0]);
        byte[] plaintext = args[0].getBytes();
        try {
            GcmEncryptionResult encryptResult = cse.gcmEncrypt(plaintext);
            logger.info("Plaintext Encrypted - Base64 Mode:"+ Base64.getEncoder().encodeToString(encryptResult.getCipherText()));
            logger.info("Data Key Encrypted  - Base64 Mode:"+ encryptResult.getBase64EnryptedKey());
            logger.info("Decrypting ciphertext");
            byte[] decrypted = cse.gcmDecrypt( encryptResult.getBase64EnryptedKey(),
                                                encryptResult.getGcmParamaters(),
                                                encryptResult.getCipherText());
            String s = new String(decrypted, StandardCharsets.UTF_8);
            logger.info("Decrypted Plaintext: "+ s);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }


    }
}
