package com.example.applux.ui.contacts

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applux.R
import com.example.applux.databinding.FragmentContactsBinding
import com.google.android.material.progressindicator.CircularProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ContactsFragment : Fragment(R.layout.fragment_contacts) {
    private val TAG = "ContactsFragment"
    private val FROM = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    private val contactsViewModel : ContactsViewModel by viewModels()

    private lateinit var binding: FragmentContactsBinding
    private lateinit var navController: NavController

    @Inject lateinit var curListAdap: CursorRecyclerViewAdapter
    private lateinit var gesture: GestureDetectorCompat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                Array(1) { Manifest.permission.READ_CONTACTS },
                662
            )
        }
        gesture = GestureDetectorCompat(requireContext(), object : GestureDetector.OnGestureListener {
            override fun onDown(p0: MotionEvent): Boolean {
                return false
            }

            override fun onShowPress(p0: MotionEvent) {

            }

            override fun onSingleTapUp(p0: MotionEvent): Boolean {
                val view = binding.contactListView.findChildViewUnder(p0.x, p0.y)
                if (view != null) {
                    val position = binding.contactListView.getChildAdapterPosition(view)
                    val contactUser = curListAdap.getContact(position)
                    val action =
                        ContactsFragmentDirections.actionContactsFragmentToChatchannelFragment(
                            contactUser.contactUser, null, null
                        )
                    findNavController().navigate(action)
                }
                return true
            }

            override fun onScroll(
                p0: MotionEvent,
                p1: MotionEvent,
                p2: Float,
                p3: Float
            ): Boolean {
                return false
            }

            override fun onLongPress(p0: MotionEvent) {

            }

            override fun onFling(
                p0: MotionEvent,
                p1: MotionEvent,
                p2: Float,
                p3: Float
            ): Boolean {
                return false
            }
        })


    }
    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentContactsBinding.bind(view)
        binding.contactListView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = curListAdap
        }
        binding.contactListView.addOnItemTouchListener(object :
            RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                gesture.onTouchEvent(e)
                return false
            }
        })

        val acti = (activity as AppCompatActivity)
        setHasOptionsMenu(true)
        binding.toolbarOfContactfrag.title = ""
        acti.setSupportActionBar(binding.toolbarOfContactfrag)
        val navHost =
            activity?.supportFragmentManager?.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHost.navController
        acti.setupActionBarWithNavController(navController)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                contactsViewModel.state.collect {
                    curListAdap.setContactsFromFirebase(it.contactsItemUiState)
                    binding.progressContacts.visibility = CircularProgressIndicator.GONE
                }
            }
        }

        readContact()

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_contactsfrag, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            android.R.id.home -> findNavController().popBackStack()
            R.id.menu_refresh_item -> {
                binding.progressContacts.visibility = View.VISIBLE
                readContact()
            }
        }
        return true
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 662 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding.contactListView.adapter = curListAdap
        }
    }
    @SuppressLint("Range")
    fun readContact() {
        val cur = activity?.contentResolver!!.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            FROM,
            null,
            null,
            null
        )
        val hashMap = HashMap<String, String>()

        if (cur != null) {
            while (cur.moveToNext()) {
                val name =
                    cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var number =
                    cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                number = number.replace("-", "").replace("(", "").replace(")", "").replace(" ", "")

                val telephonyManager: TelephonyManager =
                    activity?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val usersCountryISOCode: String = telephonyManager.networkCountryIso.uppercase()
                val phoneUtil = PhoneNumberUtil.createInstance(activity)

                val countryCode = "+" + phoneUtil.getCountryCodeForRegion(usersCountryISOCode)

                if (!number.startsWith(countryCode)) {
                    val phoneNumber = phoneUtil.parse(number, usersCountryISOCode)
                    if (phoneUtil.isValidNumber(phoneNumber)) {
                        number = "+" + phoneNumber.countryCode + phoneNumber.nationalNumber
                    }
                }
                hashMap.set(name, number)
            }

        }
        contactsViewModel.getContactsViewModel(hashMap)

    }
}