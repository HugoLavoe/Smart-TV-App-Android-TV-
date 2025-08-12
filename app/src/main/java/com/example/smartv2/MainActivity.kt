package com.example.smartv2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.media.MediaPlayer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import com.example.smartv2.ui.theme.Smartv2Theme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay



class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Smartv2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { MainScreen(navController) }
                        composable("appDetail") { AppDetailScreen(navController) }
                        composable("musicScreen") { MusicScreen(navController) }
                        composable("galleryScreen") { PaginaGaleria(navController) }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavController) {
    val currentDateTime = remember { mutableStateOf(LocalDateTime.now()) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // Dummy weather data
    val weather = remember { mutableStateOf("Victor Hugo Perez Tepox") }

    // Categories and apps data
    val categories = listOf("Home", "My Feed", "News", "Search", "Streaming Channels", "Settings")
    val apps = listOf(
        R.drawable.netflix, R.drawable.hulu, R.drawable.amazon_video,
        R.drawable.hbo_now, R.drawable.galeria, R.drawable.musica
    )

    // Update time every second
    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime.value = LocalDateTime.now()
            delay(1000L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo1), // Replace with your background image resource
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryList(categories, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            AppGrid(apps = apps, onAppClick = { app ->
                when (app) {
                    R.drawable.musica -> navController.navigate("musicScreen")
                    R.drawable.galeria -> navController.navigate("galleryScreen")
                    else -> navController.navigate("appDetail")
                }
            }, modifier = Modifier.weight(3f))
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentDateTime.value.format(formatter),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Nombre: ${weather.value}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun CategoryList(categories: List<String>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.width(200.dp)) {
        items(categories) { category ->
            Text(
                text = category,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun AppGrid(apps: List<Int>, onAppClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        apps.chunked(2).forEach { rowApps ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowApps.forEach { app ->
                    Image(
                        painter = painterResource(id = app),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(100.dp)
                            .clickable { onAppClick(app) }
                    )
                }
            }
        }
// Add the YouTube button separately
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.youtube), // Replace with the ID of the YouTube image resource
                contentDescription = "YouTube",
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
                    .background(Color.White)
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setClassName(
                                    "com.google.android.youtube.tv",
                                    "com.google.android.apps.youtube.tv.activity.ShellActivity"
                                )
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast
                                .makeText(
                                    context,
                                    "YouTube TV app no está instalada o no se puede abrir",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
            )
        }
    }
}

@Composable
fun AppDetailScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { navController.navigate("home") }) {
            Text("Regresar al Inicio")
        }
    }
}

@Composable
fun MusicScreen(navController: NavController) {
    val context = LocalContext.current
    var currentSongIndex by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentPosition by remember { mutableStateOf(0) }

    val songs = listOf(
        Song(R.drawable.album1, R.raw.song1, "It Takes Two"),
        Song(R.drawable.album2, R.raw.song2, "Fast Love"),
        Song(R.drawable.abum3, R.raw.song3, "Si Pudiera")
    )

    fun playSong() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, songs[currentSongIndex].audioResId).apply {
            seekTo(currentPosition)
            start()
        }
        isPlaying = true
    }

    fun pauseSong() {
        mediaPlayer?.pause()
        currentPosition = mediaPlayer?.currentPosition ?: 0
        isPlaying = false
    }

    fun nextSong() {
        currentPosition = 0
        currentSongIndex = (currentSongIndex + 1) % songs.size
        playSong()
    }

    fun previousSong() {
        currentPosition = 0
        currentSongIndex = if (currentSongIndex > 0) currentSongIndex - 1 else songs.size - 1
        playSong()
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = songs[currentSongIndex].backgroundResId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = songs[currentSongIndex].title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { previousSong() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_prev),
                        contentDescription = "Previous",
                        tint = Color.White
                    )
                }
                IconButton(onClick = {
                    if (isPlaying) {
                        pauseSong()
                    } else {
                        playSong()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { nextSong() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_next),
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = { navController.navigate("home") }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    }
}


@Composable
fun Galeria(navController: NavController) {
    var selectedImage by remember { mutableStateOf<Int?>(null) }
    var images by remember { mutableStateOf(listOf(
        R.drawable.ima1, R.drawable.ima2, R.drawable.ima3,
        R.drawable.ima4, R.drawable.ima5, R.drawable.ima6,
        R.drawable.fondo1, R.drawable.ima7
    ))}

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 1.dp)
    ) {
        Button(
            onClick = { navController.navigate("RutaDos") }
        ) {
            Text("<", color = Color.White)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = " Galería ",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(9.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // número de columnas
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(images.size) { index ->
                Image(
                    painter = painterResource(id = images[index]),
                    contentDescription = null,
                    modifier = Modifier
                        .size(77.dp)
                        .clickable { selectedImage = images[index] },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    if (selectedImage != null) {
        Dialog(
            onDismissRequest = { selectedImage = null }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = selectedImage!!),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { selectedImage = null }) {
                            Text("❌")
                        }
                        Button(onClick = {
                            images = images.filterNot { it == selectedImage }
                            selectedImage = null
                        }) {
                            Text("🗑️")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaginaGaleria(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Galeria(navController)

        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier.size(35.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                contentDescription = "Regresar",
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

data class Song(val backgroundResId: Int, val audioResId: Int, val title: String)

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Smartv2Theme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { MainScreen(navController) }
            composable("appDetail") { AppDetailScreen(navController) }
            composable("musicScreen") { MusicScreen(navController) }
            composable("galleryScreen") { PaginaGaleria(navController) }
        }
    }
}
