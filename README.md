💵 FR_CrediFast 💵
¡Bienvenidos a FR_CrediFast! 📱⚡
Nuestro proyecto es una plataforma integral de gestión para negocios de préstamos (prestamistas). 
Los usuarios pueden gestionar clientes, otorgar préstamos con interés simple, cobrar por calendario día a día, 
controlar la mora automáticamente y generar recibos, llevando un seguimiento completo desde que se otorga el préstamo hasta que queda saldado. 💰

👨‍💻 **Desarrolladores:**

1-Romel M. Ortega

2-Félix Alberto Muñoz

🔐 Usuario de Prueba:
Acceso con Google 🔑 Correo demo: demo@credifast.com
⚠️ La app inicia sesión mediante Google (Firebase Authentication), así que no usa una contraseña fija: 
se entra con una cuenta de Google real desde el botón "Continuar con Google". El correo de arriba es solo de referencia.

🌟 Funcionalidades Principales:
👥 Gestión de Clientes 📋
Registrar, editar y buscar clientes con validaciones reales (cédula de 11 dígitos y teléfono de 10 dígitos, ambos únicos y solo numéricos). 
Los clientes con préstamos no se pueden eliminar, para conservar el historial. 🔎

💸 Otorgamiento de Préstamos 📈
Otorgar préstamos con interés simple y cuotas semanales, respetando la regla de un solo préstamo activo por cliente y el bloqueo de clientes en lista negra.
Cada préstamo genera automáticamente su calendario de cuotas. 🗓️

🧾 Cobros por Calendario ✅
Cobrar día a día desde un calendario navegable, viendo el total por recolectar y lo cobrado de la jornada.
Se puede pagar cuota completa, saldar el préstamo o abonar un monto libre con nota, siempre con confirmación antes de cobrar,
y generando un recibo de cada pago. 🎯

⚠️ Control de Mora 📆
Los clientes con cuotas vencidas entran automáticamente en mora, sumando RD$100 por día por cada cuota vencida. 
El cálculo es en vivo y salen de mora al ponerse al día. 🚨

📚 Historial de Préstamos 📊
Consultar los préstamos ya saldados, con búsqueda y ordenamiento (recientes, antiguos o por fecha). 🗂️

🚫 Lista Negra 🔒
Bloquear clientes para que no reciban préstamos (con razón opcional). Siguen en el sistema pero no pueden recibir crédito. ⛔

📊 Dashboard 📈
Panel con las métricas del negocio: dinero pendiente en la calle, capital desembolsado, recaudado en los últimos 7 días, préstamos activos, clientes en mora y un gráfico de barras con la recaudación de la semana. 💹

🎨 Características del Sistema:
Diseño Moderno 🌟 Interfaz elegante con tema "verde billete" y diseño pensado para móviles.
Navegación por Pestañas 🎯 Barra inferior con 5 secciones (Inicio, Clientes, Préstamos, Cobros y Más) para moverse rápido por toda la app.
Estados Visuales en Tiempo Real 📊 Badges de color para clientes y cuotas (Al día, Mora, Pagada, Pendiente, Vencida) calculados en vivo.
Recibos de Pago 🧾 Cada cobro genera un recibo con el detalle completo del pago, capital, interés y balance restante.


🛠️ Tecnologías Utilizadas:
Lenguaje: Kotlin 💻 UI: Jetpack Compose con Material 3 (tema verde personalizado) 
🎨 Arquitectura: Clean Architecture + MVI (State + Event + ViewModel) 
🏗️ Inyección de Dependencias: Hilt 🧩 Base de Datos: Room (persistencia local, Single Source of Truth)
🗄️ Navegación: Navigation 3 (type-safe con NavDisplay)
🧭 Autenticación: Firebase Authentication + Credential Manager (Google)
🔐 Pruebas: JUnit + MockK + Google Truth + Coroutines Test + Robolectric ✅

🗄️ Entidades
La app tiene 5 entidades en Room, cada una con 5 registros reales digitados desde la app:
1.	Usuario 👤 — operadores que inician sesión con Google.
2.	Cliente 🧍 — nombre, cédula, teléfono, dirección, lista negra.
3.	Préstamo 💵 — capital, interés, cuotas, total, balance, estado.
4.	Cuota 🗓️ — número, vencimiento, monto, estado (la mora se calcula en vivo).
5.	Pago 🧾 — monto cobrado, capital, interés, balance restante, tipo, nota.

🚀 Instalación Local:
Pre-requisitos:
•	Android Studio (versión actual) 💻
•	JDK 17 ☕
•	Emulador o dispositivo con Google Play Services 📱
Pasos:
1.	Abrir el proyecto en Android Studio. 📂
2.	En la consola de Firebase, crear un proyecto y activar Authentication → Google. 🔥
3.	Descargar el archivo google-services.json y colocarlo en la carpeta app/. 📄
4.	En app/src/main/res/values/strings.xml, colocar en default_web_client_id el Web client ID del proyecto de Firebase. 🔑
5.	Agregar la huella SHA-1 de la máquina en Firebase para habilitar el login con Google. 🔏
6.	Sincronizar Gradle y ejecutar. ▶️

🚀 Instalación mediante Apk: https://github.com/FelixAlbertoM/CrediFast/releases/tag/v1.0

Link del video de la aplicación: https://drive.google.com/file/d/1kZO9juv-IIDkCZiN-L6c9W2EhpExgyKI/view?usp=sharing
