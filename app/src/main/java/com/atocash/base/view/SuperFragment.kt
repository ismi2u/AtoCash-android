package com.atocash.base.view

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atocash.R
import com.atocash.base.common.BaseViewModel
import com.atocash.utils.AppHelper
import com.atocash.utils.DataStorage
import com.atocash.utils.IntentHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.roundOffToTwoDecimal
import com.atocash.utils.extensions.showShortSnack
import com.atocash.utils.recycler.EvenMarginDecorator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * Created by geniuS on 9/5/2019.
 */
abstract class SuperFragment<T : ViewDataBinding, V : BaseViewModel<*>> :
    Fragment() {

    private lateinit var viewDataBinding: T
    private lateinit var viewModel: V

    lateinit var dataStorage: DataStorage
    lateinit var intentHelper: IntentHelper
//    lateinit var permissionsHelper: PermissionsHelper

    abstract fun getBindingVariable(): Int

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getViewModel(): V

    fun getViewDataBinding(): T {
        return viewDataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), container, false)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.setVariable(getBindingVariable(), viewModel)
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.executePendingBindings()
    }

    private fun initialize() {
        dataStorage = DataStorage(requireContext())
        intentHelper = IntentHelper(requireContext())

//        permissionsHelper = PermissionsHelper(requireContext() as Activity)
        viewModel = getViewModel()
    }

    fun getBottomScaleAnim(): LayoutAnimationController {
        return AnimationUtils.loadLayoutAnimation(context as Activity, R.anim.bottom_scale_anim)
    }

    fun getToken(): String {
        return dataStorage.getString(Keys.UserData.TOKEN).toString()
    }

    fun hasMultipleRoles(): Boolean {
        return dataStorage.getBoolean(Keys.UserData.HAS_MULTIPLE_LOGINS)
    }

    fun getRole(): String {
        return dataStorage.getString(Keys.UserData.USER_ROLE).toString()
    }

    fun withCurrency(amount: Float): String {
        val roundedValue = amount.roundOffToTwoDecimal().toString()
        return roundedValue + " " + dataStorage.getString(Keys.UserData.CURRENCY_CODE).toString()
    }

    fun isAlsoAnEmployee(): Boolean {
        return if (hasMultipleRoles()) {
            val rolesStr = dataStorage.getString(Keys.UserData.USER_ROLE).toString()
            val token: TypeToken<ArrayList<String>> =
                object : TypeToken<ArrayList<String>>() {}
            val roles: ArrayList<String> = Gson().fromJson(rolesStr, token.type)
            roles.contains(Keys.LoginType.EMPLOYEE)
        } else {
            dataStorage.getString(Keys.UserData.USER_ROLE) == Keys.LoginType.EMPLOYEE
        }
    }

    fun isEmployee(): Boolean {
        return dataStorage.getString(Keys.UserData.USER_ROLE) == Keys.LoginType.EMPLOYEE
    }

    fun setAdapter(recyclerView: RecyclerView) {
        recyclerView.layoutAnimation = getBottomScaleAnim()
        recyclerView.addItemDecoration(EvenMarginDecorator(20, EvenMarginDecorator.VERTICAL))
//        recyclerView.adapter = adapter
        recyclerView.scheduleLayoutAnimation()
//        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    fun showSnack(message: String?) {
        val view = requireActivity().findViewById<View>(android.R.id.content)
        message?.let { view?.showShortSnack(it) }
    }

    private var mProgressDialog: ProgressDialog? = null

    fun showLoading(msg: String) {
        cancelLoading()
        mProgressDialog = AppHelper.showLoadingDialog(requireContext(), msg)
    }

    fun showLoading() {
        cancelLoading()
        mProgressDialog = AppHelper.showLoadingDialog(requireContext(), getString(R.string.loading))
    }

    fun cancelLoading() {
        mProgressDialog?.let {
            if (it.isShowing) it.cancel()
        }
    }
}