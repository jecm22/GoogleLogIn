package dgtic.unam.firebasev1

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC,
    GOOGLE
}
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bundle :Bundle? = intent.extras
        val email:String? = bundle?.getString("email")
        val provider:String?= bundle?.getString("provider")
        //Setup
        setup(email?:"",provider?:"")

        //Guardado de datos
        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()





    }

    private fun setup(email:String,provider:String) {
        var tittle = "Inicio"
        val emailTextView=findViewById<TextView>(R.id.emailtextView)
        val providerTextView =findViewById<TextView>(R.id.textView2)
        val logOutButton=findViewById<Button>(R.id.logout)

        emailTextView.text=email
        providerTextView.text=provider
        logOutButton.setOnClickListener{

            val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()

            onBackPressed()
        }
    }




}