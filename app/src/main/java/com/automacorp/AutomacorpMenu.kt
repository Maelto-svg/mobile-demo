package com.automacorp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.automacorp.ui.theme.AutomacorpTheme

class AutomacorpMenu {
}

@SuppressLint("IntentReset")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AutomacorpTopAppBar(
    context: Context,
    title: String? = null,
    returnAction: () -> Unit = {}
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    )

    val navigateToRoomList = {
        if (context !is RoomListActivity) {
            val intent = Intent(context, RoomListActivity::class.java)
            context.startActivity(intent)
        } else {
            (context).recreate()
        }
    }


    val sendEmail = {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:thomas.maelwen@gmail.com")
    }
        context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
    }

    val openGitHub = {
        val githubIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://github.com/maelto-svg")
        }
        context.startActivity(githubIntent)
    }

    // Define the actions displayed on the right side of the app bar
    val actions: @Composable RowScope.() -> Unit = {
        IconButton(onClick = navigateToRoomList) {
            Icon(
                painter = painterResource(R.drawable.ic_rooms),
                contentDescription = stringResource(R.string.app_go_room_description)
            )
        }
        IconButton(onClick = sendEmail) {
            Icon(
                painter = painterResource(R.drawable.ic_mail),
                contentDescription = stringResource(R.string.app_go_mail_description)
            )
        }
        IconButton(onClick = openGitHub) {
            Icon(
                painter = painterResource(R.drawable.ic_github),
                contentDescription = stringResource(R.string.app_go_github_description)
            )
        }
    }

    // Display the app bar with the title if present and actions
    if (title == null) {
        TopAppBar(
            title = { Text("") },
            colors = colors,
            actions = actions
        )
    } else {
        MediumTopAppBar(
            title = { Text(title) },
            colors = colors,
            navigationIcon = {
                IconButton(onClick = returnAction) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.app_go_back_description)
                    )
                }
            },
            actions = actions
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun AutomacorpTopAppBarHomePreview() {
//    AutomacorpTheme {
//        AutomacorpTopAppBar(null)
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun AutomacorpTopAppBarPreview() {
//    AutomacorpTheme {
//        AutomacorpTopAppBar("A page")
//    }
//}