package dev.jaym21.kept.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SummaryCardData(val title: String, val value: String)
data class DuePayment(val id: Int, val title: String, val subtitle: String, val amount: String, val status: PaymentStatus)
enum class PaymentStatus { UPCOMING, DUE_SOON, OVERDUE }

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    summaryItems: List<SummaryCardData> = sampleSummaryItems(),
    upcomingPayments: List<DuePayment> = sampleDuePayments(),
    onAddClick: () -> Unit = {}
) {
    KeptTheme {
        Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
            DashboardContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                summaryItems = summaryItems,
                upcomingPayments = upcomingPayments
            )
        }
    }
}

@Composable
private fun DashboardContent(
    modifier: Modifier = Modifier,
    summaryItems: List<SummaryCardData>,
    upcomingPayments: List<DuePayment>
) {
    Column(
        modifier =  modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        DashboardTopBar()

    }
}

@Composable
private fun DashboardTopBar() {
    Text(
        text = "Dashboard",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 26.sp,
        fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
        fontWeight = FontWeight.Bold
    )
}


private fun sampleSummaryItems(): List<SummaryCardData> = listOf(
    SummaryCardData("Total Investments", "₹1,20,500"),
    SummaryCardData("Total Returns", "₹34,750"),
    SummaryCardData("Active Policies", "3"),
    SummaryCardData("Upcoming Dues", "2")
)

private fun sampleDuePayments(): List<DuePayment> = listOf(
    DuePayment(1, "LIC Jeevan Anand", "Due on Aug 21", "₹5,600", PaymentStatus.UPCOMING),
    DuePayment(2, "Bajaj Finance", "Due on Aug 26", "₹2,500", PaymentStatus.DUE_SOON),
    DuePayment(3, "SBI Mutual Fund", "Due on Sep 2", "₹10,000", PaymentStatus.UPCOMING)
)

@Composable
@Preview(showBackground = true)
fun DashboardPreview() {
    KeptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DashboardScreen(onAddClick = {})
        }
    }
}