package com.example.air_app.table

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.air_app.R
import com.example.air_app.data.User
import com.example.air_app.databinding.LedFragmentBinding
import com.example.air_app.databinding.TableFragmentBinding
import com.example.air_app.led.LedViewModel


class TableFragment : Fragment() {

    private lateinit var tableViewModel: TableViewModel
    private lateinit var binding: TableFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.table_fragment,container,false)
        tableViewModel = ViewModelProvider(this).get(TableViewModel::class.java)
        binding.tableViewModel = tableViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.tableViewModel!!.Init(requireContext())

        binding.tableViewModel!!.makeAlert.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
                builder.setMessage(it)
                builder.setPositiveButton(R.string.label_ok) { _, _ -> }
                builder.create().show()
            }
        }

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val ip = sharedPref?.getString("IP", "localhost")
        val port = sharedPref?.getInt("PORT", 80)
        val currentUser = User(ip=ip!!, port = port!!)
        binding.tableViewModel!!.loadUser(currentUser)
        binding.tableViewModel!!.updateMeasurements(null)

        return binding.root
    }

}