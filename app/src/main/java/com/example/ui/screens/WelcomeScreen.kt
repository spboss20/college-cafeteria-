package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonLight
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.viewmodel.CampusBiteViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun WelcomeScreen(
    viewModel: CampusBiteViewModel
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // JECRC Foundation Badge
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFFEE2E2),
            modifier = Modifier.border(1.dp, Color(0xFFFECACA), RoundedCornerShape(24.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "JECRC Foundation",
                    tint = CrimsonPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "JECRC FOUNDATION • JAIPUR",
                    color = CrimsonPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hero Logo Card
        Card(
            modifier = Modifier.size(110.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(CrimsonPrimary, CrimsonDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = "Food logo",
                        tint = AmberSecondary,
                        modifier = Modifier.size(52.dp)
                    )
                    Text(
                        text = "CB",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "JECRC CampusBite",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = CharcoalDark,
            letterSpacing = (-0.5).sp
        )

        Text(
            text = "“Order Smart. Skip the Queue.”",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = CrimsonPrimary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "Order delicious food from your classroom, library, or hostel across Tasteholic Café & E-Block Cafeteria.",
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Primary Action: Order Food
        Button(
            onClick = {
                viewModel.switchUserRole(UserRole.STUDENT)
                viewModel.navigateTo(Screen.OUTLET_SELECT)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("order_food_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
            shape = RoundedCornerShape(14.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Order Food",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Secondary Action: Vendor Dashboard Login
        OutlinedButton(
            onClick = {
                viewModel.navigateTo(Screen.VENDOR_LOGIN)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("vendor_login_button"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CharcoalDark),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Storefront, contentDescription = null, tint = CrimsonPrimary)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Vendor Portal & QR Scanner",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Admin & C++ Academic Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.switchUserRole(UserRole.ADMIN)
                    viewModel.navigateTo(Screen.ADMIN_DASHBOARD)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("admin_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Admin Hub", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = {
                    viewModel.navigateTo(Screen.OOPS_ACADEMIC)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("oops_academic_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Code, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("C++ OOPS", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Campus Highlights Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CAMPUS PICKUP WORKFLOW",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CrimsonPrimary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                HighlightRow(
                    stepNum = "1",
                    title = "Browse Menus & Customise",
                    desc = "Explore Tasteholic Café or E-Block menus with student prices"
                )
                Spacer(modifier = Modifier.height(8.dp))
                HighlightRow(
                    stepNum = "2",
                    title = "Instant UPI or Simulated Pay",
                    desc = "Zero waiting for change; fast digital payment"
                )
                Spacer(modifier = Modifier.height(8.dp))
                HighlightRow(
                    stepNum = "3",
                    title = "Unique QR Verification",
                    desc = "Flash QR at counter for instant verified pickup"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Designed for JECRC Foundation Students & Staff",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )
    }
}

@Composable
private fun HighlightRow(stepNum: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFFEE2E2),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(stepNum, color = CrimsonPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = CharcoalDark)
            Text(desc, fontSize = 11.sp, color = Color(0xFF64748B))
        }
    }
}
