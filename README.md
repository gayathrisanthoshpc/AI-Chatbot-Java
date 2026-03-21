<div align="center">

# ✦ ORYN
### *Light of Knowledge*

![Version](https://img.shields.io/badge/version-4.2-rose?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=java)
![License](https://img.shields.io/badge/license-MIT-pink?style=for-the-badge)
![Platform](https://img.shields.io/badge/platform-Windows-blue?style=for-the-badge)

*A premium AI chatbot with glassmorphism UI, emotional intelligence, and a soul.*

</div>

---

## 🌸 What is ORYN?

ORYN is not just another chatbot. It is a fully hand-crafted Java desktop application built with **zero external libraries** — every pixel, every animation, every intelligence layer written from scratch.

Named after an invented word meaning *light of knowledge*, ORYN is designed to feel genuinely personal — it remembers you, grows closer to you over time, senses your mood, and surprises you with things you never expected from a chatbot.

---

## ✨ Features

### 🎨 Rose Noir Glassmorphism UI
- Frosted glass bubbles with rose gold gradient borders
- Animated floating particles in background
- Warm ivory light mode / deep noir dark mode
- ORYN signature asymmetric bubble corners — a unique trademark style
- Animated slide-in message transitions
- Pulsing orb avatar with custom programmatic logo

### 🧠 Intelligence Layer
| Feature | Description |
|---|---|
| **Mood Detection** | Senses happiness, frustration, curiosity, stress and adapts tone |
| **Bond System** | Relationship grows from Stranger → Trusted Soul over 300+ messages |
| **Long Memory** | Remembers your name, interests, and topics across sessions |
| **Daily Digest** | Personalised briefing every new day with your top interests |
| **Personality Modes** | Default, Formal, Casual, Playful — switch anytime |
| **Context Awareness** | Understands follow-up questions naturally |
| **Smart Suggestions** | Clickable follow-up chips after every reply |

### 🌐 Free APIs (no API key needed)
- 🌤 **Weather** — `wttr.in` — real-time weather by city
- 😄 **Jokes** — `official-joke-api`
- 📖 **Wikipedia** — `en.wikipedia.org/api` — summaries on any topic
- 🧠 **Trivia** — `opentdb.com` — random trivia with answer checking
- 💬 **Quotes** — `api.quotable.io` — inspirational quotes
- 🔢 **Number Facts** — `numbersapi.com`

### 🎮 Mini Games
- **Number Game** — guess a number 1–100 with narrowing hints
- **Word Game** — guess a word from a clue with letter matching
- **Riddles** — classic riddles with give-up option

### 🥚 Easter Eggs (discover them yourself...)
Hidden commands that unlock secret ORYN experiences. Some hints: try `knock knock`, `oryn dance`, `matrix`, `sudo`, `sing`, `magic 8 ball`, `what are you made of`...

### ⚙️ Power Features
- 🎙️ Voice input — speak to ORYN (Windows Speech Recognition)
- 🔍 Search through chat history with highlight
- 💾 Save chat as `.txt`
- 📄 Export chat as PDF
- 🏆 ORYN Score — track your learning journey
- 🎯 Focus Mode — deep work assistant
- ⚖️ Debate Mode — ORYN argues both sides of any topic
- ⚙️ Settings — font size, sound, theme, username

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- IntelliJ IDEA (recommended) or any Java IDE

### Run in IntelliJ
```bash
# 1. Clone the repo
git clone https://github.com/yourusername/AI-Chatbot-Java.git

# 2. Open in IntelliJ IDEA
# File → Open → select the project folder

# 3. Run
# Open src/chatbot/ChatBot.java → click ▶ Run
```

### Run from terminal
```bash
javac --release 17 -d out $(find src -name "*.java")
java -cp out chatbot.ChatBot
```

---

## 💬 Commands Reference

| Say this | ORYN does this |
|---|---|
| `hi` / `hello` | Greets you based on bond level |
| `tell me about black holes` | Wikipedia summary |
| `weather in London` | Live weather |
| `trivia` | Random trivia question |
| `debate free will` | Argues both sides |
| `number game` | Starts guessing game |
| `word game` | Starts word guessing |
| `riddle` | Tells a riddle |
| `be formal` / `be playful` | Changes personality |
| `focus on my project` | Activates focus mode |
| `my score` | Shows ORYN score card |
| `help` | Full feature list |

---

## 🏗️ Project Structure
```
src/chatbot/
├── ChatBot.java                  ← Entry point
├── model/
│   └── Message.java              ← Data model
├── service/
│   ├── ChatService.java          ← Interface
│   ├── SmartChatBot.java         ← Main brain
│   └── WebApiService.java        ← Free API calls
├── intelligence/
│   ├── MoodEngine.java           ← Emotion detection
│   ├── LongMemory.java           ← Cross-session memory
│   ├── BondSystem.java           ← Relationship levels
│   ├── DailyDigest.java          ← Daily briefing
│   ├── EasterEggs.java           ← Hidden surprises
│   ├── PersonalityEngine.java    ← Tone modes
│   └── MiniGames.java            ← In-chat games
├── ui/
│   ├── ChatBotGUI.java           ← Main window
│   ├── ORYNBubble.java           ← Glassmorphism bubbles
│   ├── ORYNIcon.java             ← Programmatic logo
│   ├── ParticleBackground.java   ← Animated particles
│   ├── VoiceInput.java           ← Mic button
│   ├── TypingIndicator.java      ← Animated dots
│   ├── AetherDialog.java         ← Styled dialogs
│   └── SettingsPanel.java        ← Settings window
└── util/
    ├── AppConfig.java            ← Rose Noir theme
    ├── UserProfile.java          ← Saved preferences
    ├── LongMemory.java           ← Persistence
    ├── SoundManager.java         ← Audio feedback
    ├── ChatHistory.java          ← Save to .txt
    └── PdfExporter.java          ← Export to PDF
```

---

## 🛠️ Built With

- **Java Swing** — UI framework
- **Java AWT Graphics2D** — Custom painting, glassmorphism effects
- **java.net.http** — Free API calls
- **javax.sound.sampled** — Sound generation
- **java.awt.print** — PDF export
- **PowerShell System.Speech** — Voice recognition (Windows)
- **Zero external libraries** — Everything built from scratch

---

## 🌱 Roadmap

- [ ] Image analysis — drop image, ORYN describes it
- [ ] Multi-language support
- [ ] Plugin system for custom commands
- [ ] Mobile companion app

---

## 👩‍💻 Author

Built with 💗 as a passion project exploring what a truly personal, beautiful Java chatbot could be.

---

<div align="center">

*"Knowledge is the light that never dims."*
**— ORYN**

</div>
