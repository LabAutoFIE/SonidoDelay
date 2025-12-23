# 🎶 SonidoDelay

Aplicación Android para **medir y aplicar retardo en audio en tiempo real**.  
Permite grabar desde el micrófono, reproducir con un retardo configurable y medir la latencia del sistema.

---

## ✨ Características
- 🎤 **Grabación de audio** desde el micrófono.
- 🔊 **Reproducción con retardo** ajustable mediante un `SeekBar`.
- ⏱️ **Medición de latencia** con la clase `LatencyMeter`.
- 📱 Interfaz simple con **ViewBinding**.

---

## ⚙️ Tecnologías utilizadas
- 🟣 Kotlin
- 📐 Android Studio
- 📦 AndroidX (AppCompat, CoreKtx, Lifecycle)
- 🎨 Material Design 3
- 🧩 Version Catalog (`libs.versions.toml`)

---
## 📂 Estructura del proyecto
```
app/
├── java/com/example/sonidodelay/
│    ├── audio/        # Clases de audio (Recorder, Player, DelayBuffer, LatencyMeter)
│    └── ui/           # MainActivity y lógica de interfaz
├── res/               # Layouts y recursos
└── build.gradle.kts   # Configuración Gradle
```
---

## 🚀 Instalación
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/LabAutoFIE/SonidoDelay.git
   
## 🔒 Permisos requeridos
La app solicita:
-RECORD_AUDIO → acceso al micrófono para grabar y medir latencia.

## 📱 Uso
- Ajustar el retardo con el SeekBar.
- Presionar el botón Medir Latencia para obtener el valor en ms.
- El audio se reproduce con el retardo configurado

## 📝 Licencia
Este proyecto está bajo la licencia MIT.
Podés usarlo, modificarlo y compartirlo libremente