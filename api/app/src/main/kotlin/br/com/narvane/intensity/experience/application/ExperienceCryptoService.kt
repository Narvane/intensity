package br.com.narvane.intensity.experience.application

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Service
class ExperienceCryptoService(
    @Value("\${security.intensity.experience-encryption-key}") key: String
) {
    private val secret = SecretKeySpec(normalizeKey(key), "AES")

    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secret)
        val encrypted = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(encrypted)
    }

    fun decrypt(cipherText: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secret)
        val decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText))
        return String(decrypted, StandardCharsets.UTF_8)
    }

    fun md5(value: String): String {
        val digest = MessageDigest.getInstance("MD5").digest(value.toByteArray(StandardCharsets.UTF_8))
        return digest.joinToString("") { each -> "%02x".format(each) }
    }

    private fun normalizeKey(value: String): ByteArray {
        val source = value.toByteArray(StandardCharsets.UTF_8)
        return ByteArray(16).also { target ->
            System.arraycopy(source, 0, target, 0, minOf(source.size, target.size))
        }
    }
}
