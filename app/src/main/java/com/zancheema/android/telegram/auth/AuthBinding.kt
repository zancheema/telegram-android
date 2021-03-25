package com.zancheema.android.telegram.auth

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.hbb20.CountryCodePicker

object AuthBinding {
    @BindingAdapter("country_code")
    @JvmStatic
    fun setCountryCode(countryCodePicker: CountryCodePicker, code: Int) {
        countryCodePicker.setCountryForPhoneCode(code)
    }

    @InverseBindingAdapter(attribute = "country_code", event = "attrCountryCodeChanged")
    @JvmStatic
    fun getCountryCode(countryCodePicker: CountryCodePicker): Int {
        return countryCodePicker.selectedCountryCodeAsInt
    }

    @BindingAdapter("country_name")
    @JvmStatic
    fun setCountryName(countryCodePicker: CountryCodePicker, name: String?) {
    }

    @InverseBindingAdapter(attribute = "country_name", event = "attrCountryNameChanged")
    @JvmStatic
    fun getCountryName(countryCodePicker: CountryCodePicker): String? {
        return countryCodePicker.selectedCountryName
    }

    @BindingAdapter("attrCountryCodeChanged")
    @JvmStatic
    fun setCountryCodeChangeListener(
        countryCodePicker: CountryCodePicker,
        attrChanged: InverseBindingListener
    ) {
        countryCodePicker.setOnCountryChangeListener {
            attrChanged.onChange()
        }
    }

    @BindingAdapter("attrCountryNameChanged")
    @JvmStatic
    fun setCountryNameChangedListener(
        countryCodePicker: CountryCodePicker,
        attrChanged: InverseBindingListener
    ) {
        countryCodePicker.setOnCountryChangeListener {
            attrChanged.onChange()
        }
    }
}