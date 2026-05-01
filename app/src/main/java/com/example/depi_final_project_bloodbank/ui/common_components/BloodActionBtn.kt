package com.example.depi_final_project_bloodbank.ui.common_components
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.ui.theme.BloodRed

@Composable
fun BloodActionBtn(
    text: String,
    onClick:()-> Unit,
    modifier: Modifier= Modifier,
    isPrimary: Boolean=true,
    icon:(@Composable ()-> Unit)?=null
){
    val containerColor= if(isPrimary) BloodRed else Color.White
    val contentColor= if (isPrimary) Color.White else BloodRed
    val border= if (isPrimary)null else BorderStroke(1.dp,BloodRed)

    Button(
        onClick=onClick,
        modifier=modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border=border,
        shape= RoundedCornerShape(25.dp),
        elevation=if(isPrimary) ButtonDefaults.buttonElevation(defaultElevation = 2.dp)else null
    ){
        Row(
          verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            if (icon!= null){
                icon()
                Spacer(modifier= Modifier.width(8.dp))
            }
            Text(
                text=text.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DonateActionBtn() {
    BloodActionBtn(
        text = "Donate Now"
        , onClick = {},
        isPrimary = true,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    )
}