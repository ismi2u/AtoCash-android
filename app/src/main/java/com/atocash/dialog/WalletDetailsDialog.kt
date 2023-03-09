package com.atocash.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import com.atocash.R
import com.atocash.network.response.EmployeesResponse
import com.atocash.network.response.MaxLimitBalanceAndCashInHand
import com.atocash.utils.extensions.formatCard
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DecimalFormat

class WalletDetailsDialog(
    private val currencyType: String?,
    private val walletInfo: MaxLimitBalanceAndCashInHand,
    private val employeeInfo: EmployeesResponse?
) :
    BottomSheetDialogFragment() {

    private lateinit var cardNumberTv: TextView
    private lateinit var currentBalanceTv: TextView
    private lateinit var maxLimitTv: TextView
    private lateinit var onHandTv: TextView
    private lateinit var pendingApprovalTv: TextView
    private lateinit var pendingSettlementTv: TextView
    private lateinit var statusTv: TextView
    private lateinit var currencyTv: TextView
    private lateinit var tvPendingApprovalER: TextView
    private lateinit var tvPendingSettlementER: TextView
    private lateinit var tvAdjustedCA: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_wallet_details, null)

        cardNumberTv = view.findViewById(R.id.cardNumTv)
        currentBalanceTv = view.findViewById(R.id.currentBalanceTv)
        maxLimitTv = view.findViewById(R.id.maxLimitTv)
        onHandTv = view.findViewById(R.id.onHandTv)
        pendingApprovalTv = view.findViewById(R.id.pendingApprovalTv)
        pendingSettlementTv = view.findViewById(R.id.pendingSettlementTv)
        statusTv = view.findViewById(R.id.statusTv)
        currencyTv = view.findViewById(R.id.currencyTv)
        tvPendingApprovalER = view.findViewById(R.id.tvPendingApprovalER)
        tvPendingSettlementER = view.findViewById(R.id.tvPendingSettlementER)
        tvAdjustedCA = view.findViewById(R.id.tvAdjustedCA)

        employeeInfo?.let {
            cardNumberTv.text = it.bankCardNo.formatCard()
        }

        val decimalFormat = DecimalFormat("0.00")

        currencyTv.text = currencyType.toString()

        currentBalanceTv.text = decimalFormat.format(walletInfo.curBalance).toString()
        maxLimitTv.text = decimalFormat.format(walletInfo.maxLimit).toString()
        onHandTv.text = decimalFormat.format(walletInfo.cashInHand).toString()
        pendingApprovalTv.text = decimalFormat.format(walletInfo.pendingApprovalCA).toString()
        pendingSettlementTv.text = decimalFormat.format(walletInfo.pendingSettlementCA).toString()
        tvPendingApprovalER.text = decimalFormat.format(walletInfo.pendingApprovalER).toString()
        tvPendingSettlementER.text = decimalFormat.format(walletInfo.pendingSettlementER).toString()
        tvAdjustedCA.text = decimalFormat.format(walletInfo.adjustedAgainstCA).toString()

        dialog.setContentView(view)
        dialog.setCancelable(true)

        return dialog
    }
}