package com.pryanikov.archiveapp.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pryanikov.archiveapp.view.viewmodel.* // Імпортуємо всі ViewModel-і
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color

// Імпортуємо ВЕСЬ вміст, який ми створили
import com.pryanikov.archiveapp.view.screens.LoginSheetContent
import com.pryanikov.archiveapp.view.screens.DocumentDetailSheetContent
import com.pryanikov.archiveapp.view.screens.EditDocumentSheetContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    // --- Головні ViewModel ---
    mainViewModel: MainViewModel = hiltViewModel(),
    // Тепер MainScreen створює ViewModel-і для списків
    homeViewModel: HomeViewModel = hiltViewModel(),
    myDocsViewModel: MyDocumentsViewModel = hiltViewModel()
) {
    val mainState by mainViewModel.uiState.collectAsState()
    // --- Отримуємо стани від ViewModel-ів списків ---
    val homeState by homeViewModel.uiState.collectAsState()
    val myDocsState by myDocsViewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Стан для панелі пошуку
    var isSearchActive by remember { mutableStateOf(false) }

    // --- 1. Логіка Діалогових Вікон (Bottom Sheets) ---
    if (mainState.currentSheet != SheetContent.NONE) {
        ModalBottomSheet(
            onDismissRequest = {
                if (mainState.currentUser != null) {
                    mainViewModel.onDismissSheet()
                }
            },
            sheetState = sheetState,
            dragHandle = if (mainState.currentUser != null) {
                { BottomSheetDefaults.DragHandle() }
            } else {
                null
            }
        ) {
            when (mainState.currentSheet) {
                SheetContent.LOGIN -> {
                    LoginSheetContent(viewModel = hiltViewModel())
                }
                SheetContent.DETAILS -> {
                    DocumentDetailSheetContent(
                        mainViewModel = mainViewModel,
                        detailViewModel = hiltViewModel()
                    )
                }
                SheetContent.EDIT -> {
                    EditDocumentSheetContent(
                        mainViewModel = mainViewModel,
                        editViewModel = hiltViewModel()
                    )
                }
                SheetContent.NONE -> {}
            }
        }
    }

    // --- 2. Основний UI (Каркас з висувною панеллю) ---
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = mainState.currentUser != null,
        drawerContent = {
            // --- 2a. Вміст висувної панелі ---
            ModalDrawerSheet {
                Text(
                    "Архів",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()
                // ... (елементи панелі: Всі, Мої, Вихід...)
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AllInbox, contentDescription = "Всі документи") },
                    label = { Text("Всі документи") },
                    selected = mainState.selectedNavItem == MainNavItem.ALL_DOCUMENTS,
                    onClick = {
                        mainViewModel.onNavItemClick(MainNavItem.ALL_DOCUMENTS)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.PersonPin, contentDescription = "Мої документи") },
                    label = { Text("Мої документи") },
                    selected = mainState.selectedNavItem == MainNavItem.MY_DOCUMENTS,
                    onClick = {
                        mainViewModel.onNavItemClick(MainNavItem.MY_DOCUMENTS)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, contentDescription = "Вихід") },
                    label = { Text("Вихід") },
                    selected = false,
                    onClick = {
                        mainViewModel.onLogoutClick()
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        // --- 2b. Власне екран (Каркас) ---
        Scaffold(
            topBar = {
                // Верхня панель (тільки якщо користувач увійшов)
                if (mainState.currentUser != null) {
                    // Анімована зміна між звичайною панеллю та панеллю пошуку
                    AnimatedVisibility(
                        visible = !isSearchActive,
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        DefaultTopAppBar(
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onSearchClick = { isSearchActive = true } // Відкрити пошук
                        )
                    }
                    AnimatedVisibility(
                        visible = isSearchActive,
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        SearchTopAppBar(
                            // Стан пошуку беремо з HomeViewModel
                            query = homeState.searchQuery,
                            // Події пошуку передаємо у HomeViewModel
                            onQueryChange = { query ->
                                homeViewModel.onSearchQueryChanged(query)
                            },
                            onCloseClick = { isSearchActive = false } // Закрити пошук
                        )
                    }
                }
            },
            floatingActionButton = {
                if (mainState.currentUser != null) {
                    FloatingActionButton(
                        onClick = { mainViewModel.onAddDocumentClick() }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Додати документ")
                    }
                }
            }
        ) { innerPadding ->
            // --- Основний контент (Списки) ---
            Surface(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                if (mainState.isAuthLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (mainState.currentUser != null) {
                    LaunchedEffect(mainState.selectedNavItem, mainState.currentUser) {
                        if (mainState.selectedNavItem == MainNavItem.MY_DOCUMENTS) {
                            // Якщо обрано "Мої документи",
                            // даємо команду ViewModel завантажити їх.
                            myDocsViewModel.loadMyDocuments()
                        }
                        // (HomeViewModel завантажує себе сам в init,
                        // що для "Всіх документів" нормально).
                    }
                    when (mainState.selectedNavItem) {
                        MainNavItem.ALL_DOCUMENTS -> {
                            // Передаємо ГОТОВИЙ стан
                            AllDocumentsList(
                                state = homeState,
                                onDocumentClick = { documentId ->
                                    mainViewModel.onDocumentClick(documentId)
                                }
                            )
                        }
                        MainNavItem.MY_DOCUMENTS -> {
                            // Передаємо ГОТОВИЙ стан
                            MyDocumentsList(
                                state = myDocsState,
                                onDocumentClick = { documentId ->
                                    mainViewModel.onDocumentClick(documentId)
                                }
                            )
                        }
                    }
                }
                // (Якщо currentUser == null, тут порожньо, бо діалог входу активний)
            }
        }
    }
}

// --- Допоміжні Composable-функції для TopBar ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopAppBar(
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                "Архів Видавництва",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Меню"
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Пошук"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    TopAppBar(
        title = {
            // Поле вводу для пошуку
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Пошук документів...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Пошук",
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        actions = {
            // Іконка "Закрити"
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Закрити пошук"
                )
            }
        }
    )
}