package com.example.jetpackplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackplayground.ui.theme.JetpackPlaygroundTheme

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackPlaygroundTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val state by viewModel.viewState.collectAsState()

                    Content(
                        userData = state.userData,
                        fields = state.fields,
                        keyboardController = LocalSoftwareKeyboardController.current,
                        localFocusManager = LocalFocusManager.current
                    ) { field, newValue -> viewModel.onFormInputEntered(field, newValue) }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun Content(
    userData: String,
    fields: List<Field>,
    keyboardController: SoftwareKeyboardController?,
    localFocusManager: FocusManager,
    onValueChanged: (Field, String) -> Unit
) {
    Column {
        LazyColumn {
            itemsIndexed(fields) { index, field ->
                val imeAction = if (index == fields.lastIndex) {
                    ImeAction.Done
                } else ImeAction.Next

                val keyboardActions = KeyboardActions(
                    onNext = { localFocusManager.moveFocus(FocusDirection.Down) },
                    onDone = {
                        localFocusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )

                FormInput(
                    value = field.value,
                    description = field.description,
                    keyboardActions = keyboardActions,
                    imeAction = imeAction
                ) { newValue -> onValueChanged(field, newValue) }
            }
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            text = userData
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun FormInput(
    value: String,
    description: String,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    imeAction: ImeAction = ImeAction.Default,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp
            ),
        value = value,
        label = { Text(text = description) },
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = keyboardActions,
        onValueChange = { newValue -> onValueChanged(newValue) }
    )
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackPlaygroundTheme {
        Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxWidth()) {
            FormInput(
                value = "Android",
                description = "Title",
                onValueChanged = { }
            )
        }
    }
}
