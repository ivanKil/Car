package com.kusch.car.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kusch.car.R
import com.kusch.car.databinding.MainFragmentBinding


class MainFragment : Fragment() {
    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var screenWidth = 0
    private var screenHeight = 0
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val trackField = requireActivity().findViewById<FrameLayout>(R.id.track_field)
        trackField.viewTreeObserver.addOnGlobalLayoutListener { initView(trackField) }
    }

    private fun initView(trackField: FrameLayout) {
        screenWidth = trackField.width
        screenHeight = trackField.height
        binding.imgCar.setOnClickListener {
            viewModel.initTrack(Pair(binding.imgCar.x, binding.imgCar.y))
        }
        binding.imgCar.x = screenWidth / 10.0F
        binding.imgCar.y = (screenHeight - 200).toFloat()

        viewModel.setScreenSize(screenWidth, screenHeight)
        viewModel.setCarSize(binding.imgCar.width)
        viewModel.ldTargetPosition.observe(viewLifecycleOwner) {
            binding.imgTarget.x = it.first + binding.imgCar.width / 2
            binding.imgTarget.y = it.second + binding.imgCar.height / 2
        }
        viewModel.ldTrack.observe(viewLifecycleOwner) {
            moveToPoint(it.angDegree, it.target, it.rotateMove)
        }
    }

    private fun moveToPoint(
        angDegree: Float,
        target: Pair<Float, Float>,
        rotateMove: Pair<Float, Float>
    ) {
        binding.imgCar.animate().x(binding.imgCar.x + rotateMove.first)
            .y(binding.imgCar.y + rotateMove.second).rotation(angDegree)
            .setDuration(1000).withEndAction {
                binding.imgCar.animate().x(target.first).y(target.second)
                    .setDuration(2000).withEndAction { viewModel.calcNextTarget() }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}