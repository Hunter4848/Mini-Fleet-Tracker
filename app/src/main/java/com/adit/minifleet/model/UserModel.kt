package com.adit.minifleet.model

data class UserModel(
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var alamat: String = "",
    var telepon: String = "",
    var photo: String = ""
) {
    override fun toString(): String {
        return "UserModel(username='$username', email='$email', password='$password', alamat='$alamat', telepon='$telepon', photo='$photo')"
    }
}
