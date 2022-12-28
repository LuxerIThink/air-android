package com.example.air_app.menu.login

import android.app.AlertDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.air_app.R
import com.example.air_app.data.User
import com.example.air_app.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.login_fragment,container,false)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.loginViewModel = loginViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setHasOptionsMenu(true)


        if (!binding.loginViewModel!!.loaded) {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            val ip = sharedPref?.getString("IP", "localhost")
            val port = sharedPref?.getInt("PORT", 80)
            val currentUser = User(ip=ip!!, port = port!!)
            binding.loginViewModel!!.loadUserData(currentUser)
        }



        binding.loginViewModel!!.eventConfirm.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                updateUser()
                val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
            }
        })

        binding.editTextTextIP.doOnTextChanged { text, _, _, _ ->
            binding.loginViewModel!!.ip.value = text.toString()
        }
        binding.editTextTextPort.doOnTextChanged { text, _, _, _ ->
            binding.loginViewModel!!.port.value = text.toString()
        }

        return binding.root
    }

    private fun updateUser()
    {
        val ip = binding.editTextTextIP.text.toString()
        val port = binding.editTextTextPort.text.toString()

        if(inputCheck(ip, port)){

            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            with (sharedPref!!.edit()) {
                putString("IP", ip)
                putInt("PORT", port.toInt())
                apply()
            }

            Toast.makeText(requireContext(), R.string.updated_successfully, Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }else{
            Toast.makeText(requireContext(), R.string.empty_fields, Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(ip: String, port: String): Boolean{
        if(TextUtils.isEmpty(ip)){binding.editTextTextIP.error = requireContext().resources.getString(R.string.field_required)}
        if(TextUtils.isEmpty(port)){binding.editTextTextPort.error = requireContext().resources.getString(R.string.field_required)}
        return !(TextUtils.isEmpty(ip) || TextUtils.isEmpty(port))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> deleteUsers()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteUsers(){
        val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogTheme)
        builder.setTitle(R.string.alert_delete_user_title)
        builder.setMessage(R.string.alert_delete_user_message)
        builder.setPositiveButton(R.string.label_yes){_, _->
            loginViewModel.deleteUser()
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            with (sharedPref!!.edit()) {
                putString("IP", "localhost")
                putInt("PORT", 80)
                apply()
            }
        }
        builder.setNegativeButton(R.string.label_no) { _, _ -> }
        builder.create().show()
    }

}
