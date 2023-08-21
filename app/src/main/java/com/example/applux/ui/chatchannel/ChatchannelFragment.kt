//package com.example.applux.ui.chatchannel
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.MenuItem
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.applux.OnlineOrOffline
//import com.example.applux.Privacy
//import com.google.firebase.auth.FirebaseAuth
//import kotlinx.coroutines.launch
//import java.text.DateFormat
//import java.text.SimpleDateFormat
//import java.util.*
//import javax.inject.Inject
//import kotlin.collections.ArrayList
//
//class ChatchannelFragment : Fragment() {
//
//    //private val contactValue: ChatchannelFragmentArgs by navArgs()
//    @Inject
//    lateinit var auth: FirebaseAuth
//
//    private val chatChannelViewModel: ChatChannelViewModel by viewModels()
//    @Inject
//    lateinit var chatchannelAdapter: ChatchannelAdapter
//
//
//    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        Log.e("TAG", "onViewCreated: called")
//        /*
//        val contact: ContactUser = contactValue.contact!!
//        val profileBitmap: Bitmap? = contactValue.profileBitmap*/
//
//        /*chatChannelViewModel.getUserDataViewModel(
//            contactUser = contact,
//            pictureBitmap = profileBitmap
//        )*/
//
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                chatChannelViewModel.state.collect {
//                    if (it. != null) {
//                        //Glide.with(requireContext()).load(it.profilePictureBitmap).into(binding.receiverprofilepic)
//                    }else{
//                        //Glide.with(requireContext()).load(R.drawable.ic_face).into(binding.receiverprofilepic)
//                    }
//                    if (it.contactUser != null) {
//                        //binding.receivernameTextview.text = it.contactUser.name
//                    }
//                    if (it.isSent) {
//                        //binding.messagesRecycler.scrollToPosition(it.messages.size - 1)
//                        chatChannelViewModel.setIsSentToFalse()
//                    }
//                    if (it.lastSeen != null && it.lastSeen.onlineOrOffline!!.equals(OnlineOrOffline.ONLINE)){
//                        //binding.userState.visibility = MaterialTextView.VISIBLE
//                        //binding.userState.setTextColor(ResourcesCompat.getColor(resources, R.color.green_600, null))
//                        //binding.userState.text = "Online"
//                    }else{
//                        if (it.lastSeen != null && it.lastSeen.privacy.equals(Privacy.NONOE)){
//                            //binding.userState.visibility = MaterialTextView.GONE
//                        }else {
//                            //binding.userState.setTextColor(ResourcesCompat.getColor(resources, R.color.grey_500, null))
//                            //binding.userState.text = timestampToDate(it.lastSeen?.timestamp)
//                        }
//                    }
//                    if (it.messages.isNotEmpty()) {
//                        chatchannelAdapter.addMessages(ArrayList(it.messages.values.sortedBy {
//                            it.message.timestamp
//                        }))
//                        //binding.loadingChats.visibility = View.GONE
//                    } else {
//
//                        chatChannelViewModel.getAllMessagesViewModel()
//                    }
//                    if (it.disableLoading){
//                        //binding.loadingChats.visibility = View.GONE
//                    }
//
//                }
//            }
//        }
//
//        val linearLayoutManager = LinearLayoutManager(context)
//
//       // binding.toolbarOfChatchannelfrag.title = ""
//
//
//        val acti = (activity as AppCompatActivity)
//        //acti.setSupportActionBar(binding.toolbarOfChatchannelfrag)
//        //val navHost = acti.supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
//        //acti.setupActionBarWithNavController(navHost.navController)
//        setHasOptionsMenu(true)
//
//        //binding.toolbarOfChatchannelfrag.setOnClickListener {
//            /*val action =
//                ChatchannelFragmentDirections.actionChatchannelFragmentToProfileFragment(contact, profileBitmap)
//            findNavController().navigate(action)*/
//        //}
//
//        //binding.receivernameTextview.text = contact.name
//
//
//      /*  binding.messagesRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0
//                    && dy < 0
//                ) {
//                    binding.loadingChats.visibility = View.VISIBLE
//                    chatChannelViewModel.getAllMessagesViewModel()
//
//                }
//            }
//        })*/
//      /*  binding.sendButton.setOnClickListener {
//            val text = binding.messageTextEdittext.editText?.text.toString()
//            binding.messageTextEdittext.editText?.text?.clear()
//            TimeByZoneClient.getTime(DateTimeZone.getDefault().id)
//                .enqueue(object : Callback<WorldTimeModel>{
//                    override fun onResponse(
//                        call: Call<WorldTimeModel>,
//                        response: Response<WorldTimeModel>
//                    ) {
//                        val time = response.body()!!
//                        val timestamp = DateTime.parse(time.dateTime).millis / 1000L
//                        *//*val message = Message(
//                            UUID.randomUUID().toString(),
//                            timestamp.toString(),
//                            text,
//                            auth.currentUser!!.phoneNumber,
//                            auth.currentUser!!.uid,
//                            contact.phoneOrEmail,
//                            contact.uid,
//                            MessageType.TEXT,
//                            null
//                        )
//                        chatChannelViewModel.sendMessageViewModel(message)*//*
//                    }
//
//                    override fun onFailure(call: Call<WorldTimeModel>, t: Throwable) {
//
//                    }
//
//
//                })
//        }
//*/
//        /*binding.chatchannelCameraButton.setOnClickListener {
//            val action = ChatchannelFragmentDirections.actionChatchannelFragmentToCameraFragment(contact)
//            findNavController().navigate(action)
//        }*/
//
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                true
//            }
//            else -> false
//        }
//    }
//
//    private fun timestampToDate(timestamp: String?): String {
//        val date: DateFormat = SimpleDateFormat("hh:mm a")
//        date.timeZone = TimeZone.getDefault()
//        if (timestamp != null) {
//            val secondDate = Date(timestamp.toLong() * 1000L)
//            return date.format(secondDate)
//        }
//        return ""
//    }
//
//}