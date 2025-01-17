package com.nusademy.nusademy.ui.applytempteacher

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.developer.kalert.KAlertDialog
import com.nusademy.nusademy.dataapi.RetrofitClient
import com.nusademy.nusademy.storage.SharedPrefManager
import com.nusademy.nusademy.R
import com.nusademy.nusademy.dataapi.ListdataTemporaryRequest
import com.nusademy.nusademy.dataapi.ListdataTemporaryRequest.ListdataTemporaryRequestItem
import com.nusademy.nusademy.databinding.ActivityRequestListBinding
import com.nusademy.nusademy.ui.tempteacherrequest.TempTeacherRequestAdapter
import com.nusademy.nusademy.ui.tempteacherrequest.TempTeacherRequestAdapter.ItemClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TempTeacherRequestActivity : AppCompatActivity(), ItemClickListener {

    private val list = MutableLiveData<ArrayList<ListdataTemporaryRequestItem>>()
    private lateinit var binding: ActivityRequestListBinding
    private lateinit var dataAdapter: TempTeacherRequestAdapter
    val token = SharedPrefManager.getInstance(this).Getuser.token
    val idteacher = SharedPrefManager.getInstance(this).Getuser.idteacher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar: androidx.appcompat.app.ActionBar? = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        actionBar?.setTitle("Temporary Teacher Request")
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        GetListApply("Requested")

        dataAdapter = TempTeacherRequestAdapter(this,true)
        dataAdapter.notifyDataSetChanged()
        binding.rvItems.apply {
            layoutManager =
                LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL, false
                )
            setHasFixedSize(true)
            adapter = dataAdapter
        }

        binding.toggleButtonGroup.check(R.id.bt_list_wait)
        binding.toggleButtonGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.bt_list_wait -> {
                        dataAdapter = TempTeacherRequestAdapter(this,true)
                        dataAdapter.notifyDataSetChanged()
                        binding.rvItems.apply {
                            layoutManager =
                                LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL, false
                                )
                            setHasFixedSize(true)
                            adapter = dataAdapter
                        }
                        GetListApply("Requested")

                    }
                    R.id.bt_list_accept -> {
                        dataAdapter = TempTeacherRequestAdapter(this,false)
                        dataAdapter.notifyDataSetChanged()
                        binding.rvItems.apply {
                            layoutManager =
                                LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL, false
                                )
                            setHasFixedSize(true)
                            adapter = dataAdapter
                        }
                        GetListApply("Approved")

                    }
                    R.id.bt_list_reject -> {
                        dataAdapter = TempTeacherRequestAdapter(this,false)
                        dataAdapter.notifyDataSetChanged()
                        binding.rvItems.apply {
                            layoutManager =
                                LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL, false
                                )
                            setHasFixedSize(true)
                            adapter = dataAdapter
                        }
                        GetListApply("Rejected")

                    }
                }
            } else {
            }
        }



    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onApproveClick(id: String) {
        EditRequest(id, "Approved")
    }

    override fun onRejectClick(id: String) {
        EditRequest(id, "Rejected")
    }


    // Get List Data From API ----------------------------------------------------------------------------------------
    fun GetListApply(status:String) {
        setItems(idteacher, token,status)
        getItems().observe(this, {
            if (it != null) {
                dataAdapter.setDataItem(it)
            }
        })
    }

    fun setItems(id: String, token: String,status:String) {
        val pDialog = KAlertDialog(this, KAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()
        RetrofitClient.instanceUserApi.getTempRequest("Bearer " + token, "School", idteacher, status)
            .enqueue(object : Callback<ListdataTemporaryRequest> {
                override fun onResponse(call: Call<ListdataTemporaryRequest>, response: Response<ListdataTemporaryRequest>) {
                    Log.d("JSON", response.toString())
                    if (response.isSuccessful) {
                        list.postValue(response.body())
                        pDialog.dismissWithAnimation()
                    }
                }

                override fun onFailure(call: Call<ListdataTemporaryRequest>, t: Throwable) {
                    Log.d("onFailure", t.message.toString())
                    pDialog.dismissWithAnimation()
                }
            })
    }

    fun getItems(): LiveData<ArrayList<ListdataTemporaryRequestItem>> {
        return list
    }

    // Function ADDClass API -----------------------------------------------------------------------------------------
    fun EditRequest(idrequest: String, status: String) {
        val pDialog = KAlertDialog(this, KAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.changeAlertType(KAlertDialog.PROGRESS_TYPE)
        pDialog.show()
        RetrofitClient.instanceUserApi.editTempRequest(
            "Bearer " + token,
            status,
            idrequest,
        )
            .enqueue(object : Callback<ListdataTemporaryRequestItem> {
                override fun onResponse(
                    call: Call<ListdataTemporaryRequestItem>,
                    response: Response<ListdataTemporaryRequestItem>
                ) {
                    if (response.isSuccessful) {
                        // Saat response sukses finnish activity (menutup/mengakhiri activity editprofile)
                        var statusrespon: String
                        if (status == "Approved") {
                            statusrespon = "Diterima"
                        } else {
                            statusrespon = "Ditolak"
                        }
                        pDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE)
                        pDialog.setTitleText("Berhasil")
                        pDialog.setContentText("Permohonan berhasil $statusrespon")
                        pDialog.setConfirmText("OK")
                        pDialog.setConfirmClickListener { sDialog ->
                            sDialog.dismissWithAnimation()
                            GetListApply("Requested")
                        }
                    } else {
                        Toast.makeText(applicationContext, "Gagal cek Intenet anda", Toast.LENGTH_SHORT).show()
                        pDialog.dismissWithAnimation()
                    }
                }

                override fun onFailure(calls: Call<ListdataTemporaryRequestItem>, ts: Throwable) {
                    Log.d("Error", ts.message.toString())
                    pDialog.dismissWithAnimation()
                }
            })
    }
}


    
    
    
    
    
    
    
    
