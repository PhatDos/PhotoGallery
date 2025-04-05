package com.duyle.uploadimage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.duyle.uploadimage.ui.theme.UploadImageTheme
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UploadImageTheme {
                val samplePhotos = listOf(
                    Uri.parse("android.resource://com.duyle.photographygallery/drawable/my1"),
                    Uri.parse("android.resource://com.duyle.photographygallery/drawable/my2"),
                    Uri.parse("android.resource://com.duyle.photographygallery/drawable/week3")
                )
                PhotoGalleryScreen(photos = samplePhotos)
            }
        }
    }
}

@Composable
fun PhotoGalleryScreen(photos: List<Uri>) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var selectedPhoto by remember { mutableStateOf<Uri?>(null) }
    var photoList by remember { mutableStateOf(photos) }

    // Khởi tạo launcher để chọn ảnh
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoList = photoList + it  // Thêm ảnh vào danh sách
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                photoPickerLauncher.launch("image/*")  // Mở thư viện ảnh
            }) {
                Text("+", fontSize = 40.sp)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (selectedPhoto == null) {
                PhotoGrid(photoList) { selectedPhoto = it }
            } else {
                FullPhotoView(
                    photos = photoList,
                    currentPhoto = selectedPhoto!!,
                    setSelectedPhoto = { selectedPhoto = it },
                    onBack = { selectedPhoto = null }
                )
            }
        }
    }
}


@Composable
fun PhotoGrid(photos: List<Uri>, onPhotoClick: (Uri) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(photos.size) { index ->
            Image(
                painter = rememberAsyncImagePainter(photos[index]),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .size(200.dp)
                    .clickable { onPhotoClick(photos[index]) }
            )
        }
    }
}

@Composable
fun FullPhotoView(
    photos: List<Uri>,
    currentPhoto: Uri,
    setSelectedPhoto: (Uri) -> Unit,  // ✅ Add this parameter
    onBack: () -> Unit
) {
    val currentIndex = photos.indexOf(currentPhoto)
    val previousPhoto = if (currentIndex > 0) photos[currentIndex - 1] else null
    val nextPhoto = if (currentIndex < photos.size - 1) photos[currentIndex + 1] else null

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.size(150.dp))

        Image(
            painter = rememberAsyncImagePainter(currentPhoto),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onBack() },
                        onLongPress = { /* Handle Long Press */ }
                    )
                }
        )

        // Previous and Next buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { previousPhoto?.let { setSelectedPhoto(it) } },
                enabled = previousPhoto != null
            ) {
                Text("Previous")
            }
            Button(
                onClick = { onBack() },
            ) {
                Text("Home")
            }
            Button(
                onClick = { nextPhoto?.let { setSelectedPhoto(it) } },
                enabled = nextPhoto != null
            ) {
                Text("Next")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PhotoGalleryPreview() {
    UploadImageTheme {
        val samplePhotos = listOf(
            Uri.parse("android.resource://com.duyle.photographygallery/drawable/my1"),
            Uri.parse("android.resource://com.duyle.photographygallery/drawable/my2"),
            Uri.parse("android.resource://com.duyle.photographygallery/drawable/week3")
        )
        PhotoGalleryScreen(photos = samplePhotos)
    }
}
