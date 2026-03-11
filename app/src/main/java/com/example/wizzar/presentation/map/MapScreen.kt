package com.example.wizzar.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizzar.domain.model.LocationSearchResult
import com.example.wizzar.presentation.map.view.MapUiEvent
import com.example.wizzar.presentation.map.view.MapViewModel
import com.example.wizzar.ui.theme.PrimaryBlue
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateBack: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Initialize OSMDroid Configuration (Required)
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    // Navigation Event Observer
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MapUiEvent.NavigateBackWithSuccess -> {
                    onNavigateBack(event.message)
                }
            }
        }
    }

    // THE ENTIRE SCREEN IS WRAPPED IN THIS BOX
    Box(modifier = Modifier.fillMaxSize()) {

        // --- 1. THE FREE OPENSTREETMAP ---
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(5.0)
                    // Set center to Cairo/Egypt as default
                    controller.setCenter(GeoPoint(30.0444, 31.2357))

                    // Click Listener to drop a pin
                    val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            viewModel.onMapPinDropped(p.latitude, p.longitude)
                            return true
                        }

                        override fun longPressHelper(p: GeoPoint): Boolean = false
                    })
                    overlays.add(eventsOverlay)
                }
            },
            update = { mapView ->
                // Update Marker when location is selected
                state.selectedLocation?.let { loc ->
                    mapView.overlays.removeIf { it is Marker }
                    val marker = Marker(mapView)
                    marker.position = GeoPoint(loc.latitude, loc.longitude)
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = loc.name
                    mapView.overlays.add(marker)
                    mapView.controller.animateTo(GeoPoint(loc.latitude, loc.longitude))
                }
            }
        )

        // --- 2. THE FLOATING SEARCH BAR & DROPDOWN ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("Search for a city...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Auto-complete Dropdown Results
            if (state.searchResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .heightIn(max = 250.dp)
                ) {
                    items(state.searchResults) { result ->
                        SearchResultItem(
                            result = result,
                            onClick = { viewModel.onSearchResultClicked(result) }
                        )
                    }
                }
            }
        }

        // --- 3. THE LOADING INDICATOR ---
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center), // This now works too!
                color = PrimaryBlue
            )
        }

        // --- 4. THE CONFIRMATION BUTTON ---
        if (state.selectedLocation != null) {
            Button(
                onClick = viewModel::onConfirmLocationClicked,
                modifier = Modifier
                    .align(Alignment.BottomCenter) // And this!
                    .padding(bottom = 32.dp, start = 32.dp, end = 32.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Select ${state.selectedLocation?.localizedName ?: state.selectedLocation?.name}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun SearchResultItem(result: LocationSearchResult, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        val displayName = result.localizedName ?: result.name
        Text(text = displayName, style = MaterialTheme.typography.bodyLarge)
        if (result.state != null || result.country.isNotBlank()) {
            Text(
                text = listOfNotNull(result.state, result.country).joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}