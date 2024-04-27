package com.example.common.utils




fun String.isValidEmail(): Boolean {
    val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    return this.matches(emailRegex.toRegex())
}



fun String.isValidPassword(): Boolean = this.length > 6


fun String.isValidPhoneNumber(): Boolean {
    val phoneRegex = "^[0-9]{10,15}$"
    return this.matches(phoneRegex.toRegex())
}


fun String.isValidBio(minLength: Int = 6): Boolean = this.length > minLength
