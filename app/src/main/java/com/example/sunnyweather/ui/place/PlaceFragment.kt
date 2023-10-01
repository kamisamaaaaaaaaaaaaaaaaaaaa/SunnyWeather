package com.example.sunnyweather.ui.place

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R

class PlaceFragment : Fragment() {
    val viewModel by lazy {
        ViewModelProvider(this).get(PlaceViewModel::class.java)
    }

    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_place, container, false)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    owner.lifecycle.removeObserver(this)

                    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
                    val layoutManager = LinearLayoutManager(requireActivity())
                    recyclerView.layoutManager = layoutManager
                    val adapter = PlaceAdapter(viewModel.placeList)
                    recyclerView.adapter = adapter

                    val searchPlaceEdit = view.findViewById<EditText>(R.id.searchPlaceEdit)
                    val bgImageView = view.findViewById<ImageView>(R.id.bgImageView)

                    searchPlaceEdit.addTextChangedListener { editText ->
                        val content = editText.toString()
                        if (content.isNotEmpty()) {
                            viewModel.searchPlaces(content)
                        } else {
                            recyclerView.visibility = View.GONE
                            bgImageView.visibility = View.VISIBLE
                            viewModel.placeList.clear()
                            adapter.notifyDataSetChanged()
                        }
                    }

                    viewModel.placeLiveData.observe(this@PlaceFragment, Observer { result ->
                        val places = result.getOrNull()
                        if (places != null) {
                            recyclerView.visibility = View.VISIBLE
                            bgImageView.visibility = View.GONE
                            viewModel.placeList.clear()
                            viewModel.placeList.addAll(places)
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "未查询到任何地点",
                                Toast.LENGTH_SHORT
                            ).show()
                            result.exceptionOrNull()?.printStackTrace()
                        }
                    })
                }
            }
        )
    }
}