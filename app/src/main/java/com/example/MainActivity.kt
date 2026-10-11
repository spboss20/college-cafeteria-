package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.ItemDetailScreen
import com.example.ui.screens.LiveTrackingScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.OopsAcademicScreen
import com.example.ui.screens.OrderConfirmationScreen
import com.example.ui.screens.OutletSelectScreen
import com.example.ui.screens.PaymentSimulationScreen
import com.example.ui.screens.StudentProfileScreen
import com.example.ui.screens.VendorDashboardScreen
import com.example.ui.screens.VendorLoginScreen
import com.example.ui.screens.VendorQrScannerScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CampusBiteViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: CampusBiteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val userMessage by viewModel.userMessage.collectAsState()

                LaunchedEffect(userMessage) {
                    userMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        viewModel.clearToast()
                    }
                }

                CampusBiteApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CampusBiteApp(viewModel: CampusBiteViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            Screen.WELCOME -> WelcomeScreen(viewModel = viewModel)
            Screen.OUTLET_SELECT -> OutletSelectScreen(viewModel = viewModel)
            Screen.MENU -> MenuScreen(viewModel = viewModel)
            Screen.ITEM_DETAIL -> ItemDetailScreen(viewModel = viewModel)
            Screen.CART -> CartScreen(viewModel = viewModel)
            Screen.CHECKOUT -> CheckoutScreen(viewModel = viewModel)
            Screen.PAYMENT_SIMULATION -> PaymentSimulationScreen(viewModel = viewModel)
            Screen.ORDER_CONFIRMATION -> OrderConfirmationScreen(viewModel = viewModel)
            Screen.LIVE_TRACKING -> LiveTrackingScreen(viewModel = viewModel)
            Screen.STUDENT_PROFILE -> StudentProfileScreen(viewModel = viewModel)
            Screen.VENDOR_LOGIN -> VendorLoginScreen(viewModel = viewModel)
            Screen.VENDOR_DASHBOARD -> VendorDashboardScreen(viewModel = viewModel)
            Screen.VENDOR_QR_SCANNER -> VendorQrScannerScreen(viewModel = viewModel)
            Screen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
            Screen.OOPS_ACADEMIC -> OopsAcademicScreen(viewModel = viewModel)
        }
    }
}
