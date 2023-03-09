package com.atocash.fragments.admin.assignRoles

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.atocash.BR
import com.atocash.R
import com.atocash.activities.admin.main.AdminMainActivity
import com.atocash.adapter.UsersDropDownAdapter
import com.atocash.adapter.UsersRolesDropDownAdapter
import com.atocash.base.view.SuperFragment
import com.atocash.databinding.FragmentAssignRolesBinding
import com.atocash.network.response.UserRolesResponse
import com.atocash.network.response.UsersResponse
import com.atocash.utils.AppHelper
import com.atocash.utils.CustomTextWatcher
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showErrorResponse
import com.atocash.utils.extensions.showShortToast
import com.google.gson.JsonArray
import com.google.gson.JsonObject


class AssignRolesFragment :
    SuperFragment<FragmentAssignRolesBinding, AssignRolesViewModel>(),
    AssignRolesNavigator {

    private lateinit var viewModel: AssignRolesViewModel
    private lateinit var binding: FragmentAssignRolesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        viewModel.setDataStoreAndNavigator(dataStorage, this)

        initViewsAndClicks()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_assign_roles
    }

    override fun getViewModel(): AssignRolesViewModel {
        viewModel =
            ViewModelProvider(this).get(AssignRolesViewModel::class.java)
        return viewModel
    }

    private fun initViewsAndClicks() {
        binding.userEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.userLayout.error = ""
                }
            }
        })
        binding.roleEd.addTextChangedListener(object : CustomTextWatcher() {
            override fun onTextChanged(text: String) {
                if (text.isNotEmpty()) {
                    binding.roleLayout.error = ""
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        loadUsers()
    }

    private fun loadUsers() {
        viewModel.getUsers(getToken())
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                when (it.code()) {
                    200 -> {
                        val resBody = it.body()
                        resBody?.let { resBody_ ->
                            setUsersAdapter(resBody_)
                        }
                    }
                    401 -> {
                        (requireContext() as AdminMainActivity).showUnAuthDialog()
                    }
                    else -> requireContext().showErrorResponse(it.errorBody()?.string())
                }
            })

        loadRoles()
    }

    private var empAdapter: UsersDropDownAdapter? = null
    private var userID = ""
    private var userResponse: UsersResponse? = null

    private fun setUsersAdapter(res: ArrayList<UsersResponse>) {
        empAdapter = UsersDropDownAdapter(requireContext(), R.layout.item_status_spinner_view, res)
        binding.userEd.setAdapter(empAdapter)
        binding.userEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                userID = res[position].id
                userResponse = res[position]
                binding.userEd.setText(res[position].userName, false)
                setEmployeeFilter(empAdapter!!)
            }
    }

    private fun setEmployeeFilter(adapter: UsersDropDownAdapter) {
        adapter.filter.filter(null)
    }

    private fun setRolesAdapter(adapter: UsersRolesDropDownAdapter) {
        adapter.filter.filter(null)
    }

    private fun loadRoles() {
        viewModel.getRoles(getToken())
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                when (it.code()) {
                    200 -> {
                        val resBody = it.body()
                        resBody?.let { resBody_ ->
                            setRolesAdapter(resBody_)
                        }
                    }
                    401 -> {
                        (requireContext() as AdminMainActivity).showUnAuthDialog()
                    }
                    else -> requireContext().showErrorResponse(it.errorBody()?.string())
                }
            })
    }

    private var jobRolesResponse: UserRolesResponse? = null
    private var rolesAdapter: UsersRolesDropDownAdapter? = null

    private fun setRolesAdapter(resBody_: ArrayList<UserRolesResponse>) {
        rolesAdapter =
            UsersRolesDropDownAdapter(requireContext(), R.layout.item_status_spinner_view, resBody_)
        binding.roleEd.setAdapter(rolesAdapter)
        binding.roleEd.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, position, id ->
                printLog("Added id as ${resBody_[position].id}")
                roleIds.add(resBody_[position].id)
                roleIdArray.add(resBody_[position].id)
                jobRolesResponse = resBody_[position]
                binding.roleEd.setText(resBody_[position].name, false)
                setRolesAdapter(rolesAdapter!!)
            }
    }

    override fun onAssignProject() {
        if (AppHelper.isNetworkConnected(requireContext())) {
            if (isAllDataAvailable()) {
                showLoading()
                viewModel.assignRoles(getToken(), getRolesJson())
                    .observe(viewLifecycleOwner, Observer {
                        cancelLoading()
                        when {
                            it.code() == 200 -> {
                                requireContext().showShortToast(getString(R.string.roles_assigned_success))
                            }
                            it.code() == 400 -> {
                                requireContext().showShortToast("Bad request")
                            }
                            it.code() == 500 -> {
                                requireContext().showShortToast("Internal server error")
                            }
                            it.code() == 401 -> {
                                (requireActivity() as AdminMainActivity).showUnAuthDialog()
                            }
                            else -> {
                                requireContext().showErrorResponse(it.errorBody()?.string())
                            }
                        }
                    })
            }
        } else {
            showSnack(getString(R.string.check_internet))
        }
    }

    private var userId = ""
    private var roleIds = ArrayList<String>()
    private var roleIdArray = JsonArray()

    private fun getRolesJson(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("userId", userId)
        jsonObject.add("roleIds", roleIdArray)
        return jsonObject
    }

    private fun isAllDataAvailable(): Boolean {
        AppHelper.hideKeyboard(requireContext())

        if (binding.userEd.text.toString().trim().isEmpty()) {
            binding.userLayout.error = getString(R.string.err_choose_user)
            return false
        }

        if (roleIds.isEmpty()) {
            binding.roleLayout.error = getString(R.string.err_choose_role)
            return false
        }

        return true
    }
}