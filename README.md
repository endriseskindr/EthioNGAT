# ንጋት Ethiopia (Nigat Ethiopia) - Android App

Ethiopian university entrance exam preparation app with 1,000 practice questions and 3,356 vocabulary words with spaced repetition (SM-2 algorithm).

## Features

### 📚 Question Bank (1,000 Questions)
- **11 Chapters** organized into 3 sections:
  - **Quantitative** (Ch 1-5): Number Systems, Algebra, Applied Math, Geometry, Statistics
  - **Analytical Reasoning** (Ch 6-8): Logic, Critical Reasoning, General Awareness
  - **Language** (Ch 9-11): Grammar, Vocabulary, Reading Comprehension
- 78 questions include a 5th option (E)
- **Appendix B**: 28 specially flagged "Trap Questions" with common-mistake explanations

### 📖 Vocabulary (3,356 Words)
- 244 thematic clusters (e.g., Science, Politics, Emotions, Academic)
- Part-of-speech tagging (N/V/ADJ/ADV/PREP)
- Definitions + example sentences for every word
- Live search across words and definitions (200ms debounce)
- Cluster filter chips

### 🧠 Spaced Repetition (SM-2 Algorithm)
- Every question and vocabulary item is tracked independently
- Flashcard mode with 4 difficulty ratings: Again (0) / Hard (2) / Good (4) / Easy (5)
- Auto-adjusting Ease Factor (minimum 1.3) and review intervals
- "Due Today" dashboard showing items ready for review

### 📊 Progress Tracking
- Overall + per-section progress bars
- Last 7 days activity, mastered items, struggled items
- One-tap reset with confirmation dialog

### 🎯 Quiz Engine
- 3 modes: Chapter practice, Quick Quiz (20 random), Trap Questions (Appendix B)
- Dynamic A/B/C/D/E buttons (E auto-hidden when not applicable)
- Instant answer feedback with color highlighting
- Detailed explanations after each answer
- Progress indicator + navigation controls
- Results screen with grade tiers (≥85% Excellent, ≥70% Good, ≥50% Continue, else Review)

## Tech Stack

- **Kotlin** 1.9.20
- **Android Gradle Plugin** 8.2.0
- **Room** 2.6.1 with `createFromAsset()` (pre-packaged 1.2MB SQLite DB)
- **Material Components** 1.11.0
- **AndroidX Lifecycle + Coroutines** 1.7.3
- **View Binding**
- **Min SDK 21**, **Target SDK 34**

## Project Structure

```
NigatEthiopia/
├── build.gradle                    # Project-level (AGP, Kotlin, versions)
├── settings.gradle
├── gradle.properties
├── gradle/wrapper/gradle-wrapper.properties  # Gradle 8.2
└── app/
    ├── build.gradle                # applicationId "com.ngat.ethiopia", Room kapt
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml     # 7 activities declared
        ├── assets/ngat_seed.db     # Pre-packaged SQLite (1,000 Q + 3,356 V)
        ├── java/com/ngat/ethiopia/
        │   ├── NigatApp.kt                    # Application + DI
        │   ├── data/
        │   │   ├── NigatDatabase.kt           # Room @Database
        │   │   ├── NigatRepository.kt         # Unified repo + SM-2 recording
        │   │   ├── entity/   {Chapter, Question, Cluster, Vocabulary, UserProgress}
        │   │   └── dao/      {Chapter, Question, Vocabulary, UserProgress}Dao
        │   ├── util/SM2Algorithm.kt           # Full SM-2 implementation
        │   └── ui/
        │       ├── home/HomeActivity
        │       ├── chapters/{ChaptersActivity, ChapterAdapter}
        │       ├── quiz/QuizActivity          # 3 quiz modes
        │       ├── vocabulary/{VocabularyActivity, VocabularyAdapter, VocabDetailActivity}
        │       ├── progress/ProgressActivity
        │       └── detail/ResultActivity
        └── res/
            ├── layout/      (10 XML layouts)
            ├── drawable/    (button states + vector icons)
            ├── mipmap-*/    (adaptive + legacy launcher)
            └── values/      {colors, strings, themes}
```

## Database Schema (5 Tables)

| Table | Rows | Key Fields |
|---|---|---|
| `chapters` | 11 | id, name, section, item_count |
| `questions` | 1,000 | id (q0001..), chapter_id, question_text, option_a-e, answer_key, explanation, is_trap |
| `clusters` | 244 | id, name (unique), item_count |
| `vocabulary` | 3,356 | id (v0001..), word, pos, definition, example, cluster_id |
| `user_progress` | dynamic | PK(item_type, item_id), repetitions, ease_factor, interval_days, due_at |

**7 upstream data issues manually corrected** (v0425, v1072, v1132, v1171, v2717, v3047, v3292) — see `build_seed.py` in original seed assets.

## Building

### Option A — Local (Android Studio / JDK 17)

```bash
cd NigatEthiopia
./gradlew assembleDebug     # → app/build/outputs/apk/debug/app-debug.apk
```

Requires:
- Android Studio Hedgehog+ (or JDK 17 + Gradle 8.2)
- Android SDK Platform 34

---

### Option B — ☁️ GitHub Actions (RECOMMENDED — no local tools!)

The project ships with a ready-to-use CI workflow at `.github/workflows/build-apk.yml`.
Every push to `main`/`master` (or manual click) builds the APK on GitHub's free Ubuntu runners.

**Setup (30 seconds):**

```bash
# 1. Unzip and enter the Android project root
unzip NigatEthiopia_Project.zip
cd NigatEthiopia

# 2. Create a GitHub repo (https://github.com/new) — name it e.g. "nigat-ethiopia"

# 3. Push
git init
git checkout -b main
git add .
git commit -m "Initial commit: ንጋት Ethiopia Android app"
git remote add origin https://github.com/YOUR_USERNAME/nigat-ethiopia.git
git push -u origin main
```

**Get the APK:**
1. Open your repo → **Actions** tab
2. Click the top workflow run ("Build ንጋት Ethiopia APK")
3. Scroll to **Artifacts** → download `nigat-ethiopia-debug.zip`
4. Unzip → `app-debug.apk` → install on your phone 📱

The workflow uses **`gradle/actions/setup-gradle@v4`** which auto-provisions Gradle 8.2 + JDK 17 + dependency caching — no `gradlew` script or wrapper JAR needs to be committed.

## Color System

| Role | Hex | Usage |
|---|---|---|
| Primary | `#0D47A1` | App bar, brand, buttons |
| Accent | `#FF6F00` | Progress, CTAs, launcher "N" |
| Quantitative | `#1976D2` | Ch 1-5 section |
| Analytical | `#388E3C` | Ch 6-8 section |
| Language | `#C2185B` | Ch 9-11 + vocab cards |
| Correct | `#2E7D32` | Right answers |
| Wrong | `#C62828` | Wrong answers |
| Trap | `#FFF3E0` / `#E65100` | Appendix B warnings |

## SM-2 Algorithm Quick Reference

- Quality 0-2 → reset repetitions to 0, interval = 1 day
- Quality 3 → interval = 6 days
- Quality ≥4 → interval = previous × ease_factor
- Ease Factor updated: `EF = EF + (0.1 - (5-q) × (0.08 + (5-q) × 0.02))`
- EF clamped to minimum 1.3
- Due dates stored as epoch milliseconds in `user_progress.due_at`
