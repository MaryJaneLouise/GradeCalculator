package com.mariejuana.gradecalculator.ui.screens.dialog.subject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mariejuana.gradecalculator.data.database.realm.RealmDatabase
import com.mariejuana.gradecalculator.databinding.DialogAddSemesterBinding
import com.mariejuana.gradecalculator.databinding.DialogAddSubjectBinding
import com.mariejuana.gradecalculator.databinding.DialogAddYearBinding
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddSubjectDialog : DialogFragment() {
    private lateinit var binding: DialogAddSubjectBinding
    lateinit var refreshDataCallback: RefreshDataInterface
    private var database = RealmDatabase()

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
        binding = DialogAddSubjectBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val yearLevelId = bundle!!.getString("yearLevelId")
        val academicYear = bundle!!.getString("academicYear")
        val semesterId = bundle!!.getString("semesterId")
        val semesterName = bundle!!.getString("semesterName")

        with(binding) {
            buttonAdd.setOnClickListener {
                if (textSubjectName.text.isNullOrEmpty()) {
                    textSubjectName.error = "Required"
                    return@setOnClickListener
                }
                if (textSubjectCode.text.isNullOrEmpty()) {
                    textSubjectCode.error = "Required"
                    return@setOnClickListener
                }
                if (textSubjectUnits.text.isNullOrEmpty()) {
                    textSubjectUnits.error = "Required"
                    return@setOnClickListener
                }

                val coroutineContext = Job() + Dispatchers.IO
                val scope = CoroutineScope(coroutineContext + CoroutineName("addSubjectDetails"))
                scope.launch(Dispatchers.IO) {
                    val subjectName = textSubjectName.text.toString()
                    val subjectCode = textSubjectCode.text.toString()
                    val subjectUnits = textSubjectUnits.text.toString().toFloat()

                    if (yearLevelId != null) {
                        if (semesterName != null) {
                            if (semesterId != null) {
                                if (academicYear != null) {
                                    database.addSubject(yearLevelId, semesterName, semesterId, academicYear,
                                        subjectName, subjectCode, subjectUnits)
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "Subject has been added!", Toast.LENGTH_LONG).show()
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