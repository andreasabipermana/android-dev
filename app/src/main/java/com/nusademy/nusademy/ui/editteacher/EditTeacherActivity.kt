package com.nusademy.nusademy.ui.editteacher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.developer.kalert.KAlertDialog
import com.nusademy.nusademy.dataapi.DataTeacher
import com.nusademy.nusademy.dataapi.RetrofitClient
import com.nusademy.nusademy.databinding.ActivityEditTeacherBinding
import com.nusademy.nusademy.storage.SharedPrefManager
import com.nusademy.nusademy.ui.teacher_profil.TeacherProfilActivity
import com.nusademy.ui.mainmenuTeacher.MainMenuTeacherActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditTeacherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTeacherBinding

    //Ambil id dan token dari SharedPreference
    private val token= SharedPrefManager.getInstance(this).Getuser.token
    private val id= SharedPrefManager.getInstance(this).Getuser.idteacher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar: androidx.appcompat.app.ActionBar? = supportActionBar
        actionBar?.hide()

        val bundle = intent.extras
        if (bundle != null) {
            binding.tvLasteducation.setText( bundle.getString("last_education",""))
            binding.tvCampus.setText( bundle.getString("campus",""))
            binding.tvMajor.setText( bundle.getString("major",""))
            binding.tvIpk.setText( bundle.getString("ipk",""))
            binding.tvBrief.setText( bundle.getString("short_brief",""))
            binding.tvLinkedin.setText( bundle.getString("linkedin",""))
            binding.tvVideobranding.setText( bundle.getString("video_branding",""))
        }

//         Action Saat Button Save Di Klik
        binding.btSave.setOnClickListener(View.OnClickListener {

            // Memanggil Function UpdateSchool
            UpdateTeacher(
                binding.tvLasteducation.text.toString(),
                binding.tvCampus.text.toString(),
                binding.tvMajor.text.toString(),
                binding.tvIpk.text.toString(),
                binding.tvBrief.text.toString(),
                binding.tvVideobranding.text.toString(),
                binding.tvLinkedin.text.toString()
            )
        })

        binding.btnback.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, TeacherProfilActivity::class.java)
            startActivity(intent)
        })

    }

    //Inisiasi Fuction UpdateSchool
    fun UpdateTeacher(
                      lastEducation:String,
                      campus:String,
                      major:String,
                      ipk:String,
                      brief:String,
                      videoBranding:String,
                      linkedin:String

    ) {
        val pDialog = KAlertDialog(this, KAlertDialog.SUCCESS_TYPE)
        pDialog.contentText = "Updated data has been saved"
        pDialog.show()
        RetrofitClient.instanceUserApi.editProfileTeachers(
            id,"Bearer "+token,
            lastEducation,
            campus,
            major,
            ipk,
            brief,
            videoBranding,
            linkedin)
            .enqueue(object : Callback<DataTeacher> {
                override fun onResponse(
                    call: Call<DataTeacher>,
                    response: Response<DataTeacher>
                ) {
                    pDialog.hide()
                    if (response.isSuccessful) {
                        // Saat response sukses finnish activity (menutup/mengakhiri activity editprofile)
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Gagal Cek kembali Isian", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(calls: Call<DataTeacher>, ts: Throwable) {
                    Log.d("Error", ts.message.toString())

                }
            })
    }
}