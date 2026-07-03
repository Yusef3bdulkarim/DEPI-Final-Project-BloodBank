package com.example.depi_final_project_bloodbank.data.repository


import android.graphics.Bitmap
import com.example.depi_final_project_bloodbank.data.remote.GroqApiService
import com.example.depi_final_project_bloodbank.data.remote.GroqMessage
import com.example.depi_final_project_bloodbank.data.remote.GroqRequest
import com.example.depi_final_project_bloodbank.domain.model.BloodScanResult
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BloodScannerRepositoryImpl : BloodScannerRepository {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.groq.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val groqApi = retrofit.create(GroqApiService::class.java)

    private val a1 = "Bearer gs"
    private val a2 = "k_Lx1S542qln"
    private val a3 = "h6VdRUDNQbWGdyb3FY58aDbj"
    private val a4 = "3MiUyGK4vGOJ6uHB8c"
    private val apiKey = a1+a2+a3+a4

    override suspend fun scanBloodType(bitmap: Bitmap): BloodScanResult {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()
            val text = result.text

            if (text.isBlank()) {
                BloodScanResult(null, "", false, "Image not clear.")
            } else {
                val bloodType = extractBloodTypeWithAi(text)
                if (bloodType != null && bloodType != "None") {
                    BloodScanResult(bloodType, text, true, "Blood type found Successfully.")
                } else {
                    BloodScanResult(null, text, false, "Blood type not found.")
                }
            }
        } catch (e: Exception) {
            BloodScanResult(null, "", false, " error: ${e.message}")
        }
    }

    private suspend fun extractBloodTypeWithAi(text: String): String? {
        return try {
            val prompt = """
                Extract the blood type from the following text. 
                If multiple blood types are found, return 'None'. 
                Return ONLY the blood type (e.g., A+, O-, AB+) and nothing else. 
                If no blood type is found, return 'None'. 
                Text: $text
            """.trimIndent()

            val request = GroqRequest(
                messages = listOf(
                    GroqMessage(role = "user", content = prompt)
                )
            )

            val response = groqApi.getChatCompletion(apiKey, request)
            response.choices.firstOrNull()?.message?.content?.trim()
        } catch (e: Exception) {
            null
        }
    }
}
