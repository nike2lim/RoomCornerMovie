package kr.shlim.roomcornermovie

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

open class BaseActivity<B : ViewDataBinding, VM: ViewModel>(@LayoutRes private val layoutResId: Int,
                                                            clazz: KClass<VM>) : AppCompatActivity(){

    protected lateinit var mViewDatabinding : B
    protected val viewModel : VM by lazy { ViewModelProvider(this).get(clazz.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
    }

    private fun setBinding() {
        mViewDatabinding = DataBindingUtil.setContentView(this, layoutResId)
        mViewDatabinding.apply {
            lifecycleOwner = this@BaseActivity
            setVariable(BR.viewModel, viewModel)
            executePendingBindings()
        }
    }

}