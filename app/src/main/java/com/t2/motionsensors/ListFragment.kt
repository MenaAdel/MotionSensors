package com.t2.motionsensors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.t2.motionsensors.databinding.FragmentListBinding
import java.util.*

class ListFragment : Fragment() {

    private lateinit var bindingList: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingList = FragmentListBinding.inflate(
            inflater,
            container,
            false
        )
        return bindingList.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}