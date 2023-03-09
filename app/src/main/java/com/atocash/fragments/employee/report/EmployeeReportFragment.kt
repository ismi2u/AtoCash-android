package com.atocash.fragments.employee.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atocash.R

class EmployeeReportFragment : Fragment() {

    private lateinit var viewModelEmployee: EmployeeReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelEmployee =
            ViewModelProvider(this).get(EmployeeReportViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_employee_report, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        viewModelEmployee.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}