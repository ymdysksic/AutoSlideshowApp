package jp.techacademy.yusuke.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 外部ストレージへのアクセス許可取得
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    // スライドショー
    private fun getContentsInfo() {
        var id = mutableListOf<Long>()
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

        if (cursor!!.moveToFirst()) {
            // 全ての画像のidを取得
            var i = 0
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                Log.d("DEBUG_APP", "fieldIndex : " + fieldIndex.toString())
                id.add(i,cursor.getLong(fieldIndex))
                Log.d("DEBUG_APP", "id : " + id[i].toString())
                i++
            }while (cursor.moveToNext())
            i = 0

            // 1つ目の画像を表示
            showImage(id[i])

            // 次へボタン押下
            prog_button.setOnClickListener {
                Log.d("DEBUG_APP", "進むボタン押下")

                // 最後の画像だったら頭に
                if(i == id.size - 1){
                    i = 0
                }
                else {
                    i++
                }
                showImage(id[i])
            }

            // 戻るボタン押下
            prev_button.setOnClickListener {
                Log.d("DEBUG_APP", "戻るボタン押下")

                // 最初の画像の場合は末尾に
                if (i == 0) {
                    i = id.size - 1
                }
                else {
                    i--
                }
                showImage(id[i])
            }

            // 再生/停止ボタン押下
            playstop_button.setOnClickListener {
                Log.d("DEBUG_APP", "再生/停止ボタン押下")

            }
        }
        cursor.close()
    }

    // 画像表示
    private fun showImage(id:Long){
        val imageUri =
            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

        imageView.setImageURI(imageUri)
        Log.d("DEBUG_APP", "URI : " + imageUri.toString())
    }
}
