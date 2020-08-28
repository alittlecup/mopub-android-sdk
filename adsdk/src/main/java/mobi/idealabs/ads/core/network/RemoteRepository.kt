package mobi.idealabs.ads.core.network

import android.util.Base64
import com.google.gson.JsonObject
import io.reactivex.Flowable
import mobi.idealabs.ads.core.bean.DeviceInfo
import mobi.idealabs.ads.core.bean.UserLevelResult
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


/**
 * 直接请求:
 * Content-Type: application/json
 * 请在请求头中加 group : idealabs.mobi
 * 报文内容参照给出的接口文档
 */
object RemoteRepository {
    val service: AdService
    private const val bpUrl = "https://api.idealabs.mobi/"

    init {
        val httpLogInterceptor = HttpLoggingInterceptor()
        httpLogInterceptor.level = if (true) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        var okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLogInterceptor)
            .addInterceptor(EncryptendIntenrceptor())
            .build()
        var retrofit = Retrofit.Builder()
            .baseUrl(bpUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
        service = retrofit.create(AdService::class.java)
    }
}

interface AdService {
    @POST("devices")
    fun postDeviceInfo(@Body deviceInfo: DeviceInfo): Flowable<ResponseBody>

    @POST("events")
    fun postEventInfo(@Body eventInfo: JsonObject): Flowable<ResponseBody>

    /**
     * @param bundle : android 包名 , ios bundleID
     * @param device ad id : android gaid, ios idfa, 都为空则为 UUID
     */
    @GET("https://restrict.idealabs.mobi/i/app_state")
    fun loadCurrentUserAdLevel(
        @Query("bundle") bundle: String,
        @Query("uuid") device: String,
        @Query("ge_id") geId: String
    ): Flowable<UserLevelResult>
}

class EncryptendIntenrceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var body = chain.request().body
        body?.apply {
            val buffer = Buffer()
            body.writeTo(buffer)
            val contentType = body.contentType()
            val charset: Charset = contentType?.charset(StandardCharsets.UTF_8)
                ?: StandardCharsets.UTF_8

            var content = buffer.readString(charset)
            var encrypt = encrypt(content)
            var toRequestBody = encrypt.toRequestBody()
            var request = chain.request().newBuilder().post(toRequestBody).build()
            return chain.proceed(request)
        }
        return chain.proceed(chain.request())

    }

    val charArrays = intArrayOf(
        0x66,
        0x65,
        0x32,
        0x6f,
        0x76,
        0x78,
        0x10,
        0x53,
        0x72,
        0x68,
        0x53,
        0x4d,
        0xa6,
        0x9d,
        0xa7,
        0xd6
    )


    fun encrypt(content: String): String {
        val plaintext: ByteArray = content.toByteArray()
        val key = SecretKeySpec(loadEncryptKey(charArrays).toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val ciphertext: ByteArray = cipher.doFinal(plaintext)
        return Base64.encodeToString(ciphertext, Base64.DEFAULT)
    }

    private fun loadEncryptKey(intArray: IntArray): String {
        val newIntArray =
            intArray.mapIndexed { index, it -> it xor index * index % 255 }.toIntArray()
        return String(newIntArray, 0, 16)
    }

}