package codialapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import android.widget.Toast
import codialapp.adapters.StudentsAdapter
import ramizbek.aliyev.codialapp.databinding.DialogDeleteBinding
import ramizbek.aliyev.codialapp.databinding.FragmentShowGroupsBinding
import codialapp.db.MyDBHelper
import codialapp.models.Students
import codialapp.utils.MyObject
import codialapp.utils.MyObject.editStudents
import codialapp.utils.MyObject.groups
import ramizbek.aliyev.codialapp.R

class ShowGroupsFragment : Fragment() {
    private lateinit var bindingDelete: DialogDeleteBinding
    lateinit var dialogDelete: AlertDialog
    lateinit var binding: FragmentShowGroupsBinding
    lateinit var myDBHelper: MyDBHelper
    lateinit var arrayListStudents: ArrayList<Students>
    lateinit var adapterStudents: StudentsAdapter
    var booleanAntiBag = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowGroupsBinding.inflate(layoutInflater)

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cardStart.setOnClickListener {
            groups.open = true
            val boolean = myDBHelper.startLessonGroup(groups, requireActivity())
            if (boolean) {
                binding.cardStart.visibility = View.GONE
            }
        }

        binding.lyAdd.setOnClickListener {
            editStudents = false
            findNavController().navigate(R.id.editStudentFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadData()
        showActivity()
        binding.recyclerViewStudents.adapter = adapterStudents
    }

    private fun loadData() {
        myDBHelper = MyDBHelper(requireActivity())
        arrayListStudents = ArrayList()
        arrayListStudents = myDBHelper.readStudents(groups.id!!, groups.open!!)
        adapterStudents = StudentsAdapter(requireActivity(),
            arrayListStudents,
            object : StudentsAdapter.RVClickStudents {
                override fun editStudents(students: Students) {
                    editStudents = true
                    MyObject.students = students
                    findNavController().navigate(R.id.editStudentFragment)
                }

                override fun deleteStudents(students: Students) {
                    if (booleanAntiBag) {
                        buildDialogDelete(students)
                        booleanAntiBag = false
                    }
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun showActivity() {
        binding.tvGroupName.text = groups.name
        binding.tvGroupName2.text = "Guruh nomi: ${groups.name}"
        binding.tvTime.text = "Vaqt: ${groups.times}"
        binding.tvDay.text = "Kunlar: ${groups.days}"
        binding.tvMentor.text =
            "O`qituvchilar: ${groups.mentors!!.name} ${groups.mentors!!.surname}"
        binding.tvCountStudent.text = "Talabalar soni: ${arrayListStudents.size} ta"
        if (groups.open!!) {
            binding.cardStart.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun buildDialogDelete(students: Students) {
        val alertDialog = AlertDialog.Builder(activity)
        bindingDelete = DialogDeleteBinding.inflate(layoutInflater)
        bindingDelete.tvDescription.text = "Bu guruhni oʻchirib tashlamoqchimisiz?"

        bindingDelete.tvCancel.setOnClickListener {
            dialogDelete.cancel()
        }

        bindingDelete.tvDelete.setOnClickListener {
            val boolean = myDBHelper.deleteStudents(students)
            if (boolean) {
                Toast.makeText(requireActivity(), "Muvaffaqiyatli oʻchirildi!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireActivity(), "\n" +
                        "Oʻchirib boʻlmadi", Toast.LENGTH_SHORT).show()
            }
            dialogDelete.cancel()
            onResume()
        }

        alertDialog.setOnCancelListener {
            booleanAntiBag = true
        }

        alertDialog.setView(bindingDelete.root)
        dialogDelete = alertDialog.create()
        dialogDelete.window!!.attributes.windowAnimations = R.style.MyAnimation
        dialogDelete.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogDelete.show()
    }
}