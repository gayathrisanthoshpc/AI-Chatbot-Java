<div align="center">

# ORYN
### *Light of Knowledge*

<br>

![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white&style=flat-square)
![Version](https://img.shields.io/badge/Version-4.2-blueviolet?style=flat-square)
![Platform](https://img.shields.io/badge/Platform-Windows-0078D6?logo=windows&logoColor=white&style=flat-square)
![Libraries](https://img.shields.io/badge/External%20Libraries-Zero-success?style=flat-square)
![Stars](https://img.shields.io/github/stars/gayathrisanthoshpc/AI-Chatbot-Java?style=flat-square&color=gold)

<br>

> *A premium AI chatbot built entirely in Java — glassmorphism UI, emotional intelligence,*
> *mood detection, bond system, mini games, voice input and easter eggs.*
> *Zero external libraries. Everything from scratch.*

<br>

</div>

---

<div align="center">

##  Rose Noir · Glassmorphism · Java Swing

|  Beautiful UI |  Real Intelligence | Fun Features |
|:---:|:---:|:---:|
| Frosted glass bubbles | Mood detection | Mini games |
| Rose gold animations | Bond system (6 levels) | Easter eggs |
| Particle background | Cross-session memory | Voice input |
| Custom ORYN logo | Daily digest | Personality modes |
| Light & dark mode | Smart suggestions | Debate mode |

</div>

---

## What Makes ORYN Unique

ORYN is not just another chatbot. It's a passion project exploring what a truly **personal, beautiful Java desktop application** can be.

-  **Bond System** — relationship grows from *Stranger* → *Trusted Soul* over 300+ messages
-  **Mood Engine** — detects happiness, frustration, curiosity, stress and adapts its tone
-  **Long Memory** — remembers your name, interests and topics across sessions
-  **Daily Digest** — personalised briefing every new day
-  **Easter Eggs** — hidden commands that unlock secret ORYN experiences
-  **Voice Input** — speak to ORYN via Windows Speech Recognition
-  **Focus Mode** — deep work assistant with task tracking
-  **Debate Mode** — argues both sides of any topic

---

##  Free APIs — No Key Required

| API | Feature |
|---|---|
| `wttr.in` | Live weather by city |
| `opentdb.com` |  Trivia with answer checking |
| `en.wikipedia.org/api` |  Summaries on any topic |
| `quotable.io` | Inspirational quotes |
| `official-joke-api` |  Random jokes |
| `numbersapi.com` |  Fun number facts |

---

##  Getting Started

### Prerequisites
- Java 17 or higher
- IntelliJ IDEA (recommended)

### Run in IntelliJ
```bash
# 1. Clone
git clone https://github.com/gayathrisanthoshpc/AI-Chatbot-Java.git

# 2. Open in IntelliJ IDEA
# File → Open → select project folder

# 3. Run
# Open src/chatbot/ChatBot.java → click ▶
```

### Run from terminal
```bash
javac --release 17 -d out $(find src -name "*.java")
java -cp out chatbot.ChatBot
```

---

##  Commands

| Say this | What happens |
|---|---|
| `tell me about [topic]` | Wikipedia summary |
| `weather in [city]` | Live weather |
| `trivia` | Random trivia question |
| `debate [topic]` | Argues both sides |
| `number game` | Guessing game 1–100 |
| `word game` | Word guessing with hints |
| `riddle` | Classic riddle |
| `be formal` / `be playful` | Changes personality |
| `focus on [task]` | Activates focus mode |
| `my score` | ORYN score card |
| `knock knock` | 🥚 Try it... |
| `help` | Full feature list |

---

##  Architecture
```
src/chatbot/
├── intelligence/          ← The brain
│   ├── MoodEngine         ← Emotion detection
│   ├── BondSystem         ← Relationship levels
│   ├── LongMemory         ← Cross-session memory
│   ├── DailyDigest        ← Morning briefing
│   ├── PersonalityEngine  ← Tone modes
│   ├── MiniGames          ← In-chat games
│   └── EasterEggs         ← Hidden surprises
├── service/               ← Logic layer
│   ├── SmartChatBot       ← Main brain
│   └── WebApiService      ← Free API calls
├── ui/                    ← Everything visual
│   ├── ORYNBubble         ← Glassmorphism bubbles
│   ├── ORYNIcon           ← Programmatic logo
│   ├── ParticleBackground ← Animated particles
│   └── VoiceInput         ← Mic button
└── util/                  ← Support layer
    ├── AppConfig          ← Rose Noir theme
    ├── SoundManager       ← Audio (no files needed)
    └── PdfExporter        ← Export to PDF
```

---

##  Built With

**100% pure Java — zero external libraries**

`Java Swing` · `AWT Graphics2D` · `java.net.http` · `javax.sound.sampled` · `java.awt.print` · `PowerShell System.Speech`

---

## Roadmap

- [ ] Image drop and analysis
- [ ] Multi-language support  
- [ ] Plugin system for custom commands

---

<div align="center">

*"Knowledge is the light that never dims."*

**— ORYN **

</div>
