package com.example.wizzar.presentation.favorites

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizzar.R
import com.example.wizzar.data.dataSource.local.entity.FavoriteLocationEntity
import com.example.wizzar.presentation.common.glassmorphic
import com.example.wizzar.ui.theme.TextWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit, // Navigation event to open the Map Screen
    onNavigateToDetails: (Double, Double) -> Unit // Navigation event to view Weather Details
) {
    val favorites by viewModel.favoritesList.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.favorite_locations_title), color = TextWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = onNavigateToMap) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.add_favorite_desc),
                            tint = TextWhite
                        )
                    }
                },
            )
        }
    ) { paddingValues ->

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.no_favorites_yet),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhite
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = favorites,
                    key = { it.latitude.toString() + it.longitude.toString() }
                ) { favorite ->

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.removeFavorite(favorite)

                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.item_deleted, favorite.cityName),
                                        actionLabel = context.getString(R.string.undo),
                                        duration = SnackbarDuration.Short
                                    )
                                    // If they clicked Undo, restore the item
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.undoRemoveFavorite(favorite)
                                    }
                                }
                                return@rememberSwipeToDismissBoxState true
                            }
                            return@rememberSwipeToDismissBoxState false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val color by animateColorAsState(
                                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Transparent
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color, shape = RoundedCornerShape(16.dp))
                                    .padding(end = 24.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete_desc),
                                )
                            }
                        },
                        content = {
                            FavoriteCityCard(
                                favorite = favorite,
                                onClick = {
                                    onNavigateToDetails(
                                        favorite.latitude,
                                        favorite.longitude
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteCityCard(
    favorite: FavoriteLocationEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.glassmorphic(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(R.string.favorited_city_desc),
                tint = TextWhite
            )
            Text(
                text = favorite.cityName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                color = TextWhite
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = stringResource(R.string.check_details_desc),
                tint = TextWhite
            )
        }
    }
}