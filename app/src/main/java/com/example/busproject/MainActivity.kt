package com.example.busproject

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.*
import kotlin.random.Random
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoBackground()
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun VideoBackground() {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    exoPlayer.playWhenReady = true
    exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ALL

    val uri = RawResourceDataSource.buildRawResourceUri(R.raw.jarta)
    val mediaItem = MediaItem.fromUri(uri)

    exoPlayer.setMediaItem(mediaItem)
    exoPlayer.prepare()

    var isSimulating by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var personasEnBus by remember { mutableStateOf(0) }
    var personasTransportadas by remember { mutableStateOf(0) }
    val numParadas = 5
    val capacidadBus = 20
    var personasEsperando by remember { mutableStateOf(IntArray(numParadas) { 0 }) }
    var estadisticasParadas by remember { mutableStateOf(IntArray(numParadas) { 0 }) }
    var log by remember { mutableStateOf(listOf<String>()) }
    var totalPersonasEsperando by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    var simulationJob by remember { mutableStateOf<Job?>(null) }

    fun startSimulation() {
        isSimulating = true
        showDialog = false
        log = listOf()
        personasEnBus = 0
        personasTransportadas = 0
        personasEsperando = IntArray(numParadas) { 0 }
        estadisticasParadas = IntArray(numParadas) { 0 }
        totalPersonasEsperando = 0

        simulationJob = scope.launch {
            while (isActive) {
                llegadaPersonas(numParadas, personasEsperando)
                for (parada in 0 until numParadas) {
                    val startTime = getCurrentTime()
                    manejarParada(parada, capacidadBus, personasEsperando, estadisticasParadas, personasEnBus, personasTransportadas)
                    { newLog, newPersonasEnBus, newPersonasTransportadas, newEstadisticasParadas ->
                        val endTime = getCurrentTime()
                        log = log + newLog + "Hora de llegada: $startTime\nHora de salida: $endTime\n"
                        personasEnBus = newPersonasEnBus
                        personasTransportadas = newPersonasTransportadas
                        estadisticasParadas = newEstadisticasParadas
                    }
                    delay(5000)
                }
            }
        }
    }
    fun calcularTotalPersonasEsperando(personasEsperando: IntArray): Int {
        var total = 0
        for (i in personasEsperando.indices) {
            total += personasEsperando[i]
        }
        return total
    }

    fun stopSimulation() {
        isSimulating = false
        simulationJob?.cancel()

       //Revisar esto me tiene jarta
        totalPersonasEsperando = calcularTotalPersonasEsperando(personasEsperando)

        log = log + "Simulación finalizada\n"
        showDialog = true
    }


    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = {
            PlayerView(it).apply {
                useController = false
                player = exoPlayer
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        })

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { startSimulation() },
                enabled = !isSimulating
            ) {
                Text(text = "Iniciar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { stopSimulation() },
                enabled = isSimulating
            ) {
                Text(text = "Detener Simulación")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(log) { logEntry ->
                    Text(text = logEntry)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text("OK")
                        }
                    },
                    title = { Text("Simulación Finalizada") },
                    text = {
                        Column {
                            Text("Personas transportadas: $personasTransportadas")
                            Text("Personas en el bus: $personasEnBus")
                            for (i in 0 until numParadas) {
                                Text("Personas transportadas desde la parada ${i + 1}: ${estadisticasParadas[i]}")
                            }
                            Text("Personas esperando al final de la simulación: $totalPersonasEsperando")
                        }
                    }
                )
            }
        }
    }
}

fun llegadaPersonas(numParadas: Int, personasEsperando: IntArray) {
    for (i in 0 until numParadas) {
        personasEsperando[i] += Random.nextInt(0, 10)
    }
}

fun manejarParada(
    parada: Int,
    capacidadBus: Int,
    personasEsperando: IntArray,
    estadisticasParadas: IntArray,
    personasEnBus: Int,
    personasTransportadas: Int,
    updateState: (newLog: String, newPersonasEnBus: Int, newPersonasTransportadas: Int, newEstadisticasParadas: IntArray) -> Unit
) {
    val log = StringBuilder()
    log.append("Llegada a la parada ${parada + 1}\n")

    val personasLlegan = Random.nextInt(0, 10)
    personasEsperando[parada] += personasLlegan
    log.append("Personas que llegan a la estación: $personasLlegan\n")
    log.append("Personas en espera: ${personasEsperando[parada]}\n")

    val personasDesmontan = Random.nextInt(0, personasEnBus + 1)
    val nuevasPersonasEnBus = personasEnBus - personasDesmontan
    log.append("Personas desmontadas: $personasDesmontan\n")

    val personasSuben = minOf(capacidadBus - nuevasPersonasEnBus, personasEsperando[parada])
    personasEsperando[parada] -= personasSuben
    val personasActualizadasEnBus = nuevasPersonasEnBus + personasSuben
    estadisticasParadas[parada] += personasSuben
    val nuevasPersonasTransportadas = personasTransportadas + personasSuben
    log.append("Personas subidas: $personasSuben\n")

    updateState(log.toString(), personasActualizadasEnBus, nuevasPersonasTransportadas, estadisticasParadas)
}

fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}
