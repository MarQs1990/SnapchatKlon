package com.example.marcu.snapchatklon

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.marcu.snapchatklon.R.id.snap
import com.example.marcu.snapchatklon.R.menu.snaps
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SnapsActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var emails: ArrayList<String> = ArrayList()
    var snapsListView: ListView? = null
    var snaps: ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        snapsListView = findViewById<ListView>(R.id.snapsListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        snapsListView?.adapter = adapter

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser?.uid!!).child("snaps")
            .addChildEventListener(object: ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                override fun onChildRemoved(p0: DataSnapshot) {

                    var index = 0
                    for(snap:DataSnapshot in snaps){
                        if(snap.key == p0?.key){
                            snaps.removeAt(index)
                            emails.removeAt(index)
                            break
                        }
                        index++
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    emails.add(p0?.child("from")?.value as String)
                    snaps.add(p0!!)
                    adapter.notifyDataSetChanged()
                }
            })
        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener{adapterView, view, position,id ->
            val snapShot = snaps.get(position)

            var intent = Intent(this, ViewSnapActivity::class.java)
            intent.putExtra("imageName", snapShot.child("imagename").value as String)
            intent.putExtra("imageUrl", snapShot.child("imageUrl").value as String)
            intent.putExtra("message", snapShot.child("message").value as String)
            intent.putExtra("snapKey", snapShot.key)

            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.snaps, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.createSnap){
            val intent = Intent(this, CreateSnapActivity::class.java)
            startActivity(intent)
        }else if(item?.itemId == R.id.logout){
            mAuth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mAuth.signOut()
    }


}
