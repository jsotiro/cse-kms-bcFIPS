package xyz.leansecurity.crypto.bcfips;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import org.bouncycastle.crypto.CryptoServicesRegistrar;

import org.bouncycastle.crypto.EntropySourceProvider;
import org.bouncycastle.crypto.fips.FipsDRBG;
import org.bouncycastle.crypto.util.BasicEntropySourceProvider;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class FipsCse {

    DataKey dataKey;


    public DataKey dataKey() {
        return dataKey;
    }

    public void setDataKey(DataKey value) {
        this.dataKey = value;
    }

    public  GcmEncryptionResult gcmEncrypt(byte[] data)
            throws GeneralSecurityException
    {
        dataKey.generateDataKey();
        byte[] keyRaw = dataKey.getPlainTextDataKey();
        SecretKey key = new SecretKeySpec(keyRaw, 0,keyRaw.length, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BCFIPS");
        cipher.init(Cipher.ENCRYPT_MODE, key, // taglength, nonce
                new GCMParameterSpec(128, Hex.decode("000102030405060708090a0b")));
        GcmEncryptionResult result = new GcmEncryptionResult( dataKey.getEncryptedDataKey(), cipher.getParameters(), cipher.doFinal(data));
        return result;
    }

    public  byte[] gcmDecrypt(String encryptedDataKey, AlgorithmParameters gcmParameters, byte[] cipherText)
            throws GeneralSecurityException
    {

        dataKey.fromExistingEncryptedDataKey(encryptedDataKey);
        byte[] keyRaw = dataKey.getPlainTextDataKey();
        SecretKey key =  new SecretKeySpec(keyRaw, 0, keyRaw.length, "AES");;
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BCFIPS");
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameters);
        return cipher.doFinal(cipherText);
    }

}
