package com.ylx.demoapp.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel

abstract class BaseFragment : Fragment() {

    protected inline fun <reified VM : ViewModel> injectViewModel() : Lazy<VM> {
        return viewModels { defaultViewModelProviderFactory }
    }

    protected fun showError(msg: String) {

    }
}