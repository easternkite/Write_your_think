package com.multimedia.writeyourthink.ui.login

import androidx.credentials.GetCredentialRequest
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.credentials.CredentialManager
import androidx.navigation.NavHostController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.multimedia.writeyourthink.R
import com.multimedia.writeyourthink.Util.handleAccessTokenForLogin
import com.multimedia.writeyourthink.Util.showFailToast
import kotlinx.coroutines.launch

private val roundedRectangle = RoundedCornerShape(5.dp)
const val ROUTE_LOGIN = "route_login"

fun NavHostController.navigateToLogin() {
    navigate(ROUTE_LOGIN)
}

@Composable
fun LoginScreen(
    onNavigateToDiary: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val background = painterResource(id = R.drawable.write_your_think_1)
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = background,
            contentDescription = "background",
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GoogleSignInButton(onNavigateToDiary = onNavigateToDiary)
            Spacer(modifier = Modifier.height(10.dp))
            FacebookLoginButton(onNavigateToDiary = onNavigateToDiary)
        }
    }

}

@Composable
fun GoogleSignInButton(
    onNavigateToDiary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val googleButton = ImageVector.vectorResource(id = R.drawable.ic_google_sign_in)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val request = remember {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()

       GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            onNavigateToDiary()
        }
    }

    Surface(
        modifier = modifier
            .width(193.dp)
            .height(40.dp)
            .clickable {
                scope.launch {
                    val result =
                        credentialManager.getCredential(request = request, context = context)
                    val credential = result.credential

                    val googleIdTokenCredential = runCatching {
                        GoogleIdTokenCredential.createFrom(credential.data)
                    }
                        .onFailure { it.printStackTrace() }
                        .getOrNull() ?: return@launch

                    val googleIdToken = googleIdTokenCredential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val auth = FirebaseAuth.getInstance()
                    auth.handleAccessTokenForLogin(
                        credential = firebaseCredential,
                        onLoginComplete = onNavigateToDiary,
                        onLoginFailed = context::showFailToast
                    )
                }
            },
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = roundedRectangle
    ) {
        Image(
            imageVector = googleButton,
            contentScale = ContentScale.Crop,
            contentDescription = "Sign in with google"
        )
    }

}

@Composable
fun FacebookLoginButton(
    onNavigateToDiary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tag = "FacebookLoginButton"
    val context = LocalContext.current
    val fbBlue = colorResource(id = com.facebook.common.R.color.com_facebook_blue)
    val isPreview = LocalInspectionMode.current
    val callback = remember {
        object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(tag, "login success")
                val auth = FirebaseAuth.getInstance()
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                auth.handleAccessTokenForLogin(
                    credential = credential,
                    onLoginComplete = onNavigateToDiary,
                    onLoginFailed = context::showFailToast
                )
            }

            override fun onCancel() {
                Log.d(tag, "login canceled")
            }

            override fun onError(error: FacebookException) {
                error.printStackTrace()
                Log.d(tag, "login failed")
            }
        }
    }
    Surface(
        modifier = modifier.height(40.dp),
        color = fbBlue,
        shape = roundedRectangle
    ) {
        AndroidView(
            factory = {
                val cm = CallbackManager.Factory.create()
                LoginButton(it).also { button ->
                    if (isPreview) return@also
                    button.registerCallback(cm, callback)
                }
            },
            update = { it.setPermissions(listOf("email", "public_profile")) }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun GoogleSignInButtonPreview() {
    GoogleSignInButton(onNavigateToDiary = {})
}

@Composable
@Preview(showBackground = true)
fun FacebookLoginButtonPreview() {
    FacebookLoginButton(onNavigateToDiary = {})
}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateToDiary = {}
    )
}