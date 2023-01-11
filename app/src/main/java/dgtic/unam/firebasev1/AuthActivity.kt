package dgtic.unam.firebasev1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN=100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val bundle= Bundle()
        bundle.putString("message","Integración de Firebase completa")


        analytics.logEvent("InitScreen",bundle)

        //Setup
        setup()
        session()
    }
    fun onSart(){
        super.onStart()
        val authLayout=findViewById<LinearLayout>(R.id.authlayout)
        authLayout.visibility=View.VISIBLE
    }
    private fun session() {
        val prefs: SharedPreferences? = getSharedPreferences(getString(R.string.prefs_file),
            Context.MODE_PRIVATE)
        val email:String?=prefs?.getString("email",null)
        val provider:String?=prefs?.getString("provider",null)
        val authLayout=findViewById<LinearLayout>(R.id.authlayout)
        if(email!=null && provider !=null){
            authLayout.visibility= View.INVISIBLE
            showHome(email,ProviderType.valueOf(provider))
        }

    }

    private fun setup() {
        title = "Autenticación"

        val SignupBtn = findViewById<Button>(R.id.sigub)
        val emailEditText =findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginBtn = findViewById<Button>(R.id.logInButton)

        SignupBtn.setOnClickListener{
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() ){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        showHome(it.result.user?.email?:"",ProviderType.BASIC)
                    } else{
                        showAlert()
                    }
                }
            }
        }

        loginBtn.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty() ){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        showHome(it.result.user?.email?:"",ProviderType.BASIC)
                    } else{
                        showAlert()
                    }
                }
            }

        }


    val googleButton=findViewById<Button>(R.id.GoogleButton)
    googleButton.setOnClickListener {
        // Configuracion
        val googleConf :GoogleSignInOptions=
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleClient:GoogleSignInClient = GoogleSignIn.getClient(this,googleConf)
        googleClient.signOut()
        startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)

    }


    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog:AlertDialog=builder.create()
        dialog.show()

    }

    private fun showHome(email :String,provider:ProviderType){
        val homeIntent = Intent(this,HomeActivity::class.java ).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)

        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==GOOGLE_SIGN_IN){
            val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account:GoogleSignInAccount? = task.getResult(ApiException::class.java)

                if(account != null) {
                    val credential:AuthCredential=GoogleAuthProvider.getCredential(account.idToken,null )
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            showHome(account.email?:"",ProviderType.GOOGLE)
                        } else{
                            showAlert()
                        }
                    }
                }

            } catch (e:ApiException){
                showAlert()
            }
        }
    }
}