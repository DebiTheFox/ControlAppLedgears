package com.example.controlledgears

import android.animation.Animator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.controlledgears.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLottieAnimation()
    }

    private fun setupLottieAnimation() {
        with(binding.lottieAnimation) {
            // Fix pour la déformation du texte et rendu optimal
            setRenderMode(com.airbnb.lottie.RenderMode.SOFTWARE)

            // Indispensable pour éviter le crash IllegalStateException si des images sont présentes
            imageAssetsFolder = "images/"

            // Fournir les assets manuellement
            setImageAssetDelegate { asset ->
                val drawableResId = when (asset.id) {
                    "3d5f12ddfdb025d77d2da5c9913f90da3d5936c8" -> R.drawable.logo
                    "dc0928eadf3a921c22855452547f5436572aaa08" -> R.drawable.text
                    else -> null
                }

                drawableResId?.let { resId ->
                    val options = BitmapFactory.Options().apply { inScaled = false }
                    val originalBitmap = BitmapFactory.decodeResource(resources, resId, options)
                    originalBitmap?.let {
                        Bitmap.createScaledBitmap(it, asset.width, asset.height, true)
                    }
                } ?: Bitmap.createBitmap(asset.width.coerceAtLeast(1), asset.height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
            }

            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    postDelayed({ navigateToMain() }, 500)
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })

            playAnimation()

            // Sécurité : naviguer après 10 secondes si l'animation ne finit pas
            postDelayed({ navigateToMain() }, 10000)
        }
    }

    private fun navigateToMain() {
        if (!isFinishing) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}