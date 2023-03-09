package com.atocash.fragments.admin.task

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.common.ManageAdminActivity
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentTaskBinding
import com.atocash.fragments.admin.task.adapter.TasksAdapter
import com.atocash.network.response.TasksResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.showShortToast
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson

class TaskFragment :
    SuperFragment<FragmentTaskBinding, TaskViewModel>(),
    TaskNavigator, TasksAdapter.CostCenterCallback {

    private lateinit var binding: FragmentTaskBinding
    private lateinit var viewModel: TaskViewModel

    private lateinit var adapter: TasksAdapter

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_task
    }

    override fun getViewModel(): TaskViewModel {
        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    private fun initViewsAndClicks() {
        binding.addTasks.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Keys.IntentData.FROM, "Tasks")
            bundle.putBoolean(Keys.IntentData.IS_EDIT, false)
            intentHelper.goTo(requireActivity(), ManageAdminActivity::class.java, bundle, false)
        }

        adapter = TasksAdapter(ArrayList(), this)
        binding.tasksRv.layoutAnimation = getBottomScaleAnim()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.tasksRv.layoutManager = layoutManager
        binding.tasksRv.addItemDecoration(
            EvenMarginDecorator(
                16,
                EvenMarginDecorator.VERTICAL
            )
        )
        binding.tasksRv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        loadTasks()
    }

    private fun loadTasks() {
        viewModel.isLoading.set(true)
        viewModel.getWorkTasks(getToken()).observe(viewLifecycleOwner, Observer {
            viewModel.isLoading.set(false)
            if (it.code() == 200) {
                val listResponse = it.body()
                if (listResponse.isNullOrEmpty().not()) {
                    listResponse?.let { it1 -> adapter.addAll(it1) }
                }
            } else if (it.code() == 401) {
                (requireActivity() as AdminMainActivity).showUnAuthDialog()
            } else {
                requireContext().showShortToast(it.message())
            }
        })
    }

    override fun onEdit(item: TasksResponse) {
        val bundle = Bundle()
        bundle.putString(Keys.IntentData.FROM, "Tasks")
        bundle.putString("TaskData", Gson().toJson(item))
        bundle.putBoolean(Keys.IntentData.IS_EDIT, true)
        intentHelper.goTo(requireActivity(), ManageAdminActivity::class.java, bundle, false)
    }

    override fun onDelete(item: TasksResponse) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setMessage(getString(R.string.ask_to_delete))
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            getString(R.string.yes)
        ) { dialog_, which ->
            dialog_?.dismiss()
            proceedToDelete(item)
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.no)
        ) { dialog_, which ->
            dialog_?.dismiss()
        }
        dialog.show()
    }

    private fun proceedToDelete(item: TasksResponse) {
        if (AppHelper.isNetworkConnected(requireContext())) {
            showLoading(getString(R.string.deleting))
            viewModel.deleteTasks(getToken(), item).observe(viewLifecycleOwner, Observer {
                cancelLoading()
                when {
                    it.code() == 204 || it.code() == 200 -> {
                        requireContext().showShortToast(getString(R.string.info_project_deleted_success))
                        adapter.remove(item)
                    }
                    it.code() == 401 -> {
                        requireContext().showShortToast(it.message())
                    }
                    it.code() == 409 -> {
                        requireContext().showShortToast(getString(R.string.err_cannot_delete_project))
                    }
                    else -> {
                        requireContext().showShortToast(it.message())
                    }
                }
            })
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

}