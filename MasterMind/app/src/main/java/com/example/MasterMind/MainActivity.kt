/* Project: MasterMind: Master Mind Game
 *
 * Author: Eduardo Leon-Prado
 * Start Date: 22/12/2025
 * Last Update: 26/12/2025
 *
 * Description: This Application implements the
 * Master Mind Game for an Android Device
 *
 * The Game: When a new Game is started the App
 * generates 4 colors, out of 6, at random.
 * Player must guess these 4 different colors
 * and their right positions.
 *
 */

package com.example.MasterMind
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.MasterMind.ui.theme.AppTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.FastOutSlowInEasing

// Customized colors
val LightGreen = Color(0xFF90EE90) // Light Green
val LightBlue = Color(0xFFADD8E6) // Light Blue
val DeepPink = Color(0xFFFF1493) // Deep Pink
val Orange = Color(0xFFFFA726) // Light Bright Orange
val LightYellow = Color(0xFFFFF9C4) // Light Yellow

// FOR DEBUG ONLY
//const val DEBUG_GAME = true
const val DEBUG_GAME = false

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            AppTheme {

                // Defines all the UI Elements (App Layout)
                App_UI_Layout()

            } // End AppTheme

        } // End setContent

    } // End override function

} // End MainActivity


/** Function App_UI_Layout()
 *
 * This Function builds the layout of this Application
 *
 */
@Composable
fun App_UI_Layout()
{
    // Variables for the Execution of the Game
    var GB_Game_Running by remember { mutableStateOf(false) } // Variable to Control the Game
    var G_Nbr_Attempts by remember { mutableStateOf(0) } // Variable to Count the Number of Attempts
    var SecretColors by remember {
        mutableStateOf<List<Pair<String, Color>>>(emptyList())
    }

    var G_ColorsInPlace by remember { mutableStateOf(0) } // Variable to Count the Colors in Place
    var G_ColorsInWrongPlace by remember { mutableStateOf(0) } // Variable to Count the Colors in Wrong Place

    // Colors
    var color1 by remember { mutableStateOf(Color.LightGray) }
    var color2 by remember { mutableStateOf(Color.LightGray) }
    var color3 by remember { mutableStateOf(Color.LightGray) }
    var color4 by remember { mutableStateOf(Color.LightGray) }

    var consoleMessages by remember {
        mutableStateOf(
            listOf(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Red, // Red
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\t\t\t\t\t\t<<< Reglas del Juego >>>")
                    }
                },
                buildAnnotatedString {
                    append("\n-> El juego tiene 6 colores")
                },
                buildAnnotatedString {
                    append("-> Al iniciar se generan 4 Colores")
                },
                buildAnnotatedString {
                    append("    aleatorios que se deben resolver")
                },
                buildAnnotatedString {
                    append("    en sus posiciones correctas")
                },
                buildAnnotatedString {
                    append("-> En cada intento selecciona 4")
                },
                buildAnnotatedString {
                    append("    colores sin repetir ninguno")
                },
                buildAnnotatedString {
                    append("-> Cuando estés seguro de tu juego")
                },
                buildAnnotatedString {
                    append("    Presiona ")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF4CAF50), // Green
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("OK")
                    }
                    append(" para verificar")
                }
            )
        )

    } // End of Initialization of consoleMessages

    // Setting First Game: Random Generation of 4 different colors out of 6
    LaunchedEffect(Unit) {
        SecretColors = generateFourNamedColors()
        GB_Game_Running = true
        if (DEBUG_GAME) {
            consoleMessages = consoleMessages + consoleLine(
                "\nDEBUG Secret:\n" +
                        SecretColors.joinToString("  ") { it.first } // Select only Color Names
            )
        }
    }

    // App Layout
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // ===== BACKGROUND IMAGE =====
        Image(
            painter = painterResource(id = R.drawable.mastermind_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // (Optional) Make the background a bit darker
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
        )

        Column {
            // Header Title
            HeaderTitle()

            Spacer(modifier = Modifier.height(30.dp)) // Space

            // Player Colors Buttons UI (as row)
            ColorPickerRow(
                color1 = color1,
                color2 = color2,
                color3 = color3,
                color4 = color4,
                onColorChange1 = { color1 = it },
                onColorChange2 = { color2 = it },
                onColorChange3 = { color3 = it },
                onColorChange4 = { color4 = it })

            Spacer(modifier = Modifier.height(30.dp)) // Space

            // Buttons in Row: "NUEVO", "OK"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // One Left, one Right
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "NUEVO" Button: Button to Start a New Game
                Game_Btn(
                    text = "NUEVO",
                    color = LightBlue,
                    modifier = Modifier.width(170.dp),
                    onClick = {
                        // Re-Start Colors Buttons
                        color1 = Color.LightGray
                        color2 = Color.LightGray
                        color3 = Color.LightGray
                        color4 = Color.LightGray

                        consoleMessages = listOf(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("\t\t\t\t<<< Nuevo Juego Iniciado >>>")
                                }
                            }
                        )

                        // Init Game Variables
                        GB_Game_Running = true // Game is Running
                        G_Nbr_Attempts = 0 // Reset Counter of Attempts
                        G_ColorsInPlace = 0 // Reset Counter of Colors in Place
                        G_ColorsInWrongPlace = 0 // Reset Counter of Colors in Wrong Place

                        // Random Generation of 4 different colors out of 6
                        SecretColors = generateFourNamedColors()

                        if (DEBUG_GAME) {
                            consoleMessages = consoleMessages + consoleLine(
                                "DEBUG Secret:\n" +
                                        SecretColors.joinToString("  ") { it.first } // Select only Color Names
                            )
                        }

                    } // End onClick
                )

                // "OK" Button: Button to Verify the Game
                Game_Btn(
                    text = "OK",
                    color = LightGreen,
                    modifier = Modifier.width(170.dp),
                    onClick = {
                        // If Game is Not Running: Return (No Action allowed with this Button)
                        if (!GB_Game_Running) {

                            consoleMessages = consoleMessages + consoleLine("\n-> Inicia \"NUEVO\" Juego")
                            return@Game_Btn
                        }

                        // If any of the 4 Colors is still LightGray: Return (do nothing)
                        if (color1 == Color.LightGray || color2 == Color.LightGray ||
                            color3 == Color.LightGray || color4 == Color.LightGray) return@Game_Btn

                        // Game is Running and Colors are Selected by Player
                        G_Nbr_Attempts++
                        consoleMessages = consoleMessages + consoleLine("\n-> Número de Intentos: $G_Nbr_Attempts")

                        /* *** Check for Colors in Place and in Wrong Place *** */

                        // Convert Colors to String Names
                        val Name1 = colorToName(color1)
                        val Name2 = colorToName(color2)
                        val Name3 = colorToName(color3)
                        val Name4 = colorToName(color4)

                        // Player Guess Colors: List of Pairs (String, Color)
                        val PlayerColors = listOf(
                            Name1 to color1,
                            Name2 to color2,
                            Name3 to color3,
                            Name4 to color4
                        )

                        // Show Player´s Selected Colors on Console
                        consoleMessages = consoleMessages + listOf(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = color1,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("\t\t\t\t$Name1  ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = color2,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("$Name2  ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = color3,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("$Name3  ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = color4,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("$Name4")
                                }
                            } // End buildAnnotatedString
                        )

                        // Compare Player Guess Colors with Secret (Random Generated) Colors
                        if (PlayerColors == SecretColors) {
                            if (G_Nbr_Attempts <= 6) {
                                consoleMessages = consoleMessages + consoleLine("\n**********************************")
                                consoleMessages = consoleMessages + consoleLine("CORRECTO: NIVEL BRILLANTE")
                                consoleMessages = consoleMessages + consoleLine("**********************************")
                            } else if (G_Nbr_Attempts < 10) {
                                consoleMessages = consoleMessages + consoleLine("\n**********************************")
                                consoleMessages = consoleMessages + consoleLine("CORRECTO: NIVEL BUENO")
                                consoleMessages = consoleMessages + consoleLine("**********************************")
                            } else if (G_Nbr_Attempts < 15) {
                                consoleMessages = consoleMessages + consoleLine("\n**********************************")
                                consoleMessages = consoleMessages + consoleLine("CORRECTO: NIVEL MEJORABLE")
                                consoleMessages = consoleMessages + consoleLine("**********************************")
                            } else {
                                consoleMessages = consoleMessages + consoleLine("\n**********************************")
                                consoleMessages = consoleMessages + consoleLine("CORRECTO: NIVEL BAJO")
                                consoleMessages = consoleMessages + consoleLine("**********************************")
                            }

                            GB_Game_Running = false // Update State: Game Not Running

                        } else {
                            /* Show Number of Colors in Place and in Wrong Place
                             * Comparison is done only by Names:
                             * Secret Name = SecretColors[n].(first, second, third, fourth)
                             * where "n" is the position: <0-3>
                             *
                             */

                            // Check Name1
                            if(Name1 == SecretColors[0].first) {
                                G_ColorsInPlace++
                            } else if (Name1 == SecretColors[1].first ||
                                Name1 == SecretColors[2].first ||
                                Name1 == SecretColors[3].first) {
                                G_ColorsInWrongPlace++
                            }

                            // Check Name2
                            if(Name2 == SecretColors[1].first) {
                                G_ColorsInPlace++
                            } else if (Name2 == SecretColors[0].first ||
                                Name2 == SecretColors[2].first ||
                                Name2 == SecretColors[3].first) {
                                G_ColorsInWrongPlace++
                            }

                            // Check Name3
                            if(Name3 == SecretColors[2].first) {
                                G_ColorsInPlace++
                            } else if (Name3 == SecretColors[0].first ||
                                Name3 == SecretColors[1].first ||
                                Name3 == SecretColors[3].first) {
                                G_ColorsInWrongPlace++
                            }

                            // Check Name4
                            if(Name4 == SecretColors[3].first) {
                                G_ColorsInPlace++
                            } else if (Name4 == SecretColors[0].first ||
                                Name4 == SecretColors[1].first ||
                                Name4 == SecretColors[2].first) {
                                G_ColorsInWrongPlace++
                            }

                            // Report Colors in Place and in Wrong Place on Console
                            consoleMessages = consoleMessages + consoleLine("-> Colores en lugar correcto: $G_ColorsInPlace")
                            consoleMessages = consoleMessages + consoleLine("-> Colores en lugar incorrecto: $G_ColorsInWrongPlace")
                            consoleMessages = consoleMessages + consoleLine("-> INTENTALO DE NUEVO")

                            // Reset Counter of Colors in Place and in Wrong Place for next guess
                            G_ColorsInPlace = 0
                            G_ColorsInWrongPlace = 0
                        }

                    } // End onClick
                )

            } // End Buttons in Row

            Spacer(modifier = Modifier.height(30.dp)) // Space

            // Output Console UI defined as a composable function
            ConsoleOutput(messages = consoleMessages)

            Spacer(modifier = Modifier.height(30.dp)) // Space

            // Quit Application Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Declares Context to be used when Quiting the App
                val context = LocalContext.current

                // Variable for Asynchronous Delay
                val coroutineScope = rememberCoroutineScope()

                Game_Btn(
                    text = "Salir",
                    color = DeepPink,
                    modifier = Modifier.width(150.dp),
                    onClick = {

                        consoleMessages = listOf(
                            consoleLine("<<< Cerrando Aplicación >>>")
                        )

                        coroutineScope.launch {
                            delay(1500) // Asynchronous Delay before Closing App
                            (context as? Activity)?.finish() // Close Activity: Non Blocking
                            (context as? Activity)?.finishAffinity() // Close Activities & Quit App
                        }
                    }
                )

            } // End of Box for Quit Button

        } // End Column

    } // End of Box

} // End Function App_UI_Layout()


/** Function HeaderTitle()
 *
 * This Function defines the Header
 * of the App (App Title)
 *
 */
@Composable
fun HeaderTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(
                color = Color(0xFFFF9800), // Nice Orange Color
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFE65100),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Master Mind by ELPD",
            //color = Color.White,
            color = Color.Blue,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }

} // End Function


/** Function ConsoleOutput()
 *
 * This is the Output Console
 *
 */
@Composable
fun ConsoleOutput(messages: List<AnnotatedString>) {

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        coroutineScope.launch {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(8.dp)
            .background(LightYellow)
            .border(1.dp, Color.Gray)
    ) {
        items(messages) { message ->
            Text(
                text = message,  // 👈 AnnotatedString
                modifier = Modifier.padding(4.dp),
                fontSize = 10.sp,
                lineHeight = 10.sp  // IMPORTANT: to avoid Empty Lines
            )
        }
    }

} // End Function ConsoleOutput()


/** Function: consoleLine()
 *
 * This Function is a Helper to build a Console Line
 *
 * Note: this is not a @composable Function
 *
 */
fun consoleLine(text: String): AnnotatedString =
    buildAnnotatedString {
        append(text)
    } // End Function consoleLine()


/** Function ColorPickerRow()
 *
 * This Function is the UI for the Player
 * showing 4 Buttons in a row. Each Button
 * is a List of 6 colors and Player must
 * Select one
 *
 */
@Composable
fun ColorPickerRow(color1: Color,
                   color2: Color,
                   color3: Color,
                   color4: Color,
                   onColorChange1: (Color) -> Unit,
                   onColorChange2: (Color) -> Unit,
                   onColorChange3: (Color) -> Unit,
                   onColorChange4: (Color) -> Unit) {
    val colorsList = listOf(
        Color.Red, Color.Green, Color.Blue,
        Orange, Color.Magenta, Color.Cyan
    )

    // States: Color & Menu for each Button
    //var color1 by remember { mutableStateOf(Color.LightGray) }
    var showMenu1 by remember { mutableStateOf(false) }

    //var color2 by remember { mutableStateOf(Color.LightGray) }
    var showMenu2 by remember { mutableStateOf(false) }

    //var color3 by remember { mutableStateOf(Color.LightGray) }
    var showMenu3 by remember { mutableStateOf(false) }

    //var color4 by remember { mutableStateOf(Color.LightGray) }
    var showMenu4 by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Color1 Button
        ColorPickerButton(color1, showMenu1, colorsList,
            onButtonClick = { showMenu1 = true }
        ) {
            onColorChange1(it)
            showMenu1 = false
        }

        // Color2 Button
        ColorPickerButton(color2, showMenu2, colorsList,
            onButtonClick = { showMenu2 = true }
        ) {
            onColorChange2(it)
            showMenu2 = false
        }

        // Color3 Button
        ColorPickerButton(color3, showMenu3, colorsList,
            onButtonClick = { showMenu3 = true }
        ) {
            onColorChange3(it)
            showMenu3 = false
        }

        // Color4 Button
        ColorPickerButton(color4, showMenu4, colorsList,
            onButtonClick = { showMenu4 = true }
        ) {
            onColorChange4(it)
            showMenu4 = false
        }
    }

} // End Function ColorPickerRow()


/** Function ColorPickerButton()
 *
 * This Function allows the User to Select a Color
 * for a Button. On top of this, a soft color transition
 * and border are added
 *
 */
@Composable
fun ColorPickerButton(
    color: Color,
    showMenu: Boolean,
    colorsList: List<Color>,
    onButtonClick: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    // 🎨 Soft Color Animation
    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "colorAnim"
    )

    // 🔍 Micro scale when changing State (color)
    val scale by animateFloatAsState(
        targetValue = if (color != Color.LightGray) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "scaleAnim"
    )

    // 🔲 Dynamic Border
    val borderColor = if (color != Color.LightGray)
        Color.Black.copy(alpha = 0.6f)
    else
        Color.Transparent

    Box {
        Button(
            onClick = onButtonClick,
            colors = ButtonDefaults.buttonColors(containerColor = animatedColor),
            modifier = Modifier
                .width(80.dp)
                .height(50.dp)
                .scale(scale)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(6.dp)
                ),
            shape = RoundedCornerShape(6.dp)
        ) {}

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { onButtonClick() }
        ) {
            colorsList.forEach { c ->
                DropdownMenuItem(
                    onClick = { onColorSelected(c) },
                    text = {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                                .background(c)
                                .border(1.dp, Color.Black)
                        )
                    }
                )
            }
        }
    }

} // End Function ColorPickerButton()


/** Function generateFourNamedColors()
 *
 * This Function generates 4 Random Colors out of 6
 * without Repeating.
 * Function returns a List of Pairs (String, Color)
 *
 * Note: this is not a @composable Function
 *
 */
fun generateFourNamedColors(): List<Pair<String, Color>> {
    val fixedColors = listOf(
        "Rojo" to Color.Red,
        "Verde" to Color.Green,
        "Azul" to Color.Blue,
        "Naranja" to Orange,
        "Magenta" to Color.Magenta,
        "Ciano" to Color.Cyan
    )

    // Returns 4 colors generated at Random
    return fixedColors.shuffled().take(4)

}  // End Function generateFourNamedColors()


/** Function colorToName()
 *
 * This Function returns the Name of a Color
 * passed as a Parameter.
 *
 * Note: this is not a @composable Function
 *
 */
fun colorToName(color: Color): String =
    when (color) {
        Color.Red     -> "Rojo"
        Color.Green   -> "Verde"
        Color.Blue    -> "Azul"
        Orange  -> "Naranja" // Customized Color
        Color.Magenta -> "Magenta"
        Color.Cyan    -> "Ciano"
        else        -> "Desconocido"

    } // End Function colorToName()


/** Function Game_Btn()
 *
 * Standard Button to be used throughout the App
 *
 */
@Composable
fun Game_Btn(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier
            .height(40.dp)
            .shadow(2.dp, shape = RoundedCornerShape(6.dp), clip = false)
            .border(
                width = 4.dp,
                color = Color.Blue.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }

} // End Function Game_Btn


// END OF APP