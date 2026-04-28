package denis.and.co.handshop.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertChartOutlined
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.graphics.toColorInt
import denis.and.co.handshop.data.model.ChildItem
import denis.and.co.handshop.data.model.ParentItem
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest

@Composable
fun ExpandableList(
    items: List<ParentItem>,
    onChildClick: (ChildItem) -> Unit
) {
    LazyColumn {
        items(items) { parent ->
            ExpandableListItem(
                parent = parent,
                onChildClick = onChildClick
            )
        }
    }
}

@Composable
fun ExpandableListItem(
    parent: ParentItem,
    onChildClick: (ChildItem) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(HardBack.copy(0.35f))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = parent.title,
                style = TextStyle(
                    fontFamily = Onest,
                    color = BlackText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (isExpanded)
                    Icons.Outlined.KeyboardArrowUp
                else
                    Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = LowAlphaBlackText
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .padding(bottom = 8.dp)
            ) {
                parent.children.forEach { child ->
                    ChildItemView(
                        child = child,
                        onClick = { onChildClick(child) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChildItemView(
    child: ChildItem,
    onClick: () -> Unit
) {
    var showInformation by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(7.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(LowAlphaBlackText)
        )

        Icon(
            imageVector = child.icon,
            contentDescription = null,
            modifier = Modifier.padding(start = 5.dp).size(15.dp),
            tint = BlackText
        )

        Text(
            text = child.title,
            style = TextStyle(
                fontFamily = Comfortaa,
                color = BlackText,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            modifier = Modifier.padding(start = 5.dp)
        )

        if (child.description.isNotBlank()) {
            Spacer(Modifier.weight(1f))

            Box {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { showInformation = true },
                    tint = LowAlphaBlackText
                )

                if (showInformation) {
                    Popup(
                        alignment = Alignment.TopEnd,
                        onDismissRequest = { showInformation = false }
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = child.title,
                                    style = TextStyle(
                                        fontFamily = Onest,
                                        color = BlackText,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                )

                                HorizontalDivider(
                                    thickness = 2.dp,
                                    color = BlackText.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(5.dp)
                                )

                                Text(
                                    text = child.description,
                                    style = TextStyle(
                                        fontFamily = Comfortaa,
                                        color = BlackText,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}