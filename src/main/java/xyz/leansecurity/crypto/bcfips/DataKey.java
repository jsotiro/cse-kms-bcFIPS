package xyz.leansecurity.crypto.bcfips;

public interface DataKey {
    String  getEncryptedDataKey();
    byte[] getPlainTextDataKey();
    void generateDataKey();
    void fromExistingEncryptedDataKey(String base64EncDataKey);
}

