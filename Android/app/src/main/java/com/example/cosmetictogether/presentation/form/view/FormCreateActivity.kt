package com.example.cosmetictogether.presentation.form.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.model.CreateFormRequest
import com.example.cosmetictogether.data.model.Delivery
import com.example.cosmetictogether.data.model.Product
import com.example.cosmetictogether.presentation.form.adapter.DeliveryAdapter
import com.example.cosmetictogether.presentation.form.adapter.ProductAdapter
import com.example.cosmetictogether.presentation.form.viewmodel.FormCreateViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Calendar.*

class FormCreateActivity: AppCompatActivity() {
    private val viewModel: FormCreateViewModel by viewModels()

    // UI 요소 선언
    private lateinit var formThumbnailImageView: ImageView
    private lateinit var selectImageButton: Button
    private var thumbnailPart: MultipartBody.Part? = null

    private var thumbnailsUri: Uri? = null


    private lateinit var formTitle: EditText
    private lateinit var formDescription: EditText

    private  lateinit var saleStartDate: TextView
    private lateinit var startDateButton: ImageButton

    private  lateinit var saleEndDate: TextView
    private lateinit var endDateButton: ImageButton

    private var selectedDate: String? = null

    private lateinit var productImageView: ImageView
    private lateinit var productImageButton: Button
    private val imageParts = mutableListOf<MultipartBody.Part>()

    private var productUri: Uri? = null

    private lateinit var productName: EditText
    private lateinit var price: EditText
    private lateinit var stock: EditText
    private lateinit var purchaseLimit: EditText
    private lateinit var deliveryOption: EditText
    private lateinit var deliveryCost: EditText
    private lateinit var addProductButton: Button
    private lateinit var addDeliveryButton: Button
    private lateinit var addFormButton: Button
    private lateinit var deliveryInstruction: EditText

    private lateinit var productAdapter: ProductAdapter
    private lateinit var deliveryAdapter: DeliveryAdapter

    private var editingProduct: Product? = null

    // 데이터 저장할 리스트
    private val productNames = mutableListOf<String>()
    private val prices = mutableListOf<Int>()
    private val stocks = mutableListOf<Int>()
    private val maxPurchaseLimits = mutableListOf<Int>()
    private val deliveryOptions = mutableListOf<String>()
    private val deliveryCosts = mutableListOf<Int>()
    private val newProductUris = mutableListOf<Uri>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_write)

        // UI 요소 초기화
        formThumbnailImageView = findViewById(R.id.formThumbnailImageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        formTitle = findViewById(R.id.formTitle)
        formDescription = findViewById(R.id.formDescription)
        saleStartDate = findViewById(R.id.saleStartDate)
        startDateButton = findViewById(R.id.startDateButton)
        saleEndDate = findViewById(R.id.saleEndDate)
        endDateButton = findViewById(R.id.endDateButton)
        productImageView = findViewById(R.id.productImageView)
        productImageButton = findViewById(R.id.productImageButton)
        productName = findViewById(R.id.productName)
        price = findViewById(R.id.price) // 가격
        stock = findViewById(R.id.stock) // 재고
        purchaseLimit = findViewById(R.id.purchaseLimit) // 최대 구매 갯수
        deliveryOption = findViewById(R.id.deliveryOption) // 배송 방법
        deliveryCost = findViewById(R.id.deliveryCost) // 배송비
        addProductButton = findViewById(R.id.addProductButton) // 상품 추가 버튼
        addDeliveryButton = findViewById(R.id.addDeliveryButton) // 배송 추가 버튼
        deliveryInstruction = findViewById(R.id.deliveryInstruction)
        addFormButton = findViewById(R.id.addFormButton) // 폼 생성 버튼
        val productRecyclerView = findViewById<RecyclerView>(R.id.productRecyclerView)
        val deliveryRecyclerView = findViewById<RecyclerView>(R.id.deliveryRecyclerView)


        // RecyclerView 설정
        productAdapter = ProductAdapter()
        deliveryAdapter = DeliveryAdapter()

        productRecyclerView.layoutManager = LinearLayoutManager(this)
        productRecyclerView.adapter = productAdapter

        deliveryRecyclerView.layoutManager = LinearLayoutManager(this)
        deliveryRecyclerView.adapter = deliveryAdapter

        // 폼 썸네일 이미지 버튼
        selectImageButton.setOnClickListener {
            Log.d("버튼 클릭", "통과")
            // 갤러리에서 이미지 선택
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*" // 이미지 파일만 선택
            startActivityForResult(intent, PICK_FORM_IMAGE_REQUEST)
        }

        // 상품 이미지 버튼
        productImageButton.setOnClickListener {
            // 갤러리에서 이미지 선택
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*" // 이미지 파일만 선택
            startActivityForResult(intent, PICK_PRODUCT_IMAGE_REQUEST)
        }

        // 날짜 선택 버튼 클릭 리스너
        startDateButton.setOnClickListener {
            val calendar = getInstance()
            val year = calendar.get(YEAR)
            val month = calendar.get(MONTH)
            val day = calendar.get(DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                saleStartDate.text = selectedDate
            }, year, month, day).show()
        }

        // 날짜 선택 버튼 클릭 리스너
        endDateButton.setOnClickListener {
            val calendar = getInstance()
            val year = calendar.get(YEAR)
            val month = calendar.get(MONTH)
            val day = calendar.get(DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                saleEndDate.text = selectedDate
            }, year, month, day).show()
        }

        // 상품 수정 버튼 클릭 시 EditText에 값 설정
        productAdapter.onEditClick = { product ->
            editingProduct = product
            productName.setText(product.productName)
            price.setText(product.price.toString())
            stock.setText(product.stock.toString())
            purchaseLimit.setText(product.maxPurchaseLimit.toString())
            addProductButton.text = "수정하기" // 버튼 텍스트 수정
        }

        // 상품 추가 버튼 클릭 리스너
        addProductButton.setOnClickListener {
            val productName = productName.text.toString()
            val productPrice = price.text.toString().toIntOrNull() ?: 0
            val productStock = stock.text.toString().toIntOrNull() ?: 0
            val limit = purchaseLimit.text.toString().toIntOrNull() ?: 0

            if (editingProduct == null) {
                // 새로운 상품 추가
                productNames.add(productName)
                prices.add(productPrice)
                stocks.add(productStock)
                maxPurchaseLimits.add(limit)
                productUri?.let { it1 -> newProductUris.add(it1) }
                viewModel.addProduct(Product(productUri, productName, productPrice, productStock, limit))
            } else {
                // 기존 상품 수정
                val updatedProduct = editingProduct!!.copy(productName = productName, price = productPrice, stock = productStock, maxPurchaseLimit = limit)
                viewModel.updateProduct(updatedProduct)
                addProductButton.text = "상품 등록"
                editingProduct = null
            }
            resetProductInputs()
        }

        // 배송 방법 추가 버튼 클릭 리스너
        addDeliveryButton.setOnClickListener {
            val option = deliveryOption.text.toString()
            val cost = deliveryCost.text.toString().toIntOrNull() ?: 0

            if (option.isNotBlank()) {
                // 배송 정보 저장
                deliveryOptions.add(option)
                deliveryCosts.add(cost)
                val delivery = Delivery(option, cost)
                viewModel.addDelivery(delivery)
                resetDeliveryInputs()
            }
        }

        // 삭제 버튼 클릭 시 호출될 리스너 설정
        productAdapter.onDeleteClick = { product ->
            viewModel.removeProduct(product)
            resetProductInputs()
        }

        // 삭제 버튼 클릭 시 호출될 리스너 설정
        deliveryAdapter.onDeleteClick = { delivery ->
            viewModel.removeDelivery(delivery)
            resetProductInputs()
        }

        // LiveData 관찰하여 UI 갱신
        viewModel.productList.observe(this, Observer {
            productAdapter.submitList(it)
        })

        viewModel.deliveryList.observe(this, Observer {
            deliveryAdapter.submitList(it)
        })

        // 폼 만들기 버튼 클릭: API 호출
        addFormButton.setOnClickListener {
            val CreateFormRequest = CreateFormRequest(
                title = formTitle.text.toString(),
                formDescription = formDescription.text.toString(),
                startDate = saleStartDate.text.toString(),
                endDate = saleEndDate.text.toString(),
                productName = productNames,
                price = prices,
                stock = stocks,
                maxPurchaseLimit = maxPurchaseLimits,
                deliveryOption = deliveryOptions,
                deliveryCost = deliveryCosts,
                deliveryInstructions = deliveryInstruction.text.toString()
            )

            // API 호출
            if (thumbnailPart != null) {
                viewModel.submitForm(
                    token = getToken(),
                    thumbnail = thumbnailPart!!,
                    request = CreateFormRequest,
                    images = imageParts
                )
            }

            viewModel.createFormResponse.observe(this) {response ->
                response?.let {
                    Toast.makeText(this, "폼 작성이 완료되었습니다", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, FormActivity::class.java))
                    finish()
                } ?: run {
                    Toast.makeText(this, "폼 작성 실패: 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // onActivityResult()에서 이미지 선택 후 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                PICK_FORM_IMAGE_REQUEST -> {
                    val formThumbnailUri = data.data // 선택된 폼 썸네일 이미지 URI

                    // URI에서 파일 경로 추출
                    val file = formThumbnailUri?.let { uriToFile(it) }

                    // File을 RequestBody로 변환
                    val requestBody = file?.asRequestBody("image/*".toMediaTypeOrNull())

                    // MultipartBody.Part로 변환
                    thumbnailPart =
                        requestBody?.let {
                            MultipartBody.Part.createFormData("thumbnail", file.name, it)
                        }

                    // formImageView에 선택된 이미지 표시
                    formThumbnailImageView.setImageURI(formThumbnailUri)
                    formThumbnailImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                }
                PICK_PRODUCT_IMAGE_REQUEST -> {
                    productUri = data.data // 선택된 상품 이미지 URI
                    val file = productUri?.let { uriToFile(it) }
                    if (file != null && file.exists()) {
                        // File을 RequestBody로 변환
                        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

                        // MultipartBody.Part로 변환하여 리스트에 추가
                        val productImagePart = MultipartBody.Part.createFormData("images", file.name, requestBody)
                        imageParts.add(productImagePart)

                        // productImageView에 선택된 이미지 표시
                        productImageView.setImageURI(productUri)
                        productImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                }
            }
        }
    }

    // EditText 초기화 메서드
    private fun resetProductInputs() {
        productName.text.clear()
        price.text.clear()
        stock.text.clear()
        purchaseLimit.text.clear()
    }

    private fun resetDeliveryInputs() {
        deliveryOption.text.clear()
        deliveryCost.text.clear()
    }

    companion object {
        private val PICK_FORM_IMAGE_REQUEST = 1
        private val PICK_PRODUCT_IMAGE_REQUEST = 2
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        val authToken = "Bearer $token"
        return authToken
    }

    fun uriToFile(uri: Uri): File {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: throw NullPointerException("InputStream이 null입니다.")

        // 캐시 디렉토리에 임시 파일 생성
        val tempFile = File.createTempFile("temp_image", ".jpg", applicationContext.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        return tempFile
    }
}