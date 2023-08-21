package com.example.applux.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {

    private val chatViewModel: ChatViewModel by activityViewModels()

    @Inject
    lateinit var chatAdapter: ChatsRecyclerAdapter



    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        chatAdapter.setResources(resources)
        val state = chatViewModel.state


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                state.observe(viewLifecycleOwner){
//                    chatAdapter.setAllUsers(ArrayList(it.chatItemUiState.values.sortedByDescending{
//                        it.timestamp
//                    }))
//                }


            }
        }

        val gestureDetector =
            GestureDetectorCompat(requireContext(), object : GestureDetector.OnGestureListener {
                override fun onDown(p0: MotionEvent): Boolean {
                    return false
                }

                override fun onShowPress(p0: MotionEvent) {

                }

                override fun onSingleTapUp(p0: MotionEvent): Boolean {
                    val viewPosition = "binding.chatsRecyclerview.findChildViewUnder(p0.x, p0.y)"
                    if (viewPosition != null) {
                        //val position = binding.chatsRecyclerview.getChildAdapterPosition(viewPosition)
                        val user = chatAdapter.getChatItem(0)

                        if (user.contactUser != null) {
                            /*val action =
                                MainFragmentDirections.actionMainFragmentToChatchannelFragment(
                                    user.contactUser!!,
                                    user.profileBitmap,
                                    user.lastSeen
                                )
                            findNavController().navigate(action)*/
                        }
                    }
                    return false
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

}