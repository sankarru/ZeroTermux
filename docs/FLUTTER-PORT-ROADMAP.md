# ZeroTermux → Flutter (Dart-equivalents) rewrite

Status: **roadmap only** — no implementation started.
Last verified: 2026-10-15, against `/home/flux/pro/zerotermux` (commit `c8b9904`).

**Strategy (user-directed):** replace each Java component with its Dart/Flutter
equivalent, which removes every `com.android.support` reference — and with them the need
for jetifier entirely.

---

## 1. Why removing jetifier is legitimate here

**Target platform: `minSdk 26` (Android 8.0).** Set in both build paths so they agree:

- `gradle.properties` → `minSdkVersion=26` (single source; all four modules read
  `project.properties.minSdkVersion`)
- `workspace.json` → `facets.android.minSdk = 26` for all four modules (acs path)

This is not cosmetic. 26 is the desugar-free floor: `java.time`, `java.util.stream` and
most of `java.nio.file` land natively at API 26, so `coreLibraryDesugaring` /
`desugar_jdk_libs:2.0.4` becomes largely redundant. **Audit and likely drop it** —
see §6. Keeping the two files in sync matters: acs silently ignores edits that disagree.

The project targets `targetSdk 28` and `compileSdk 36`; leave both alone. minSdk 26
gates off Android 7.x and older only; the test device is Android 15 (SDK 35).

Earlier `jetifier: false` failed because support-lib classes were still *referenced*
(`ClassCastException: android.support.v7.widget.ContentFrameLayout cannot be cast to
androidx...`). Disabling jetifier alone is invalid.

It becomes valid **only when zero `android.support.*` references remain**, because then
no rewriting is needed at all. Current holders of those references:

| Holder | Files touched | Dart replacement |
|---|---|---|
| `XXPermissions` 11.5 | 16 | `permission_handler` v13.0.2 |
| `imagepicker` 2.2.7 | 2 | `image_picker` v1.2.4 (official) |
| `fastandrutils` 0.8.0 | 2 | TBD — audit usage |
| `smart-swipe` / `-x` | 2 | Flutter `Drawer` |

Also drop the direct `com.android.support:support-fragment:24.2.0` dependency.

**Package availability verified on pub.dev** (not assumed):

| Package | Version | Dart SDK | Native? |
|---|---|---|---|
| `xterm` (xterm.dart) | 4.0.0 | `>=3.0.0 <4.0.0` | **pure Dart** |
| `flutter_pty` | 0.4.2 | `>=3.0.0 <4.0.0` | native (android/ios/linux/macos/windows) |
| `image_picker` | 1.2.4 | `^3.11.0` | official |
| `permission_handler` | 13.0.2 | `^3.6.0` | — |
| `file_picker` | 13.1.0 | `^3.10.0` | — |
| `shared_preferences` | 2.5.6 | `^3.11.0` | official |

Local toolchain: Flutter 3.48.0 stable / Dart 3.13.1 at `~/dev/flutter`. acs 0.1.0 with a
working `flutter-app` template.

---

## 2. What gets deleted

The native surface is far smaller than the file counts suggest — the entire JNI layer is
**five functions**:

```
terminal_JNI_createSubprocess   terminal_JNI_setPtyUTF
terminal_JNI_setPtyWindowSize   terminal_JNI_waitFor   terminal_JNI_close
```

| Component | Size | Fate |
|---|---|---|
| `terminal-view` (`TerminalView`, `TerminalRenderer`, gestures, selection) | 9 Java | **deleted** → `xterm` |
| `terminal-emulator` (`TerminalSession`, JNI) | 14 Java + `termux.c` | **deleted** → `xterm` + `flutter_pty` |
| `termux-shared` | 120 Java | mostly replaced; prefs/utils stay behind a thin host |
| `app` UI layer | 331 Java + 299 Kotlin | rewritten in Dart |
| `com.termux.termcore` | 298 Kotlin / 39 subpackages | per-subsystem; engines stay native |
| **bootstrap** `bootstrap-aarch64.zip` | 32 MB | **must survive** — full userland (bash, gnutls, `libtermux-exec`) |
| `termux-bootstrap.c` | 330 B | **must survive** — unpacks the above |

Net: the Android UI + terminal engine go away. The **Linux userland stays** — it is not
UI and is the actual product value.

---

## 3. Phase 0 — PTY spike (DO THIS FIRST, 2–3 days)

**This is the make-or-break gate and it is not about Flutter.**

Termux cannot simply `forkpty()` a shell on Android. Its 32 MB bootstrap ships
`libtermux-exec_nos_c_tre.so`, a patched `ld.so`, and `libpcre2`, specifically to work
around Android's `execve` restrictions (W^X, SELinux, seccomp). `flutter_pty` uses
ordinary NDK `forkpty`/`execve`.

Spike must answer, on a real device:

1. Can `flutter_pty` spawn a process from the unpacked Termux bootstrap at all?
2. If not, can it spawn `/system/bin/sh` — i.e. is the failure bootstrap-specific or
   Android-wide?
3. Does `termux-bootstrap.c` + `libtermux-exec` still need to run as the spawn target
   (bootstrap → shell), with `flutter_pty` only providing the PTY?

**Exit:** a Dart-driven shell in a Flutter app running a real command on device.

**If (1) fails, the plan changes fundamentally** — the PTY layer must be written in C
against `libtermux-exec`, and everything downstream shifts right. Do not build 46
screens before knowing this.

---

## 4. Remaining phases

### Phase 1 — Flutter shell + `xterm` (1 week)
- acs `flutter-app` scaffold; `xterm` widget for the emulator view.
- `flutter_pty` wired to the verified spawn path.
- Extra-keys bar becomes a Flutter widget (Termux-specific, not part of xterm).
- Keyboard: IME + six/twelve-key input reimplemented as Flutter key handling.

**Exit:** shell runs `bash`, renders, handles selection and resize on device.

### Phase 2 — Eliminate the support libs (1 week)
Swap `XXPermissions`→`permission_handler`, `imagepicker`→`image_picker`,
`smart-swipe`→`Drawer`, remove `support-fragment`, audit `fastandrutils`.

**Exit:** no `com.android.support` reference remains; jetifier disabled; build is green.
**This is the point where "remove jetifier" becomes real.**

### Phase 3 — termcore, screen-shaped only (2–4 weeks)
Use `ZtPageNavigationRegistry.kt` (313 lines) as the migration checklist.

Port UI. Do **not** port engines: `adb`, `scrcpy`, `ftp`, `http_service`, `ai`,
`workstation`, `bosybox`, `libsu`, `otg`, `keybord` are services/native integrations and
stay on the Android side behind platform channels.

**Exit:** settings parity; engines reachable from Flutter.

### Phase 4 — Cutover (1–2 weeks)
Flutter becomes the only Activity; keep Java reachable behind a flag for one release;
then delete the replaced modules.

---

## 5. Risks

| Risk | Severity | Mitigation |
|---|---|---|
| **`flutter_pty` cannot exec the Termux bootstrap** (W^X / SELinux / seccomp) | **Critical** | Phase 0 gate, before any UI work |
| This is a rewrite, not a port — 630 + 120 files | High | Incremental; registry as checklist; never big-bang |
| acs Flutter builds `arm64-v8a` only | Medium | Matches `bootstrap-aarch64.zip`; blocks universal APKs |
| acs `flutter-app` vs multi-module Gradle | Medium | Spike answers it; Gradle path proven if acs balks |
| Losing terminal scrollback/state across navigation | Medium | Keep session owner above the widget tree |
| acs artifact quota exhausted | Low | Use `build-release-asset.yml` (release assets) |

Note: the `ca-jetifier` bug stops mattering the moment Phase 2 lands — which is a genuine
benefit of this direction.

---

## 6. Open decisions

1. **`coreLibraryDesugaring`** — `app/build.gradle:229` sets
   `coreLibraryDesugaringEnabled true` with `desugar_jdk_libs:2.0.4`. At minSdk 26 this is
   largely redundant. Audit callers first (`java.time`, streams, `java.nio.file`); drop it
   if clean, because it also removes a `desugar_jdk_libs` dependency and speeds up d8.
   This is a behaviour change, so it is called out rather than done silently.
2. **Is the ZeroTermux branding revert intentional?** Working tree strips branding HEAD has,
   and that revert introduced a duplicate-string resource bug that is *also* in HEAD
   (`zt_nav_page_termux_settings`). Resolve before porting.
3. **acs or Gradle as the end state?** The Gradle path is proven working on CI
   (https://github.com/sankarru/ZeroTermux/releases/tag/ci-1); acs Flutter is unproven.
4. **`fastandrutils` usage** — 2 files reference it; confirm whether it can be dropped
   outright.