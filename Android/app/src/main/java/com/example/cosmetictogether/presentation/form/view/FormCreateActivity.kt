package com.example.cosmetictogether.presentation.form.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.model.CreateFormRequest
import com.example.cosmetictogether.data.model.Delivery
import com.example.cosmetictogether.data.model.Product
import com.example.cosmetictogether.databinding.ActivityFormWriteBinding
import com.example.cosmetictogether.presentation.form.adapter.DeliveryAdapter
import com.example.cosmetictogether.presentation.form.adapter.ProductAdapter
import com.example.cosmetictogether.presentation.form.viewmodel.FormCreateViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Calendar.*

class FormCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormWriteBinding
    private val viewModel: FormCreateViewModel by viewModels()

    private var thumbnailPart: MultipartBody.Part? = null
    private val imageParts = mutableListOf<MultipartBody.Part>()
    private var productUri: Uri? = null
    private var selectedBank: String = ""
    private var selectedDate: String? = null
    private var editingProduct: Product? = null

    private val productNames = mutableListOf<String>()
    private val prices = mutableListOf<Int>()
    private val stocks = mutableListOf<Int>()
    private val maxPurchaseLimits = mutableListOf<Int>()
    private val deliveryOptions = mutableListOf<String>()
    private val deliveryCosts = mutableListOf<Int>()
    private val newProductUris = mutableListOf<Uri>()

    private lateinit var productAdapter: ProductAdapter
    private lateinit var deliveryAdapter: DeliveryAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productAdapter = ProductAdapter()
        deliveryAdapter = DeliveryAdapter()

        binding.productRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productRecyclerView.adapter = productAdapter

        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.deliveryRecyclerView.adapter = deliveryAdapter

        binding.toolbar.findViewById<View>(R.id.backBtn).setOnClickListener { finish() }

        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_FORM_IMAGE_REQUEST)
        }

        binding.productImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_PRODUCT_IMAGE_REQUEST)
        }

        binding.startDateButton.setOnClickListener { showDatePicker { date -> binding.saleStartDate.text = date } }
        binding.endDateButton.setOnClickListener { showDatePicker { date -> binding.saleEndDate.text = date } }

        productAdapter.onEditClick = { product ->
            editingProduct = product
            binding.productName.setText(product.productName)
            binding.price.setText(product.price.toString())
            binding.stock.setText(product.stock.toString())
            binding.purchaseLimit.setText(product.maxPurchaseLimit.toString())
            binding.addProductButton.text = "수정하기"
        }

        binding.addProductButton.setOnClickListener {
            val name = binding.productName.text.toString()
            val productPrice = binding.price.text.toString().toIntOrNull() ?: 0
            val productStock = binding.stock.text.toString().toIntOrNull() ?: 0
            val limit = binding.purchaseLimit.text.toString().toIntOrNull() ?: 0

            if (editingProduct == null) {
                productNames.add(name)
                prices.add(productPrice)
                stocks.add(productStock)
                maxPurchaseLimits.add(limit)
                productUri?.let { newProductUris.add(it) }
                viewModel.addProduct(Product(productUri, name, productPrice, productStock, limit))
            } else {
                val updatedProduct = editingProduct!!.copy(productName = name, price = productPrice, stock = productStock, maxPurchaseLimit = limit)
                viewModel.updateProduct(updatedProduct)
                binding.addProductButton.text = "상품 등록"
                editingProduct = null
            }
            resetProductInputs()
        }

        binding.addDeliveryButton.setOnClickListener {
            val option = binding.deliveryOption.text.toString()
            val cost = binding.deliveryCost.text.toString().toIntOrNull() ?: 0
            if (option.isNotBlank()) {
                deliveryOptions.add(option)
                deliveryCosts.add(cost)
                viewModel.addDelivery(Delivery(option, cost))
                resetDeliveryInputs()
            }
        }

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.bank_list,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBank.adapter = adapter

        binding.spinnerBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedBank = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        productAdapter.onDeleteClick = {
            viewModel.removeProduct(it)
            resetProductInputs()
        }

        deliveryAdapter.onDeleteClick = {
            viewModel.removeDelivery(it)
            resetProductInputs()
        }

        viewModel.productList.observe(this, Observer {
            productAdapter.submitList(it)
        })

        viewModel.deliveryList.observe(this, Observer {
            deliveryAdapter.submitList(it)
        })

        binding.addFormButton.setOnClickListener {
            val request = CreateFormRequest(
                title = binding.formTitle.text.toString(),
                formDescription = binding.formDescription.text.toString(),
                startDate = binding.saleStartDate.text.toString(),
                endDate = binding.saleEndDate.text.toString(),
                productName = productNames,
                price = prices,
                stock = stocks,
                maxPurchaseLimit = maxPurchaseLimits,
                deliveryOption = deliveryOptions,
                deliveryCost = deliveryCosts,
                deliveryInstructions = binding.deliveryInstruction.text.toString(),
                bankName = selectedBank,
                accountNumber = binding.etAccountNumber.text.toString()
            )

            if (thumbnailPart != null) {
                viewModel.submitForm(getToken(), thumbnailPart!!, request, imageParts)
            }

            viewModel.createFormResponse.observe(this) { response ->
                response?.let {
                    Toast.makeText(this, "폼 작성이 완료되었습니다", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, FormActivity::class.java))
                    finish()
                } ?: Toast.makeText(this, "폼 작성 실패: 응답이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                PICK_FORM_IMAGE_REQUEST -> {
                    val uri = data.data
                    val file = uri?.let { uriToFile(it) }
                    val requestBody = file?.asRequestBody("image/*".toMediaTypeOrNull())
                    thumbnailPart = requestBody?.let {
                        MultipartBody.Part.createFormData("thumbnail", file.name, it)
                    }
                    binding.formThumbnailImageView.setImageURI(uri)
                    binding.formThumbnailImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                }
                PICK_PRODUCT_IMAGE_REQUEST -> {
                    productUri = data.data
                    val file = productUri?.let { uriToFile(it) }
                    if (file != null && file.exists()) {
                        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("images", file.name, requestBody)
                        imageParts.add(part)
                        binding.productImageView.setImageURI(productUri)
                        binding.productImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                }
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val date = "$year-${month + 1}-$day"
            onDateSelected(date)
        }, calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH)).show()
    }

    private fun resetProductInputs() {
        binding.productName.text.clear()
        binding.price.text.clear()
        binding.stock.text.clear()
        binding.purchaseLimit.text.clear()
    }

    private fun resetDeliveryInputs() {
        binding.deliveryOption.text.clear()
        binding.deliveryCost.text.clear()
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return "Bearer $token"
    }

    fun uriToFile(uri: Uri): File {
        val inputStream = applicationContext.contentResolver.openInputStream(uri) ?: throw NullPointerException("InputStream이 null입니다.")
        val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
        tempFile.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
        return tempFile
    }

    companion object {
        private const val PICK_FORM_IMAGE_REQUEST = 1
        private const val PICK_PRODUCT_IMAGE_REQUEST = 2
    }
}
