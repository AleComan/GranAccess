package com.example.granaccess.tareas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.granaccess.ui.theme.GranAccessTheme

class MenuTareas : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GranAccessTheme {
                MainScreen(
                    onNavigateToComanda = {
                        startActivity(Intent(this, ComandaActivity::class.java))
                    },
                    onNavigateToSeleccionarMaterial = {
                        startActivity(Intent(this, SeleccionarMaterialActivity::class.java))
                    },
                    onNavigateToGestionarTareas = {
                        startActivity(Intent(this, GestionTareasActivity::class.java))
                    },
                    onNavigateToNotificaciones = {
                        startActivity(Intent(this, VentanaNotificacionesActivity::class.java))
                    },
                    onNavigateToCrearNotificacion = {
                        startActivity(Intent(this, CrearNotificacionActivity::class.java))
                    },
                    onNavigateToVerListasMaterial = {
                        startActivity(Intent(this, VentanaVerListasMaterial::class.java))
                    },
                    onNavigateToMenu = {
                        startActivity(Intent(this, MenuActivity::class.java))
                    },
                    onNavigateToAsignarMenus = {
                        startActivity(Intent(this, VentanaAsignarMenus::class.java))
                    },
                    onNavigateToVerMenus = {
                        startActivity(Intent(this, VentanaVerMenus::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    onNavigateToComanda: () -> Unit,
    onNavigateToSeleccionarMaterial: () -> Unit,
    onNavigateToGestionarTareas: () -> Unit,
    onNavigateToNotificaciones: () -> Unit,
    onNavigateToCrearNotificacion: () -> Unit,
    onNavigateToVerListasMaterial: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToAsignarMenus: () -> Unit,
    onNavigateToVerMenus: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Bienvenido a GranAccess")

                Button(onClick = onNavigateToComanda, modifier = Modifier.fillMaxWidth()) {
                    Text("Ir a Gestión de Comandas")
                }

                Button(onClick = onNavigateToSeleccionarMaterial, modifier = Modifier.fillMaxWidth()) {
                    Text("Seleccionar Material")
                }

                Button(onClick = onNavigateToGestionarTareas, modifier = Modifier.fillMaxWidth()) {
                    Text("Gestionar Tareas")
                }

                Button(onClick = onNavigateToNotificaciones, modifier = Modifier.fillMaxWidth()) {
                    Text("Notificaciones")
                }

                Button(onClick = onNavigateToCrearNotificacion, modifier = Modifier.fillMaxWidth()) {
                    Text("Crear Notificación")
                }

                Button(onClick = onNavigateToVerListasMaterial, modifier = Modifier.fillMaxWidth()) {
                    Text("Ver Listas Material")
                }

                Button(onClick = onNavigateToMenu, modifier = Modifier.fillMaxWidth()) {
                    Text("Gestión de Menús")
                }

                Button(onClick = onNavigateToAsignarMenus, modifier = Modifier.fillMaxWidth()) {
                    Text("Asignar Menús")
                }

                Button(onClick = onNavigateToVerMenus, modifier = Modifier.fillMaxWidth()) {
                    Text("Ver Menús")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    GranAccessTheme {
        MainScreen({}, {}, {}, {}, {}, {}, {}, {}, {})
    }
}
