package com.smartherd.firebasemessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chatt_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChattLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    var toUser:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatt_log)

        recycler_chatlog.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
      // val username =  intent.getStringExtra(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

       // setUpDummyData()
        listenForMessage()
        send_btn_chatlog.setOnClickListener {
            performSendMessage()
        }
    }
    private fun listenForMessage(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uids
        val ref  = FirebaseDatabase.getInstance().getReference("/user-message/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(task: DataSnapshot, p1: String?) {
                val chatMessage = task.getValue(ChatMessage::class.java)
                if (chatMessage != null){
                    Log.d("Chatt", chatMessage.text)
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    }else{
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }


                }

            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
    private fun performSendMessage(){

        val text = edittext_chatlog.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uids
       // val reference = FirebaseDatabase.getInstance().getReference("/message").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-message/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-message/$toId/$fromId").push()
        val chatMessage = ChatMessage(reference.key!!, text, fromId!!, toId, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage).addOnSuccessListener {
            Log.d("Chatt", "Saved ur messages")
            edittext_chatlog.text.clear()
            recycler_chatlog.scrollToPosition(adapter.itemCount-1)
        }
        toReference.setValue(chatMessage)
    }
   // private fun setUpDummyData(){
    //    val adapter = GroupAdapter<ViewHolder>()

    //    adapter.add(ChatFromItem("From Message..."))
      //  adapter.add(ChatToItem("To Message...."))
        //adapter.add(ChatFromItem("From Message..."))
       // adapter.add(ChatToItem("To Message...."))
        //adapter.add(ChatFromItem("From Message..."))
        //adapter.add(ChatToItem("To Message...."))


        //recycler_chatlog.adapter = adapter

    //}
}
class ChatFromItem(val text:String, val user:User) : Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.text_from_row.text = text
        val uri = user.profileImageUrl
        val targetView = viewHolder.itemView.image_chat_from_row
        Picasso.get().load(uri).into(targetView)
    }
}
class ChatToItem(val text:String, val user:User) : Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.text_to_row.text = text
        val uri = user.profileImageUrl
        val targetView = viewHolder.itemView.image_chat_to_row
        Picasso.get().load(uri).into(targetView)
    }
}