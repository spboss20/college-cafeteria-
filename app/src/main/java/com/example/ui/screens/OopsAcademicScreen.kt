package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.viewmodel.CampusBiteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OopsAcademicScreen(
    viewModel: CampusBiteViewModel
) {
    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("C++ OOPS Architecture", fontWeight = FontWeight.Bold, color = CharcoalDark) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CharcoalDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Academic Context Note
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Academic Assignment Companion", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E40AF))
                        Text(
                            "This section details the theoretical Object-Oriented C++ mapping developed for the college OOPS coursework.",
                            fontSize = 11.sp,
                            color = Color(0xFF1E3A8A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ConceptCard(
                title = "1. Inheritance & User Hierarchy",
                description = "Base abstract class `User` extended by `Student` and `Vendor`.",
                cppCode = """
// User Base Class & Inheritance
class User {
protected:
    std::string id, name, email, phone;
public:
    User(std::string id, std::string name, std::string email, std::string phone)
        : id(id), name(name), email(email), phone(phone) {}
    virtual void displayProfile() const = 0; // Pure virtual function
    virtual ~User() {}
};

class Student : public User {
    std::string studentBlock;
public:
    Student(std::string id, std::string name, std::string email, std::string phone, std::string block)
        : User(id, name, email, phone), studentBlock(block) {}
    void displayProfile() const override {
        std::cout << "Student: " << name << " | Block: " << studentBlock << std::endl;
    }
};

class Vendor : public User {
    std::string outletId;
public:
    Vendor(std::string id, std::string name, std::string email, std::string phone, std::string outlet)
        : User(id, name, email, phone), outletId(outlet) {}
    void displayProfile() const override {
        std::cout << "Vendor: " << name << " | Outlet: " << outletId << std::endl;
    }
};
""".trimIndent()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ConceptCard(
                title = "2. Encapsulation: FoodItem & Outlet",
                description = "Data hiding with private members and getters/setters.",
                cppCode = """
class FoodItem {
private:
    std::string id, name, category;
    double price;
    bool isAvailable;
public:
    FoodItem(std::string id, std::string name, std::string cat, double p)
        : id(id), name(name), category(cat), price(p), isAvailable(true) {}
    double getPrice() const { return price; }
    std::string getName() const { return name; }
    bool getAvailability() const { return isAvailable; }
    void setAvailability(bool status) { isAvailable = status; }
};

class Outlet {
private:
    std::string outletId, name, location;
    std::vector<FoodItem> menu;
    bool isOpen;
public:
    Outlet(std::string id, std::string name, std::string loc)
        : outletId(id), name(name), location(loc), isOpen(true) {}
    void addMenuItem(const FoodItem& item) { menu.push_back(item); }
    const std::vector<FoodItem>& getMenu() const { return menu; }
};
""".trimIndent()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ConceptCard(
                title = "3. Polymorphism: Payment Abstraction",
                description = "Runtime polymorphism for simulated UPI, Cards, and QR gateways.",
                cppCode = """
class PaymentStrategy {
public:
    virtual bool processPayment(double amount, std::string& txRef) = 0;
    virtual ~PaymentStrategy() {}
};

class UpiPayment : public PaymentStrategy {
    std::string vpa;
public:
    UpiPayment(std::string vpa) : vpa(vpa) {}
    bool processPayment(double amount, std::string& txRef) override {
        txRef = "UPI/JECRC/" + std::to_string(rand() % 900000 + 100000);
        return true; // Verified instant transaction
    }
};

class QrTokenVerifier {
public:
    static bool verifyToken(const std::string& token, const std::string& outletId, const std::string& orderOutletId) {
        if (outletId != orderOutletId) return false; // Outlet boundary check
        return token.rfind("CB-QR-", 0) == 0;
    }
};
""".trimIndent()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ConceptCard(
                title = "4. Composition: Order, OrderItem & CanteenManager",
                description = "Whole-part relationships composing the campus ordering life-cycle.",
                cppCode = """
struct OrderItem {
    FoodItem item;
    int quantity;
    double getSubtotal() const { return item.getPrice() * quantity; }
};

class Order {
private:
    std::string orderId;
    std::string studentEmail;
    std::vector<OrderItem> items;
    std::string status;
    std::string qrToken;
public:
    Order(std::string id, std::string studentEmail, std::string token)
        : orderId(id), studentEmail(studentEmail), qrToken(token), status("PLACED") {}
    void addItem(const OrderItem& it) { items.push_back(it); }
    double calculateTotal() const {
        double sum = 0.0;
        for (const auto& i : items) sum += i.getSubtotal();
        return sum;
    }
    void updateStatus(std::string newStatus) { status = newStatus; }
};
""".trimIndent()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ConceptCard(title: String, description: String, cppCode: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CharcoalDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 12.sp, color = Color(0xFF64748B))

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = cppCode,
                    color = Color(0xFF38BDF8),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
