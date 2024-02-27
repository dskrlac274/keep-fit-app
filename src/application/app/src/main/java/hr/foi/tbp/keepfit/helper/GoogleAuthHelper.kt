package hr.foi.tbp.keepfit.helper

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleAuthHelper {
    companion object {
        fun getGoogleSignInClient(context: Context): GoogleSignInClient {
            val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("880290460326-9ki1llmtugv1ttlpvotl8s8fvv9890u8.apps.googleusercontent.com")
                .build()

            return GoogleSignIn.getClient(context, signInOptions)
        }
    }
}