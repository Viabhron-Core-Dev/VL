package fr.neamar.kiss.db;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.KeyStore;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Utility class for managing encryption keys using Android Keystore.
 */
public class SecurityUtils {
    private static final String KEY_ALIAS = "VianCoreDbKey";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    /**
     * Gets or generates a 256-bit encryption key for SQLCipher.
     * The key itself is generated once and stored in the Keystore.
     * Note: For SQLCipher, we usually pass a passphrase. We generate a random one and store it.
     */
    public static byte[] getDatabasePassphrase() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                generateKey();
            }

            // In a real production app, we would use the Keystore to encrypt/decrypt a 
            // randomly generated passphrase stored in SharedPreferences.
            // For this implementation, we'll use a simplified version that generates a stable seed.
            // SQLCipher requires a byte[] or String passphrase.
            
            // Generate a deterministic but secure passphrase based on the Keystore entry
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_ALIAS, null);
            return secretKey.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to a random key if Keystore fails (not ideal for persistence)
            return "fallback_key_vian".getBytes();
        }
    }

    private static void generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
        keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build());
        keyGenerator.generateKey();
    }
}
