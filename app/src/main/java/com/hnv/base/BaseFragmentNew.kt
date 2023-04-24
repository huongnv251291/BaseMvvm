package com.hnv.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.example.basemvvm.R
import com.hnv.utils.exceptions.HvException
import com.hnv.widgets.LoadingDialog
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class BaseFragmentNew<VM: BaseViewModel>(@LayoutRes layoutId: Int): Fragment(layoutId) {

    abstract val viewModel: VM
    private var customErrorHandler: ((exception: HvException) -> Unit)? = null

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Always hide keyboard and processing layout when enter a new screen
        hideProcessing()

        viewModel.exception.observe(viewLifecycleOwner) {
            hideProcessing()
        }
    }

    fun addCustomErrorHandler(block: ((exception: HvException) -> Unit)) {
        customErrorHandler = block
    }

    fun showProcessing(msgSynchronizing: Int = R.string.label_processing_content) {
        LoadingDialog.getDialog(requireContext(), msgSynchronizing).show()
    }

    fun hideProcessing() {
        LoadingDialog.dismiss(requireContext())
    }
}