package com.example.noteapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ticket.*
import kotlinx.android.synthetic.main.ticket.view.*
import kotlinx.android.synthetic.main.ticket.view.ivDelete

class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add dummy data
//        listNotes.add(Note(1, "Name's note 1", "Some description here 1"))
//        listNotes.add(Note(2, "Name's note 2", "Some description here 2"))
//        listNotes.add(Note(3, "Name's note 3", "Some description here 3"))
        //Load from database
        LoadQuery("%")//Pesquisa qualquer coisa, % indica isso

    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

    fun LoadQuery(title:String){
        var dbManager=DbManager(this)
        val projections= arrayOf("ID", "Title", "Description")
        val selectionArgs= arrayOf(title)
        val cursor=dbManager.Query(projections, "Title like ?", selectionArgs, "Title")
        listNotes.clear()

        if(cursor.moveToFirst()){
            do {
                val ID=cursor.getInt(cursor.getColumnIndex("ID"))
                val Title=cursor.getString(cursor.getColumnIndex("Title"))
                val Description=cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID, Title, Description))
            }while (cursor.moveToNext())
        }

        var myNotesAdapter = MyNotesAdapter(this, listNotes)
        lvNotes.adapter=myNotesAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val sv = menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(applicationContext, query, Toast.LENGTH_LONG).show()
                LoadQuery("%$query%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item != null){
            when(item.itemId){
                R.id.addNote->{
                    //Go to the add page
                    var intent = Intent(this, AddNotes::class.java)
                    startActivity(intent)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter:BaseAdapter{
        var listNotesAdapter=ArrayList<Note>()
        var context:Context?=null
        constructor(context:Context, listNotesAdapter:ArrayList<Note>):super(){
            this.listNotesAdapter = listNotesAdapter
            this.context=context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var myView=layoutInflater.inflate(R.layout.ticket, null)    //Conecta ao XML passado como parâmetro
            var myNote=listNotesAdapter[position]
            myView.tvTitle.text=myNote.nodeName
            myView.tvDes.text=myNote.nodeDes
            myView.ivDelete.setOnClickListener(View.OnClickListener {
                var dbManager = DbManager(this.context!!)
                val selectionArgs= arrayOf(myNote.nodeID.toString())
                dbManager.Delete("ID=?", selectionArgs)
                LoadQuery("%")
            })
            myView.ivEdit.setOnClickListener(View.OnClickListener {
                GoToUpdate(myNote)
            })
            return myView
        }

        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }
    }

    fun GoToUpdate(note:Note){
        var intent = Intent(this, AddNotes::class.java)
        intent.putExtra("ID", note.nodeID)
        intent.putExtra("name", note.nodeName)
        intent.putExtra("des", note.nodeDes)
        startActivity(intent)
    }

}
