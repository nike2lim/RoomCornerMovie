package kr.shlim.roomcornermovie.view.dialog

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.dialog_datepicker.*
import kotlinx.android.synthetic.main.dialog_datepicker.view.*
import kr.shlim.roomcornermovie.R

class DatePickerDialogFragment(var mYear: Int, var mMonth : Int, var mDay : Int , val callback : (tyear : Int, month: Int, day: Int) -> Unit) : DialogFragment(), DatePicker.OnDateChangedListener, View.OnClickListener {
    val mContext by lazy { activity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_datepicker, null, false)
        view.datePicker.init(mYear, mMonth, mDay, this@DatePickerDialogFragment)

        view.cancel.setOnClickListener(this@DatePickerDialogFragment)
        view.confirm.setOnClickListener(this@DatePickerDialogFragment)

        return view
    }

    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        Logger.d("onDateChanged year : $year, monthOfYear : $monthOfYear, dayOfMonth : $dayOfMonth")
        mYear = year
        mMonth = monthOfYear
        mDay = dayOfMonth
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.cancel -> {
                 dismiss()
            }
            R.id.confirm -> {
                callback.invoke(mYear, mMonth, mDay)
                dismiss()
            }
        }
    }
}

