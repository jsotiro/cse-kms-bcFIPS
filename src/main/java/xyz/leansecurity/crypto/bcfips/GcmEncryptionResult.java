package xyz.leansecurity.crypto.bcfips;

import java.security.AlgorithmParameters;

public class GcmEncryptionResult {
    private String base64EnryptedKey;
    private AlgorithmParameters gcmParamaters;
    private byte[] cipherText;

    public String getBase64EnryptedKey() {
        return base64EnryptedKey;
    }

    public AlgorithmParameters getGcmParamaters() {
        return gcmParamaters;
    }

    public byte[] getCipherText() {
        return cipherText;
    }

    public GcmEncryptionResult(String base64EnryptedKey, AlgorithmParameters gcmParamaters, byte[] cipherText) {
        this.base64EnryptedKey = base64EnryptedKey;
        this.gcmParamaters = gcmParamaters;
        this.cipherText = cipherText;

    }
}
