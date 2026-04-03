package fr.neamar.kiss.security;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

/**
 * Helper class to handle biometric and device credential authentication.
 */
public class BiometricHelper {

    public interface AuthCallback {
        void onAuthSuccess();
        void onAuthError(String error);
    }

    /**
     * Shows the biometric prompt for app unlocking.
     */
    public static void authenticate(@NonNull FragmentActivity activity, @NonNull String appName, @NonNull AuthCallback callback) {
        Executor executor = ContextCompat.getMainExecutor(activity);
        
        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                callback.onAuthError(errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callback.onAuthSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // This is called when biometric is recognized but not valid (e.g. wrong finger)
                // The system UI usually handles this, but we can log it.
            }
        });

        BiometricPrompt.PromptInfo.Builder promptBuilder = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock " + appName)
                .setSubtitle("Authenticate to launch the app")
                .setConfirmationRequired(false);

        // Use BIOMETRIC_STRONG and DEVICE_CREDENTIAL (PIN/Pattern/Password)
        // Note: setNegativeButtonText cannot be used if DEVICE_CREDENTIAL is allowed
        int authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG 
                           | BiometricManager.Authenticators.DEVICE_CREDENTIAL;
        
        promptBuilder.setAllowedAuthenticators(authenticators);

        try {
            biometricPrompt.authenticate(promptBuilder.build());
        } catch (Exception e) {
            callback.onAuthError("Authentication failed to start: " + e.getMessage());
        }
    }

    /**
     * Checks if the device is capable of authentication.
     */
    public static boolean canAuthenticate(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        int result = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG 
                                                    | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
        return result == BiometricManager.BIOMETRIC_SUCCESS;
    }
}
