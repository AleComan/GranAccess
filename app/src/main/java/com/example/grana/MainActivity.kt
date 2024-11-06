package com.example.grana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun AppContent(modifier: Modifier = Modifier) {
    var mostrarPantallaRegistro by remember { mutableStateOf(false) }
    var mostrarPantallaAdminOptions by remember { mutableStateOf(false) }

    when {
        mostrarPantallaRegistro -> {
            RegistrarUsuarioScreen(onBack = { mostrarPantallaRegistro = false })
        }
        mostrarPantallaAdminOptions -> {
            AdminOptionsScreen(
                onRegisterClickAlumno = { /* Sin acción por ahora */ },
                onRegisterProfesorAdministradorClick = { mostrarPantallaRegistro = true },
                onCreateTasksClick = { /* Sin acción por ahora */ },
                onBack = {
                    mostrarPantallaAdminOptions = false // Volver al inicio de sesión
                }
            )
        }
        else -> {
            LoginScreen(
                onLoginSuccess = { esAdmin ->
                    if (esAdmin) {
                        mostrarPantallaAdminOptions = true
                    } else {
                        // Mensaje de "Inicio de sesión exitoso" para profesores
                        mostrarPantallaRegistro = false
                    }
                }
            )
        }
    }
}






@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: (esAdmin: Boolean) -> Unit
) {
    var nombreUsuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            label = { Text("Nombre de Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            realizarLogin(nombreUsuario, contrasena, snackBarHostState, coroutineScope) { esAdmin ->
                onLoginSuccess(esAdmin)
            }
        }) {
            Text("Iniciar Sesión")
        }
        SnackbarHost(hostState = snackBarHostState)
    }
}

@Composable
fun RegistrarUsuarioScreen(onBack: () -> Unit) {
    RegistrarUsuario()
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = onBack) {
        Text("Panel de control")
    }
}

@Composable
fun RegistrarUsuario(modifier: Modifier = Modifier) {
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var tipoUsuario by remember { mutableStateOf(false) } // false: profesor, true: administrador
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            label = { Text("Nombre de Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("Profesor")
            RadioButton(
                selected = !tipoUsuario,
                onClick = { tipoUsuario = false }
            )
            Text("Administrador")
            RadioButton(
                selected = tipoUsuario,
                onClick = { tipoUsuario = true }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            registrarUsuario(nombre, apellidos, nombreUsuario, contrasena, tipoUsuario, snackBarHostState, coroutineScope)
        }) {
            Text("Registrar Usuario")
        }

        SnackbarHost(hostState = snackBarHostState)
    }
}

class PasswordTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformed = "*".repeat(text.length)
        return TransformedText(AnnotatedString(transformed), OffsetMapping.Identity)
    }
}

fun registrarUsuario(
    nombre: String,
    apellidos: String,
    nombreUsuario: String,
    contrasena: String,
    tipoUsuario: Boolean,
    snackBarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val url = "http://10.0.2.2/miapp/api_register.php"

    val json = JSONObject().apply {
        put("nombre", nombre)
        put("apellidos", apellidos)
        put("nombre_usuario", nombreUsuario)
        put("contrasena", contrasena)
        put("tipo_usuario", tipoUsuario)
    }

    val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            coroutineScope.launch {
                snackBarHostState.showSnackbar("Error de conexión: ${e.message}")
            }
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (response.isSuccessful) {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Registro exitoso.")
                    }
                } else {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Error al registrar el usuario: ${response.message}")
                    }
                }
            }
        }
    })
}


fun realizarLogin(
    nombreUsuario: String,
    contrasena: String,
    snackBarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onResult: (esAdmin: Boolean) -> Unit
) {
    val url = "http://10.0.2.2/miapp/api_login.php"
    val json = JSONObject().apply {
        put("nombre_usuario", nombreUsuario)
        put("contrasena", contrasena)
    }
    val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            coroutineScope.launch {
                snackBarHostState.showSnackbar("Error de conexión: ${e.message}")
            }
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody ?: "{}")
                    val success = jsonResponse.getBoolean("success")

                    if (success) {
                        val tipoUsuario = jsonResponse.getString("tipo_usuario")
                        coroutineScope.launch {
                            if (tipoUsuario == "administrador") {
                                onResult(true) // Avanzar a pantalla de registro
                            } else {
                                snackBarHostState.showSnackbar("Inicio de sesión exitoso") // Profesor
                                onResult(false)
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar("Error: ${jsonResponse.getString("message")}")
                        }
                    }
                } else {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Error en la respuesta: ${response.message}")
                    }
                }
            }
        }
    })
}


@Composable
fun AdminOptionsScreen(
    onRegisterClickAlumno: () -> Unit,
    onRegisterProfesorAdministradorClick: () -> Unit,
    onCreateTasksClick: () -> Unit,
    onBack: () -> Unit // Añadido el parámetro onBack para volver a la pantalla de login
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onRegisterClickAlumno) {
            Text("Registrar Alumno")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRegisterProfesorAdministradorClick) {
            Text("Registrar Profesor/Administrador")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCreateTasksClick) {
            Text("Crear tareas")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {  // Añadido el botón "Iniciar sesión"
            Text("Cerrar sesión")
        }
    }
}

