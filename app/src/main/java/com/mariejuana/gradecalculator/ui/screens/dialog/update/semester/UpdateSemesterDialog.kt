package com.mariejuana.gradecalculator.ui.screens.dialog.update.semester

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.databinding.DialogAddYearBinding
import com.mariejuana.gradecalculator.databinding.DialogUpdateSemesterBinding
import com.mariejuana.gradecalculator.databinding.DialogUpdateYearBinding
import com.mariejuana.gradecalculator.extensions.Extensions
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateSemesterDialog : DialogFragment() {
    private lateinit var binding: DialogUpdateSemesterBinding
    lateinit var refreshDataCallback: RefreshDataInterface
    private var database = RealmDatabase()
    private var extensions = Extensions()

    interface RefreshDataInterface {
        fun refreshData()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogUpdateSemesterBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val yearLevelId = bundle!!.getString("updateYearLevelId").toString()
        val semesterId = bundle!!.getString("updateSemesterId").toString()
        val semesterName = bundle!!.getString("updateSemesterName").toString()

        with(binding) {
            textSemesterLevel.setText(semesterName)

            buttonUpdate.setOnClickListener {
                if (textSemesterLevel.text.isNullOrEmpty()) {
                    textSemesterLevel.error = "Required"
                    return@setOnClickListener
                }

                val coroutineContext = Job() + Dispatchers.IO
                val scope = CoroutineScope(coroutineContext + CoroutineName("updateSemesterDetails"))
                scope.launch(Dispatchers.IO) {
                   val semesterName = textSemesterLevel.text.toString()

                    database.updateSemester(yearLevelId, semesterId, semesterName)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Semester has been updated!", Toast.LENGTH_LONG).show()
                        refreshDataCallback.refreshData()
                        dialog?.dismiss()
                    }
                }
            }

            buttonCancel.setOnClickListener {
                dialog?.cancel()
            }
        }
    }


}