package net.ezra.ui.students

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.ezra.navigation.ROUTE_ADD_STUDENTS
import net.ezra.navigation.ROUTE_HOME
import net.ezra.navigation.ROUTE_VIEW_STUDENTS


data class Item(

    val imageUrl: String? = "",
    val studentName: String? = "",
    val studentClass: String? = "",
    val studentEmail: String? = "",
    val studentHomeAddress: String? = ""

    )


class FirestoreViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    private val itemsCollection = firestore.collection("Students")

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    init {
        fetchItems()
    }

    fun fetchItems() {
        itemsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirestoreViewModel", "Error fetching items", error)
                return@addSnapshotListener
            }

            val itemList = mutableListOf<Item>()
            snapshot?.documents?.forEach { document ->
                val item = document.toObject(Item::class.java)?.copy(studentClass = document.id)
                item?.let {
                    itemList.add(it)
                }
            }
            _items.value = itemList
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemList(items: List<Item>) {


//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(text = "Top App Bar")
//                },
//                navigationIcon = {
//                    IconButton(onClick = {}) {
//                        Icon(Icons.Filled.ArrowBack, "backIcon")
//                    }
//                },
////                backgroundColor = MaterialTheme.colors.primary,
////                contentColor = Color.White,
////                elevation = 10.dp
//            )
//        }, content = {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color(0xff8d6e63)),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Content of the page",
//                    fontSize = 30.sp,
//                    color = Color.White
//                )
//            }
//
//        })

    Column(
        modifier = Modifier
            .background(Color.White)
    ) {

        Text(text = "Student List")


        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .background(Color.White)
                .padding(10.dp)
        ) {

            items.forEach { item ->
                item {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                    ) {


                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.imageUrl)
                                .crossfade(true)
                                .build(),
                            loading = {
                                CircularProgressIndicator()
                            },
                            contentDescription = item.studentName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10))
                                .size(150.dp)
                        )

                        item.studentName?.let { Text(text = it) }
                        item.studentClass?.let { Text(text = it) }
                        item.studentEmail?.let { Text(text = it) }
                        item.studentHomeAddress?.let { Text(text = it) }

                    }

                }
            }


        }
    }
}


@Composable
fun StudentList(navController: NavHostController, viewModel: FirestoreViewModel) {
    val items by viewModel.items.observeAsState(initial = emptyList())

    // Fetch items when the composable is first created
    LaunchedEffect(viewModel, key2 = true) {
        viewModel.fetchItems()
    }


    Column(
        modifier = Modifier
            .background(Color.White)
    ) {

        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_VIEW_STUDENTS) { inclusive = true }
                    }
                },
            text = "go home",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )



        ItemList(items)

    }


}