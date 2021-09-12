package com.example.jetpackplayground

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ViewState {
    data class Content(
        val fields: List<Field> = emptyList(),
        val userData: String = ""
    ) : ViewState()
}

sealed class Field {
    abstract val value: String
    abstract val description: String

    data class FirstName(
        override val value: String = "",
        override val description: String = "First Name"
    ) : Field()

    data class LastName(
        override val value: String = "",
        override val description: String = "Last Name"
    ) : Field()

    data class Dob(
        override val value: String = "",
        override val description: String = "DOB"
    ) : Field()
}

private val DefaultList = listOf(
    Field.FirstName(),
    Field.LastName(),
    Field.Dob()
)

class MainViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(ViewState.Content())
    val viewState = _viewState.asStateFlow()

    init {
        _viewState.value = ViewState.Content(fields = DefaultList)
    }

    fun onFormInputEntered(field: Field, newValue: String) {
        val fields = viewState.value.fields.toMutableList()
        val currentFieldIndex = indexOf(fields, field)

        fields[currentFieldIndex] = when (field) {
            is Field.FirstName -> Field.FirstName(value = newValue)
            is Field.LastName -> Field.LastName(value = newValue)
            is Field.Dob -> Field.Dob(value = newValue)
        }

        _viewState.value = ViewState.Content(fields = fields, userData = buildUserData(fields))
    }

    private fun indexOf(fields: List<Field>, field: Field) =
        fields.first { it == field }.let { fields.indexOf(it) }

    private fun buildUserData(fields: List<Field>) = buildString {
        fields.forEach { field -> append(field.value + " ") }
    }
}
