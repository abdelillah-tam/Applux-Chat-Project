package com.example.applux.ui.chatchannel

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applux.OnlineOrOffline
import com.example.applux.R
import com.example.applux.databinding.FragmentChatchannelBinding
import com.example.applux.domain.models.About
import com.example.applux.domain.models.ContactUser
import com.example.applux.domain.models.Message
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class ChatchannelFragment : Fragment(R.layout.fragment_chatchannel) {

    private val contactValue: ChatchannelFragmentArgs by navArgs()
    @Inject
    lateinit var auth: FirebaseAuth

    private val chatChannelViewModel: ChatChannelViewModel by viewModels()
    private lateinit var binding: FragmentChatchannelBinding
    @Inject
    lateinit var chatchannelAdapter: ChatchannelAdapter


    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("TAG", "onViewCreated: called")
        binding = FragmentChatchannelBinding.bind(view)
        val contact: ContactUser = contactValue.contact!!
        val profileBitmap: Bitmap? = contactValue.profileBitmap

        chatChannelViewModel.getUserDataViewModel(
            contactUser = contact,
            pictureBitmap = profileBitmap
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatChannelViewModel.state.collect {
                    if (it.profilePictureBitmap != null) {
                        binding.receiverprofilepic.setImageBitmap(it.profilePictureBitmap)
                    }
                    if (it.contactUser != null) {
                        binding.receivernameTextview.text = it.contactUser.name
                    }
                    if (it.isSent) {
                        binding.messagesRecycler.scrollToPosition(it.messages.size - 1)
                        chatChannelViewModel.setIsSentToFalse()
                    }
                    if (it.lastSeen != null && it.lastSeen.onlineOrOffline!!.equals(OnlineOrOffline.ONLINE)){
                        binding.userState.visibility = MaterialTextView.VISIBLE
                        binding.userState.setTextColor(ResourcesCompat.getColor(resources, R.color.green_600, null))
                        binding.userState.text = "Online"
                    }else{
                        binding.userState.visibility = MaterialTextView.GONE
                    }
                    if (it.messages.isNotEmpty()) {
                        chatchannelAdapter.addMessages(ArrayList(it.messages.values.sortedBy {
                            it.timestamp
                        }))
                        binding.loadingChats.visibility = View.GONE
                    } else {

                        chatChannelViewModel.getAllMessagesViewModel()
                    }
                    if (it.disableLoading){
                        binding.loadingChats.visibility = View.GONE
                    }

                }
            }
        }

        val linearLayoutManager = LinearLayoutManager(context)
        binding.messagesRecycler.apply {
            layoutManager = linearLayoutManager
            adapter = chatchannelAdapter
        }
        binding.toolbarOfChatchannelfrag.title = ""


        val acti = (activity as AppCompatActivity)
        acti.setSupportActionBar(binding.toolbarOfChatchannelfrag)
        val navHost =
            acti.supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        acti.setupActionBarWithNavController(navHost.navController)
        setHasOptionsMenu(true)

        binding.toolbarOfChatchannelfrag.setOnClickListener {
            val action =
                ChatchannelFragmentDirections.actionChatchannelFragmentToProfileFragment(contact)
            findNavController().navigate(action)
        }

        binding.receivernameTextview.text = contact.name


        binding.messagesRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0
                    && dy < 0
                ) {
                    binding.loadingChats.visibility = View.VISIBLE
                    chatChannelViewModel.getAllMessagesViewModel()

                }
            }
        })
        binding.sendButton.setOnClickListener {
            val text = binding.messageTextEdittext.text.toString()
            binding.messageTextEdittext.text.clear()
            val message = Message(
                UUID.randomUUID().toString(),
                Timestamp.now().seconds.toString(),
                text,
                auth.currentUser!!.phoneNumber,
                auth.currentUser!!.uid,
                contact.phone,
                contact.uid
            )

            chatChannelViewModel.sendMessageViewModel(message)

        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigate(R.id.action_chatchannelFragment_to_mainFragment)
                true
            }
            else -> false
        }
    }


}