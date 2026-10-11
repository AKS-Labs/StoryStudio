# Interactive Photo Canvas to Instagram Stories Integration

An expressive, creative Android studio app built with Jetpack Compose, CameraX, custom multi-touch gesture canvas, Figma-inspired Project Status Stickers, and direct Instagram Stories integration.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following design and architectural choices have been confirmed and tailored to match Google Creative Lab / Androidify styling standards:

- **Visual Aesthetic & Theme**: Google Creative Lab aesthetic with vibrant Material 3 Expressive tokens, playful bouncy spring animations, rounded pill containers, and dynamic color accents.
- **Sticker Suite**: Comprehensive library of Agile & Project Status Badges ("IN PROGRESS", "DONE", "SHIPPED", "BLOCKED", "IN REVIEW", "WIP", "LGTM", "HOTFIX", "MERGED") along with dev stamps and expressive reactions.
- **Camera & Photo Ingestion**: In-app CameraX live viewfinder with camera switch (front/back), flash/torch controls, grid toggle, and seamless zero-permission Android Photo Picker (`PickVisualMedia`) fallback.
- **Instagram Stories Integration**: Direct intent `com.instagram.share.ADD_TO_STORY` targeted at `com.instagram.android` with `FileProvider` URI permission grants, background gradient metadata, composite high-res image rendering, clipboard caption helper, and graceful fallback with Play Store redirect if Instagram is not installed.
- **Git & CI/CD Workflow**: Micro-commits following conventional commit standards (`feat:`, `fix:`, `docs:`) and a dedicated GitHub Actions release APK builder workflow (`.github/workflows/build-apk.yml`).

---

## 1. Overview & Core Concept

**StoryCanvas** is an expressive mobile creation studio for developers, designers, and creators to turn real-time work-in-progress moments into polished, branded Instagram Stories. 

Users capture their current setup, code screen, bug, or team moment via the camera, mark it up with freehand glowing pens and markers, slap on high-fidelity Project Status badges (with full pinch, zoom, rotate, and layering gestures), craft punchy captions with stylish typography styles, and fire the final creation directly into Instagram Stories with one fluid tap.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Capture Screen (Viewfinder)**:
   - Full-bleed edge-to-edge live camera preview using CameraX `PreviewView`.
   - Floating pill action bar: Flash toggle, Camera flip (front/back), Grid overlay toggle.
   - Large shutter button with tactile animated ring and haptic feedback.
   - Quick gallery import button launching Android Photo Picker.
   - Permission onboarding screen with playful Google-style illustration and explanation.

2. **Creative Canvas Studio Screen**:
   - High-fidelity preview of captured photo centered in 9:16 Instagram Story aspect ratio.
   - **Top Action Bar**: Discard button (with confirmation modal), Undo/Redo buttons, Clear layer, and Finish/Share CTA.
   - **Floating Creative Toolbar**:
     - ✏️ **Draw Tool**: Freehand stroke rendering with color palette picker (8 vibrant M3 tones), stroke size preview slider, and brush types (Smooth Pen, Translucent Highlighter, Neon Glow, Eraser).
     - 🏷️ **Sticker Tool**: Bottom sheet drawer with Figma Project Status Stickers (Agile status pills, GitHub-style dev badges, expressive stamps).
     - 💬 **Caption Tool**: Rich text overlay with customizable typography styles (Modern Sans, Script, Marker, Bubble), pill backgrounds (Solid, Translucent, Outline), and text colors.
   - **Canvas Gestures**:
     - Direct drawing when Draw mode is active.
     - Multi-touch transform gestures (drag, pinch-to-scale, rotate) for stickers and text overlays.
     - Selected sticker bounds with deletion handle, rotation knob, and duplication.

3. **Caption & Export Modal / Story Preload**:
   - Quick caption composer with preset tech hashtags (`#shipped`, `#wip`, `#devlife`, `#code`).
   - High-resolution off-screen composite rendering to clean JPEG/PNG file.
   - Instant launch of Instagram Stories via `com.instagram.share.ADD_TO_STORY`.
   - Friendly modal if Instagram is not detected on the device, offering one-tap Play Store installation and direct gallery save.

### Visual Identity & Theme Tokens

- **Dominant Colors**: Deep ink canvas (`#121316`), Electric Violet (`#7C4DFF`), Neon Mint (`#00E676`), Coral Flame (`#FF5252`), Cyber Amber (`#FFD600`), and Soft Lavender (`#E8DEF8`).
- **Typography**: Expressive display hierarchy with Google Sans-styled typography, clean readable labels, and pill badges.
- **Motion**: Spring physics (`spring(dampingRatio = 0.75f, stiffness = 400f)`) for sticker placement, sheet slide-ins, and button presses.

---

## 3. Key Product Decisions & Trade-Offs

| Decision | Chosen Approach | Why | Alternatives Considered |
|---|---|---|---|
| **Direct IG Stories Integration** | `com.instagram.share.ADD_TO_STORY` with explicit package `com.instagram.android` and `FileProvider` | Directly opens Instagram Stories editor without intermediary Android system share sheets | Generic `ACTION_SEND` (rejected: opens generic system picker instead of directly IG Stories) |
| **Canvas Architecture** | Multi-layer Compose Canvas + Custom Gesture Handler | Seamless rendering of background photo, path vector drawing list, and draggable/rotatable sticker models | Web-based canvas (rejected: slow, lacks native tactile feel) |
| **Composite Image Export** | Off-screen `android.graphics.Canvas` and `Bitmap.compress` matching 1080x1920 story dimensions | Guarantees crisp high-resolution export matching Instagram Stories aspect ratio | View snapshot / screen capture (rejected: low resolution, captures UI buttons) |
| **Sticker Source** | Vector-based and custom-rendered Project Status badges inspired by the Figma design | Crisp at all scale factors, zero pixelation, lightweight APK size | Raster web downloads (rejected: requires network, prone to failure) |
| **Fallback for Missing IG** | Play Store redirect + local gallery save dialog | Users never lose their created artwork if Instagram is absent | Silent crash or unhandled error (rejected: poor UX) |

---

## 4. Technical Architecture & Data Strategy

### System & Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                          MainActivity & Navigation                     │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
        ┌───────────────────────────┴───────────────────────────┐
        ▼                                                       ▼
┌───────────────────────────────┐       ┌───────────────────────────────┐
│     CameraCaptureScreen       │       │       StoryCanvasScreen       │
│  - CameraX Preview & Capture  │       │  - Photo Background Layer     │
│  - Photo Picker Fallback      │──────>│  - Freehand Path Draw Layer   │
│  - Flash / Lens Controls      │       │  - Interactive Sticker Layer  │
│  - Permission State UI        │       │  - Caption Text Layer         │
└───────────────────────────────┘       └───────────────┬───────────────┘
                                                        │
                                ┌───────────────────────┴───────────────────────┐
                                ▼                                               ▼
                ┌───────────────────────────────┐               ┌───────────────────────────────┐
                │        StoryCanvasViewModel   │               │      StickerDrawerSheet       │
                │  - Undo / Redo Stacks         │               │  - Project Status Badges      │
                │  - Active Tool (Pen, Sticker) │               │  - Dev & Agile Stamps         │
                │  - Sticker Transform States   │               │  - Reaction Emojis            │
                └───────────────┬───────────────┘               └───────────────────────────────┘
                                │
                                ▼
                ┌───────────────────────────────┐
                │   StoryExporter & IG Intenter │
                │  - Offscreen 1080x1920 Render │
                │  - FileProvider Cache URI     │
                │  - Instagram Stories Intent   │
                │  - Clipboard Caption Copy     │
                └───────────────────────────────┘
```

### Data Models & State Structure

```kotlin
// Drawing path model
data class DrawPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val brushStyle: BrushStyle // Pen, Highlighter, Glow, Eraser
)

// Sticker item model with transform matrix
data class CanvasSticker(
    val id: String = UUID.randomUUID().toString(),
    val type: StickerType,
    val title: String,
    val subtitle: String? = null,
    val iconRes: Int? = null,
    val badgeColor: Color,
    val textColor: Color,
    val offset: Offset,
    val scale: Float = 1.0f,
    val rotation: Float = 0.0f
)

// Canvas state
data class CanvasState(
    val basePhotoUri: Uri?,
    val paths: List<DrawPath> = emptyList(),
    val undonePaths: List<DrawPath> = emptyList(),
    val stickers: List<CanvasSticker> = emptyList(),
    val selectedStickerId: String? = null,
    val caption: String = "",
    val activeTool: CanvasTool = CanvasTool.NONE,
    val brushColor: Color = Color(0xFF00E676),
    val brushWidth: Float = 8f,
    val isExporting: Boolean = false
)
```

### CI/CD Workflow (`.github/workflows/build-apk.yml`)

A production GitHub Actions workflow that:
- Runs on push to `main`/`master` and pull requests.
- Sets up JDK 17.
- Builds debug and release APK.
- Uploads the generated APK as a workflow artifact named `app-release-apk` for direct download from the Actions tab.

---

## 5. Implementation Stages

1. **Step 1: Project Configuration & Dependencies**:
   - Enable CameraX, Coil, and Accompanist permissions in `build.gradle.kts`.
   - Update `metadata.json` with app name "StoryCanvas" and description.
   - Configure `AndroidManifest.xml` with Camera permissions, FileProvider, and Instagram `<queries>` element.
2. **Step 2: Theme & Expressive Styling**:
   - Modern M3 Expressive color palette, dynamic typography, and shapes.
3. **Step 3: Camera Capture & Photo Picker**:
   - CameraX preview, capture controller, permission handler, front/back switch, and Android Photo Picker fallback.
4. **Step 4: Sticker Library**:
   - Agile status stickers (In Progress, Done, Blocked, In Review, Shipped, WIP) + Dev stamps (LGTM, Merged, Hotfix).
5. **Step 5: Interactive Drawing & Transformable Canvas**:
   - Smooth gesture drawing, brush styles, undo/redo, pinch/zoom/rotate sticker manipulation, and text caption tool.
6. **Step 6: Instagram Stories Integration & Exporter**:
   - 1080x1920 composite bitmap rendering, FileProvider URI sharing, direct Instagram Stories intent with fallback to Play Store / local gallery.
7. **Step 7: GitHub Actions CI/CD Workflow**:
   - Setup `.github/workflows/build-apk.yml` for artifact generation.
8. **Step 8: Git Commit History & Build Verification**:
   - Clean, conventional git commit history for every milestone and `compile_applet` verification.
