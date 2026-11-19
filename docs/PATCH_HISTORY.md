# Patch history: everything zinwaq25-sableos changed, and where it lives now

Generated 2026-11-19 from the patch inventory of `aimindseye/zinwaq25-sableos` and checked against this repo. From now on app changes are edits to the source here; no new patch scripts.

## What was checked

- **203 source patches** (`patches/sable-src/`, applied at import): 98,111 meaningful added lines. **81,290** are in this repo as written, **3,712** were replaced by a later patch, **13,109** are not here. Of those, about 12,900 sit in modules that were removed on purpose (the old Sable Start presentation check, and Sable Games, which moved to `sablegames`). 235 are in modules still here (rewording such as "Sable Start" to "Sable Launcher", and the clipboard code rewritten later); the features were spot-checked and are present.
- **Flavor patch scripts** (`sable-src/apps/r8/*/apply_sable_flavor.py`): the old verifiers were run against this repo. Mail, Terminal, Speech and Text Reader pass in full. Launcher passes all checks except `q25_fit`, which fails on purpose: it expected the Command icon on the old BBOS 10 bar, and the Sable home proposal removes it (PR #3).
- **Framework patches** (`patches/framework/`, 182 files) change the Android tree, not this repo. They stay in zinwaq25-sableos by decision; listed at the end.

## 1. Source patches (patches/sable-src), in order

| Patch | Date | What | Files here | Added lines still here |
|---|---|---|---|---|
| 0001 |  | 0001-zinwa-q25-keyboard-camera-profiles.patch | `titan2-platform/camera/src/main/java/org/sableos/titan2/camera/core/DeviceProfile.kt`, `titan2-platform/camera/src/test/java/org/sableos/titan2/camera/core/CameraCoreTest.kt` +2 more | 31/33, 2 replaced later |
| 0002 |  | 0002-zinwa-q25-t3-setup-weather-keyprobe.patch | `android/weather/build.gradle.kts`, `android/weather/src/main/java/org/sableos/weather/CitiesSection.kt` +21 more | 1276/1401, 125 replaced later |
| 0003 |  | 0003-zinwa-q25-kf-b-system-tokens.patch | (outside this repo: sable-src/src/android) | n/a |
| 0004 |  | 0004-zinwa-q25-kf-d-allapps-privacy-responsive.patch | `android/calendar/src/main/java/org/sableos/calendar/MainActivity.kt`, `android/design/src/test/java/org/sableos/design/SableAlphabetAndMiniPlayerTest.kt` +13 more | 494/945, 124 replaced later |
| 0005 |  | 0005-zinwa-q25-kf-a-hub-notification-policy.patch | `android/config/detekt.yml`, `android/hub/src/main/AndroidManifest.xml` +48 more | 1752/1847, 95 replaced later |
| 0006 |  | 0006-zinwa-q25-kf-c-sable-tools.patch | `titan2-platform/keyboard/src/main/AndroidManifest.xml`, `titan2-platform/settings.gradle.kts` +40 more | 3350/3721, 370 replaced later |
| 0007 |  | 0007-zinwa-q25-integration-fixes.patch | `android/hub/src/main/java/org/sableos/hub/HubRepository.kt`, `android/hub/src/main/java/org/sableos/hub/ui/HubScreen.kt` | 11/20, 9 replaced later |
| 0008 |  | 0008-zinwa-q25-adaptive-phosphor-icons.patch | `reader/leisure/src/main/AndroidManifest.xml`, `reader/leisure/src/main/res/drawable/ic_sable_reader.xml` +97 more | 639/833, 57 replaced later |
| 0009 |  | 0009-zinwa-q25-app-corner-style.patch | `reader/leisure/src/main/java/org/sableos/design/SableCornerStyle.kt`, `reader/leisure/src/main/java/org/sableos/design/SableGlobalAppearance.kt` +8 more | 255/291, 36 replaced later |
| 0010 |  | 0010-zinwa-q25-daily-driver-core-apps.patch | `android/sablestart-presentation-check/build.gradle.kts`, `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/CoreAppIdentityTest.kt` | 0/35 |
| 0011 |  | 0011-zinwa-q25-ci-quality-fixes.patch | `reader/gradle/verification-metadata.xml`, `android/.editorconfig` +18 more | 282/312, 30 replaced later |
| 0012 |  | 0012-zinwa-q25-camera-50mp-high-res-only.patch | `titan2-platform/camera/src/main/java/org/sableos/titan2/camera/core/CapabilityInterpreter.kt`, `titan2-platform/camera/src/main/java/org/sableos/titan2/camera/core/DeviceProfile.kt` +1 more | 48/48 |
| 0013 |  | 0013-zinwa-q25-keyboard-first-calculator-and-ime-shortcuts.patch | `android/calculator/gradle.lockfile`, `android/calculator/build.gradle.kts` +8 more | 183/191, 4 replaced later |
| 0014 |  | 0014-zinwa-q25-start-status-rings-and-quick-bar.patch | `android/sablestart-presentation-check/build.gradle.kts`, `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/QuickBarTest.kt` +1 more | 0/57, 27 replaced later |
| 0015 |  | 0015-zinwa-q25-design-gap-waves.patch | `android/calendar/src/main/java/org/sableos/calendar/MainActivity.kt`, `android/hub/src/main/java/org/sableos/hub/HubRepository.kt` +7 more | 84/167, 41 replaced later |
| 0016 |  | 0016-zinwa-q25-design-gap-followups.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/QuickBarTest.kt`, `titan2-platform/camera/src/main/java/org/sableos/titan2/camera/android/CameraController.kt` +34 more | 891/993, 98 replaced later |
| 0017 |  | 0017-zinwa-q25-waves-2-3-and-sonar-cleanup.patch | `reader/core/build.gradle.kts`, `reader/core/src/androidMain/kotlin/org/sableos/reader/core/audio/AudiobookSourceLoader.kt` +118 more | 4760/5143, 283 replaced later |
| 0018 |  | 0018-zinwa-q25-sonar-second-pass.patch | `reader/core/build.gradle.kts`, `reader/leisure/build.gradle.kts` +4 more | 53/53 |
| 0019 |  | 0019-zinwa-q25-hub-dock.patch | `android/hub/src/main/java/org/sableos/hub/ConnectedAppsActivity.kt`, `android/hub/src/main/java/org/sableos/hub/ConnectedAppsInventory.kt` +20 more | 865/892, 27 replaced later |
| 0020 |  | 0020-zinwa-q25-mail-without-appearance-provider.patch | (outside this repo: sable-src/apps/r8/mail) | n/a |
| 0021 |  | 0021-zinwa-q25-start-three-page-design.patch | `android/sablestart-presentation-check/build.gradle.kts`, `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/HomeLayoutTest.kt` +2 more | 0/69, 18 replaced later |
| 0022 |  | 0022-zinwa-q25-start-bb10-layout.patch | `android/sablestart-presentation-check/build.gradle.kts`, `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/Bb10HomeTest.kt` +5 more | 0/190, 49 replaced later |
| 0029 |  | 0029-zinwa-q25-quick-settings-three-columns.patch | (outside this repo: sable-src/src/android) | n/a |
| 0030 |  | 0030-zinwa-q25-hub-filters-search-actions-privacy.patch | `android/hub/src/main/AndroidManifest.xml`, `android/hub/src/main/java/org/sableos/hub/HubModels.kt` +14 more | 840/981, 141 replaced later |
| 0040 |  | 0040-zinwa-q25-camera-keys-visuals-default.patch | `titan2-platform/camera/src/main/AndroidManifest.xml`, `titan2-platform/camera/src/main/java/org/sableos/titan2/camera/android/CaptureResult.kt` +9 more | 318/377, 59 replaced later |
| 0041 |  | 0041-zinwa-q25-calendar-week-split-keys.patch | `android/calendar/src/main/java/org/sableos/calendar/CalendarRepository.kt`, `android/calendar/src/main/java/org/sableos/calendar/CalendarWeek.kt` +2 more | 450/490, 40 replaced later |
| 0045 |  | 0045-zinwa-q25-mail-split-pane-dock-and-artifact-keys.patch | (outside this repo: sable-src/apps/r8/mail) | n/a |
| 0046 |  | 0046-zinwa-q25-media-key-labels.patch | `android/media/src/main/java/org/sableos/media/MediaScreen.kt`, `android/media/src/test/java/org/sableos/media/MediaKeymapTest.kt` | 24/26, 2 replaced later |
| 0050 |  | 0050-zinwa-q25-display-compat-applies-profiles.patch | `titan2-platform/displaycompat/src/main/AndroidManifest.xml`, `titan2-platform/displaycompat/src/main/java/org/sableos/titan2/displaycompat/android/DisplayCompatApp.kt` +11 more | 232/274, 42 replaced later |
| 0051 |  | 0051-zinwa-q25-secure-vault.patch | `titan2-platform/settings.gradle.kts`, `titan2-platform/vault/build.gradle.kts` +15 more | 533/621, 88 replaced later |
| 0060 |  | 0060-zinwa-q25-sable-games-release-and-keyboard-play.patch | `android/games/build.gradle.kts`, `android/games/proguard-rules.pro` +7 more | 0/221, 32 replaced later |
| 0061 |  | 0061-zinwa-q25-sable-media-fm-radio.patch | `android/config/detekt.yml`, `android/media/src/main/AndroidManifest.xml` +9 more | 498/535, 37 replaced later |
| 0062 |  | 0062-zinwa-q25-start-frames-hide-private-space.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/Bb10HomeTest.kt` | 0/4 |
| 0063 |  | 0063-zinwa-q25-vault-reports-leftover-partial-copy.patch | `titan2-platform/vault/src/main/java/org/sableos/titan2/vault/android/VaultStore.kt` | 1/5, 4 replaced later |
| 0064 |  | 0064-zinwa-q25-ktlint-detekt-camera-vault-hub.patch | `android/calendar/src/main/java/org/sableos/calendar/CalendarWeek.kt`, `android/calendar/src/main/java/org/sableos/calendar/MainActivity.kt` +26 more | 488/527, 39 replaced later |
| 0065 |  | 0065-zinwa-q25-calculator-release-build.patch | `android/calculator/build.gradle.kts`, `android/calculator/proguard-rules.pro` | 2/5, 3 replaced later |
| 0066 |  | 0066-zinwa-q25-device-review-fixes.patch | `android/weather/src/main/AndroidManifest.xml`, `android/weather/src/main/java/org/sableos/weather/WeatherRefreshJob.kt` +2 more | 33/38, 5 replaced later |
| 0067 |  | 0067-zinwa-q25-square-screen-games-and-keyboard-strip.patch | `android/games/src/main/java/org/sableos/games/BoardSize.kt`, `android/games/src/main/java/org/sableos/games/Game2048Screen.kt` +9 more | 56/69, 13 replaced later |
| 0068 |  | 0068-zinwa-q25-setup-weather-city-step.patch | `android/weather/src/main/AndroidManifest.xml`, `android/weather/src/main/java/org/sableos/weather/WeatherSetupAccess.kt` +18 more | 445/483, 38 replaced later |
| 0069 |  | 0069-zinwa-q25-start-glyphs-grid-and-recents.patch | `android/sablestart-presentation-check/build.gradle.kts`, `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/AppGlyphsTest.kt` +1 more | 0/69 |
| 0070 |  | 0070-zinwa-q25-start-hub-page-one-hub.patch | `android/hub/src/main/AndroidManifest.xml`, `android/hub/src/main/java/org/sableos/hub/HubSnapshotProvider.kt` +7 more | 189/288, 1 replaced later |
| 0071 |  | 0071-zinwa-q25-messages-default-sms-app.patch | `android/messages/build.gradle.kts`, `android/messages/src/main/AndroidManifest.xml` +37 more | 2827/2892, 65 replaced later |
| 0072 |  | 0072-zinwa-q25-display-compat-tall-canvas-hotkey-rotation.patch | `titan2-platform/displaycompat/src/main/AndroidManifest.xml`, `titan2-platform/displaycompat/src/main/java/org/sableos/titan2/displaycompat/android/BootReceiver.kt` +21 more | 1114/1182, 68 replaced later |
| 0073 |  | 0073-zinwa-q25-device-review-2.patch | `android/games/src/main/java/org/sableos/games/BoardSize.kt`, `android/games/src/main/java/org/sableos/games/Game2048Screen.kt` +56 more | 655/908, 69 replaced later |
| 0074 |  | 0074-zinwa-q25-device-review-3.patch | `android/calculator/src/main/java/org/sableos/calculator/MainActivity.kt`, `android/calendar/src/main/AndroidManifest.xml` +35 more | 287/373, 59 replaced later |
| 0075 |  | 0075-zinwa-q25-dollar-key-and-translatable-setup.patch | `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/KeyMapper.kt`, `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/SableImeService.kt` +13 more | 339/378, 37 replaced later |
| 0076 |  | 0076-zinwa-q25-blackberry-features.patch | `android/design/src/test/java/org/sableos/design/SableListKeysTest.kt`, `android/hub/src/main/java/org/sableos/hub/ui/HubScreen.kt` +30 more | 750/903, 113 replaced later |
| 0077 |  | 0077-zinwa-q25-alpha-review.patch | `reader/leisure/src/main/java/org/sableos/design/SableTheme.kt`, `android/games/build.gradle.kts` +66 more | 156/1861, 140 replaced later |
| 0078 |  | 0078-zinwa-q25-bb7-return-from-hub.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/Bb7HomeTest.kt` | 0/9 |
| 0079 |  | 0079-zinwa-q25-audiobook-chapter-keys.patch | `reader/leisure/src/main/java/org/sableos/reader/ui/audio/AudiobookControls.kt`, `reader/reader-model/src/main/kotlin/org/sableos/reader/input/ReaderKeyMap.kt` +1 more | 13/13 |
| 0080 |  | 0080-zinwa-q25-reader-comics-audio-tabs-defaults-square-fit.patch | `reader/core/src/androidMain/kotlin/org/vaachak/reader/core/data/repository/SettingsRepository.kt`, `reader/leisure/src/main/java/org/sableos/reader/audio/AudioSettingsStore.kt` +20 more | 236/237, 1 replaced later |
| 0081 |  | 0081-zinwa-q25-sable-notes.patch | `android/notes/build.gradle.kts`, `android/notes/src/main/AndroidManifest.xml` +27 more | 1061/1134, 72 replaced later |
| 0082 |  | 0082-zinwa-q25-setup-translations.patch | `titan2-platform/setup/src/main/res/values-de/strings.xml`, `titan2-platform/setup/src/main/res/values-es/strings.xml` +1 more | 169/180, 8 replaced later |
| 0083 |  | 0083-zinwa-q25-keyboard-clipboard-history.patch | `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/ClipboardWatcher.kt`, `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/CompactStripView.kt` +6 more | 191/238, 6 replaced later |
| 0084 |  | 0084-zinwa-q25-sable-terminal.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/AppGlyphsTest.kt` | 0/1 |
| 0085 |  | 0085-zinwa-q25-app-strings.patch | `android/calculator/src/main/AndroidManifest.xml`, `android/calculator/src/main/java/org/sableos/calculator/CalculatorApplication.kt` +207 more | 5068/5437, 33 replaced later |
| 0086 |  | 0086-zinwa-q25-weather-translations.patch | `android/weather/src/main/res/values-ar/strings.xml`, `android/weather/src/main/res/values-de/strings.xml` +14 more | 278/624, 346 replaced later |
| 0087 |  | 0087-zinwa-q25-calculator-converter-shared-translations.patch | `android/calculator/src/main/res/values-ar/strings.xml`, `android/calculator/src/main/res/values-de/strings.xml` +34 more | 810/864, 54 replaced later |
| 0088 |  | 0088-zinwa-q25-app-translations.patch | `android/calendar/src/main/res/values-ar/strings.xml`, `android/calendar/src/main/res/values-de/strings.xml` +247 more | 25072/26572, 45 replaced later |
| 0089 |  | 0089-zinwa-q25-sos-pedometer-letter-shortcuts.patch | `android/sablestart-presentation-check/build.gradle.kts`, `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/LetterShortcutsTest.kt` +29 more | 646/677 |
| 0090 |  | 0090-zinwa-q25-start-bar-hub-dots-camera.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/QuickBarTest.kt` | 0/33, 3 replaced later |
| 0091 |  | 0091-zinwa-q25-hub-bb10-row-menu.patch | `android/hub/src/main/java/org/sableos/hub/HubText.kt`, `android/hub/src/main/java/org/sableos/hub/MainActivity.kt` +26 more | 442/458, 16 replaced later |
| 0092 |  | 0092-zinwa-q25-start-hub-message-list.patch | `android/hub/src/main/AndroidManifest.xml`, `android/hub/src/main/java/org/sableos/hub/HubSnapshotProvider.kt` +5 more | 129/134, 4 replaced later |
| 0093 |  | 0093-zinwa-q25-sable-speech-espeak-ng.patch | (outside this repo: sable-src/apps/r8/espeak) | n/a |
| 0094 |  | 0094-zinwa-q25-text-reader-speak-focused.patch | (outside this repo: sable-src/apps/r8/textreader) | n/a |
| 0095 |  | 0095-zinwa-q25-calendar-busy-rings-start-month.patch | `android/calendar/src/main/java/org/sableos/calendar/BusyMonth.kt`, `android/calendar/src/main/java/org/sableos/calendar/CalendarBusy.kt` +30 more | 442/489, 19 replaced later |
| 0096 |  | 0096-zinwa-q25-sable-tasks.patch | `android/notes/src/main/AndroidManifest.xml`, `android/notes/src/main/java/org/sableos/notes/NotesController.kt` +68 more | 1873/1951, 76 replaced later |
| 0097 |  | 0097-zinwa-q25-apps-rust-to-kotlin.patch | `android/build.gradle.kts`, `android/calculator/build.gradle.kts` +68 more | 669/1041, 73 replaced later |
| 0098 |  | 0098-zinwa-q25-first-compile-fixes.patch | `android/hub/src/main/java/org/sableos/hub/HubSnapshotProvider.kt`, `android/hub/src/main/java/org/sableos/hub/notifications/ConnectedActionRegistry.kt` +15 more | 147/149, 2 replaced later |
| 0099 |  | 0099-zinwa-q25-media-probes-lint-tasks-receiver.patch | `android/media/src/main/java/org/sableos/media/MediaFormats.kt`, `android/media/src/test/java/org/sableos/media/MediaFormatsTest.kt` +1 more | 144/144 |
| 0100 |  | 0100-zinwa-q25-hub-upcoming-prior-read-each-message.patch | `android/hub/src/main/java/org/sableos/hub/ConnectedAppsActivity.kt`, `android/hub/src/main/java/org/sableos/hub/HubModels.kt` +30 more | 219/229, 5 replaced later |
| 0101 |  | 0101-zinwa-q25-command-instant-actions.patch | `android/sablestart-presentation-check/build.gradle.kts`, `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/InstantActionsTest.kt` | 0/30 |
| 0102 |  | 0102-zinwa-q25-live-active-frames-media-controls.patch | `android/sablestart-presentation-check/build.gradle.kts`, `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/Bb10HomeTest.kt` | 0/11 |
| 0103 |  | 0103-zinwa-q25-bedside-and-calendar-next-for-lock-screen.patch | `android/calendar/src/main/AndroidManifest.xml`, `android/calendar/src/main/java/org/sableos/calendar/CalendarSnapshotProvider.kt` +23 more | 97/108, 11 replaced later |
| 0104 |  | 0104-zinwa-q25-sable-browser.patch | `android/browser/build.gradle.kts`, `android/browser/src/main/AndroidManifest.xml` +41 more | 1003/1027, 24 replaced later |
| 0105 |  | 0105-zinwa-q25-browser-and-bedside-lint.patch | `android/browser/src/main/java/org/sableos/browser/MainActivity.kt`, `android/browser/src/main/java/org/sableos/browser/core/PointerPhysics.kt` +9 more | 68/68 |
| 0106 |  | 0106-zinwa-q25-hub-snooze-and-remind-me-options.patch | `android/hub/src/main/AndroidManifest.xml`, `android/hub/src/main/java/org/sableos/hub/ConnectedAppsActivity.kt` +52 more | 333/335, 2 replaced later |
| 0107 |  | 0107-zinwa-q25-start-balance.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/Bb10HomeTest.kt` | 0/11 |
| 0108 |  | 0108-zinwa-q25-sable-spaces-work-space.patch | `titan2-platform/settings.gradle.kts`, `titan2-platform/setup/src/main/AndroidManifest.xml` +64 more | 1037/1070, 33 replaced later |
| 0109 |  | 0109-zinwa-q25-start-trackpad-flick.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/QuickBarTest.kt` | 0/36 |
| 0110 |  | 0110-zinwa-q25-bb7-home-delete.patch | (outside this repo: sable-src/src/android) | n/a |
| 0111 |  | 0111-zinwa-q25-bb7-tray-layout-and-accent-search.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/Bb7HomeTest.kt`, `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/CommandQueryTest.kt` | 0/25 |
| 0112 |  | 0112-zinwa-q25-bb7-own-tabs-and-hold-letters.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/Bb7HomeTest.kt` | 0/33 |
| 0113 |  | 0113-zinwa-q25-start-page-dots-only.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/QuickBarTest.kt` | 0/7 |
| 0114 |  | 0114-zinwa-q25-sable-print-to-go.patch | `titan2-platform/printtogo/build.gradle.kts`, `titan2-platform/printtogo/src/main/AndroidManifest.xml` +44 more | 1761/1837, 76 replaced later |
| 0115 |  | 0115-zinwa-q25-bb7-menu-import.patch | (outside this repo: sable-src/src/android) | n/a |
| 0116 |  | 0116-zinwa-q25-sable-compass.patch | `titan2-platform/compass/build.gradle.kts`, `titan2-platform/compass/src/main/AndroidManifest.xml` +35 more | 1245/1271, 26 replaced later |
| 0117 |  | 0117-zinwa-q25-spaces-work-options.patch | `titan2-platform/spaces/src/main/AndroidManifest.xml`, `titan2-platform/spaces/src/main/java/org/sableos/titan2/spaces/android/WorkOptionsActivity.kt` +24 more | 385/386, 1 replaced later |
| 0118 |  | 0118-zinwa-q25-no-cleartext-print-compass.patch | `titan2-platform/compass/src/main/AndroidManifest.xml`, `titan2-platform/printtogo/src/main/AndroidManifest.xml` | 2/2 |
| 0119 |  | 0119-zinwa-q25-titan2-detekt-defaults.patch | `titan2-platform/compass/src/main/java/org/sableos/titan2/compass/android/CompassActivity.kt`, `titan2-platform/compass/src/main/java/org/sableos/titan2/compass/android/DialView.kt` +9 more | 176/176 |
| 0120 |  | 0120-zinwa-q25-command-quick-prefixes.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/InstantActionsTest.kt` | 0/25 |
| 0121 |  | 0121-zinwa-q25-command-from-any-app.patch | (outside this repo: sable-src/src/android) | n/a |
| 0122 |  | 0122-zinwa-q25-setup-look-for-start.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/StartLookTest.kt` | 0/18, 11 replaced later |
| 0123 |  | 0123-zinwa-q25-start-launch-request-helper.patch | (outside this repo: sable-src/src/android) | n/a |
| 0124 |  | 0124-zinwa-q25-start-look-in-presentation-check.patch | `android/sablestart-presentation-check/build.gradle.kts` | 0/1 |
| 0125 |  | 0125-zinwa-q25-drop-plain-look.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/StartLookTest.kt` | 0/2 |
| 0126 |  | 0126-SableOS-folders-on-the-BB10-app-grid.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/GridFoldersTest.kt` | 0/49 |
| 0127 |  | 0127-SableOS-Peek-key-handling-in-helpers.patch | (outside this repo: sable-src/src/android) | n/a |
| 0128 |  | 0128-SableOS-folder-panel-grid-size-frames-and-hidden-apps.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/HomeOptionsTest.kt` | 0/35 |
| 0129 |  | 0129-SableOS-Priority-Hub-from-the-row-menu.patch | `android/hub/src/main/java/org/sableos/hub/HubModels.kt`, `android/hub/src/main/java/org/sableos/hub/HubPreferences.kt` +29 more | 241/258, 17 replaced later |
| 0130 |  | 0130-SableOS-Start-search-and-Peek-helpers-for-Sonar.patch | (outside this repo: sable-src/src/android) | n/a |
| 0131 |  | 0131-SableOS-Hub-Priority-Hub-store-for-detekt.patch | `android/hub/src/main/java/org/sableos/hub/HubPreferences.kt`, `android/hub/src/main/java/org/sableos/hub/HubPriorityPeople.kt` +2 more | 29/29 |
| 0132 |  | 0132-SableOS-Cosgrove-assistant-chat-hold-to-talk-and-Command-ask.patch | `android/assistant/build.gradle.kts`, `android/assistant/proguard-rules.pro` +55 more | 2181/2205, 14 replaced later |
| 0133 |  | 0133-SableOS-Command-ask-Enter-for-Sonar.patch | (outside this repo: sable-src/src/android) | n/a |
| 0134 |  | 0134-SableOS-Start-Android-bp-lists-AssistantPersona-StartLook-LookReceiver.patch | (outside this repo: sable-src/src/android) | n/a |
| 0135 |  | 0135-SableOS-Weather-morning-forecast-and-rain-alerts.patch | `android/weather/src/main/AndroidManifest.xml`, `android/weather/src/main/java/org/sableos/weather/MainActivity.kt` +25 more | 441/442, 1 replaced later |
| 0136 |  | 0136-SableOS-Start-Hub-swipe-to-settings-and-Device-Monitor-frame.patch | `android/sablestart-presentation-check/build.gradle.kts`, `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/Bb10HomeTest.kt` | 0/26 |
| 0137 |  | 0137-SableOS-Weather-refresh-job-without-the-extra-safe-call.patch | `android/weather/src/main/java/org/sableos/weather/WeatherRefreshJob.kt` | 1/1 |
| 0138 |  | 0138-SableOS-Sable-Phone-BB10-style-Phone-app.patch | `android/hub/src/main/java/org/sableos/hub/policy/HubIntents.kt`, `android/hub/src/test/java/org/sableos/hub/policy/HubCommsTest.kt` +50 more | 2128/2178, 43 replaced later |
| 0139 |  | 0139-SableOS-Phone-read-only-speed-dial-writes-and-timer-lambda-for-Sonar.patch | `titan2-platform/phone/src/main/AndroidManifest.xml`, `titan2-platform/phone/src/main/java/org/sableos/phone/android/InCallActivity.kt` | 7/7 |
| 0140 |  | 0140-SableOS-Phone-handle-refused-Telecom-calls-for-lint.patch | `titan2-platform/phone/src/main/java/org/sableos/phone/android/InCallActivity.kt`, `titan2-platform/phone/src/main/java/org/sableos/phone/android/PhoneActivity.kt` | 17/18, 1 replaced later |
| 0141 |  | 0141-SableOS-Phone-lint-and-ktlint-after-0140.patch | `titan2-platform/phone/src/main/java/org/sableos/phone/android/Dialing.kt`, `titan2-platform/phone/src/main/java/org/sableos/phone/android/InCallActivity.kt` +1 more | 10/10 |
| 0142 |  | 0142-SableOS-Phone-privileged-phone-state-for-lint.patch | `titan2-platform/phone/src/main/AndroidManifest.xml` | 5/5 |
| 0143 |  | 0143-zinwa-q25-2-0-black-and-white-and-key-fixes.patch | `reader/leisure/src/main/java/org/sableos/design/SableTheme.kt`, `android/browser/src/main/res/drawable/ic_sable_browser.xml` +14 more | 23/34, 1 replaced later |
| 0144 |  | 0144-zinwa-q25-sable-tools-ruler.patch | `titan2-platform/tools/src/main/java/org/sableos/tools/core/RulerScale.kt`, `titan2-platform/tools/src/main/java/org/sableos/tools/core/ToolCatalog.kt` +23 more | 157/159, 2 replaced later |
| 0145 |  | 0145-SableOS-Phone-clear-of-the-status-bar-and-an-on-screen-Menu.patch | `titan2-platform/phone/src/main/java/org/sableos/phone/android/InCallActivity.kt`, `titan2-platform/phone/src/main/java/org/sableos/phone/android/PhoneActivity.kt` +4 more | 26/27, 1 replaced later |
| 0146 |  | 0146-zinwa-q25-source-sans-3-and-ruler-ci-fix.patch | `reader/leisure/src/main/java/org/sableos/design/SableTheme.kt`, `titan2-platform/compass/src/main/java/org/sableos/titan2/compass/android/CompassActivity.kt` +15 more | 37/37 |
| 0147 |  | 0147-zinwa-q25-hub-rows-and-status-labels.patch | `android/sablestart-presentation-check/src/test/java/org/sableos/start/ui/StatusLabelLineTest.kt` | 0/9, 9 replaced later |
| 0148 |  | 0148-zinwa-q25-hub-row-divider-colour.patch | (outside this repo: sable-src/src/android) | n/a |
| 0149 |  | 0149-zinwa-q25-hub-views-panel-and-flat-hub-screens.patch | `reader/leisure/src/main/java/org/sableos/design/SableComponents.kt`, `android/config/detekt.yml` +4 more | 16/16 |
| 0150 |  | 0150-zinwa-q25-bb7-tray-black-and-white.patch | (outside this repo: sable-src/src/android) | n/a |
| 0151 |  | 0151-zinwa-q25-privacy-screen-in-text-reader.patch | (outside this repo: sable-src/apps/r8/textreader) | n/a |
| 0152 |  | 0152-zinwa-q25-phone-favorites-tab.patch | `titan2-platform/phone/src/main/java/org/sableos/phone/android/PhoneActivity.kt`, `titan2-platform/phone/src/main/java/org/sableos/phone/android/PhoneListAdapter.kt` +5 more | 137/137 |
| 0153 |  | 0153-zinwa-q25-flat-panels-in-the-shared-design-library.patch | (outside this repo: sable-src/src/android) | n/a |
| 0154 |  | 0154-zinwa-q25-action-bar-and-delete-with-undo.patch | `android/config/detekt.yml`, `android/design/src/test/java/org/sableos/design/SableActionBarLayoutTest.kt` +9 more | 69/69 |
| 0155 |  | 0155-zinwa-q25-hub-shows-every-app-like-bb10.patch | `android/hub/src/main/java/org/sableos/hub/ConnectedAppPolicy.kt`, `android/hub/src/main/java/org/sableos/hub/ConnectedAppsRepository.kt` +12 more | 86/86 |
| 0156 |  | 0156-zinwa-q25-bb7-command-folders-and-home-shortcuts.patch | (outside this repo: sable-src/apps/r8/textreader) | n/a |
| 0157 |  | 0157-zinwa-q25-hub-alerts-replaces-attention.patch | `android/hub/src/main/java/org/sableos/hub/HubText.kt` | 3/3 |
| 0158 |  | 0158-zinwa-q25-hub-coming-up-calendar-strip.patch | `android/calendar/src/main/java/org/sableos/calendar/CalendarSnapshotProvider.kt`, `android/hub/src/main/AndroidManifest.xml` +24 more | 71/87, 16 replaced later |
| 0159 |  | 0159-zinwa-q25-calendar-add-account-phone-calendar-ics.patch | `android/calendar/src/main/AndroidManifest.xml`, `android/calendar/src/main/java/org/sableos/calendar/CalendarImport.kt` +24 more | 376/376 |
| 0160 |  | 0160-zinwa-q25-calendar-action-bar-search-and-month-over-week.patch | `android/calendar/src/main/java/org/sableos/calendar/CalendarText.kt`, `android/calendar/src/main/java/org/sableos/calendar/EventSearch.kt` +21 more | 196/208, 12 replaced later |
| 0161 |  | 0161-zinwa-q25-calendar-colours-and-dimmed-past-events.patch | `android/calendar/src/main/java/org/sableos/calendar/CalendarColors.kt`, `android/calendar/src/main/java/org/sableos/calendar/CalendarText.kt` +22 more | 101/107, 6 replaced later |
| 0162 |  | 0162-zinwa-q25-space-t-b-scrolling-on-plain-screens.patch | `android/calendar/src/main/java/org/sableos/calendar/MainActivity.kt`, `android/design/src/test/java/org/sableos/design/SableListKeysTest.kt` +2 more | 12/12 |
| 0163 |  | 0163-zinwa-q25-sable-tools-privacy-check.patch | `titan2-platform/tools/src/main/java/org/sableos/tools/android/PrivacyCollector.kt`, `titan2-platform/tools/src/main/java/org/sableos/tools/core/PrivacyCheck.kt` +25 more | 628/630, 2 replaced later |
| 0164 |  | 0164-sable-tools-privacy-check-compile-fix.patch | `titan2-platform/tools/src/main/java/org/sableos/tools/android/PrivacyCollector.kt` | 3/3 |
| 0165 |  | 0165-fix-r8-ci-hub-start-text-calendar-compose-detekt.patch | `android/calendar/src/main/java/org/sableos/calendar/MainActivity.kt`, `android/config/detekt.yml` +3 more | 28/28 |
| 0166 |  | 0166-black-white-blue-media-hub-calendar-keyboard.patch | `android/calendar/src/main/java/org/sableos/calendar/MainActivity.kt`, `android/hub/src/main/java/org/sableos/hub/ui/HubScreen.kt` +6 more | 38/42, 3 replaced later |
| 0167 |  | 0167-games-black-white-blue.patch | `android/games/src/main/java/org/sableos/games/ArcadeScreens.kt`, `android/games/src/main/java/org/sableos/games/BoardScreens.kt` +2 more | 0/29 |
| 0168 |  | 0168-shared-theme-no-material-purple.patch | `reader/leisure/src/main/java/org/sableos/design/SableTheme.kt` | 42/42 |
| 0169 |  | 0169-icons-black-white-blue.patch | `reader/leisure/src/main/res/drawable/ic_sable_reader.xml`, `reader/leisure/src/main/res/drawable/ic_sable_reader_background.xml` +97 more | 132/190 |
| 0170 |  | 0170-keyboard-number-pad.patch | `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/SableImeService.kt`, `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/SoftKeyboardView.kt` | 27/27 |
| 0171 |  | 0171-hub-apps-first-calendar-undo-reader-scroll-keys.patch | `reader/leisure/src/main/java/org/sableos/design/SableComponents.kt`, `reader/leisure/src/main/java/org/sableos/design/SableScrollKeys.kt` +4 more | 70/70 |
| 0172 |  | 0172-home-style-flat-list-bb7-menu-my-sable.patch | `titan2-platform/tools/src/main/java/org/sableos/tools/core/SettingsLink.kt`, `titan2-platform/tools/src/main/java/org/sableos/tools/core/ToolCatalog.kt` +22 more | 153/154, 1 replaced later |
| 0173 |  | 0173-media-playlist-undo-my-sable-privacy-screen.patch | `android/media/src/main/java/org/sableos/media/MediaApp.kt`, `android/media/src/main/java/org/sableos/media/MediaAppControllers.kt` +24 more | 81/82, 1 replaced later |
| 0174 |  | 0174-media-station-undo-one-blue-glyphs-no-purple.patch | `reader/leisure/src/main/res/values/colors.xml`, `android/media/src/main/java/org/sableos/media/MediaApp.kt` +5 more | 50/50 |
| 0175 |  | 0175-my-sable-privacy-screen-switch.patch | `titan2-platform/tools/src/main/AndroidManifest.xml`, `titan2-platform/tools/src/main/java/org/sableos/tools/core/PrivilegePolicy.kt` +21 more | 61/61 |
| 0176 |  | 0176-my-sable-dual-apps-battery-health.patch | `titan2-platform/tools/src/main/java/org/sableos/tools/core/SettingsLink.kt`, `titan2-platform/tools/src/main/java/org/sableos/tools/core/ToolsText.kt` +20 more | 175/175 |
| 0177 |  | 0177-keyboard-fits-q25-square-screen.patch | `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/SoftKeyboardView.kt`, `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/core/CompactStrip.kt` +1 more | 19/19 |
| 0178 |  | 0178-cosgrove-assistant-role-and-confirmed-texts.patch | `android/assistant/src/main/AndroidManifest.xml`, `android/assistant/src/main/java/org/sableos/assistant/MainActivity.kt` +27 more | 465/466, 1 replaced later |
| 0179 |  | 0179-quick-settings-one-column-list.patch | (outside this repo: patches/README.md, sable-src/src/android) | n/a |
| 0180 |  | 0180-calendar-rings-by-calendar-day-view-phone-photos.patch | `android/calendar/src/main/java/org/sableos/calendar/BusyMonth.kt`, `android/calendar/src/main/java/org/sableos/calendar/CalendarBusy.kt` +29 more | 182/195, 13 replaced later |
| 0181 |  | 0181-calendar-account-apps-mail-microsoft-signin.patch | `android/assistant/src/main/java/org/sableos/assistant/platform/PhoneActions.kt` | 4/4 |
| 0182 |  | 0182-hub-email-from-sable-mail.patch | (outside this repo: patches/README.md, sable-src/apps/r8/mail) | n/a |
| 0183 |  | 0183-sable-colours-in-upstream-apps.patch | (outside this repo: patches/README.md, sable-src/apps/r8/connectbot) | n/a |
| 0184 |  | 0184-shared-flavor-helpers.patch | (outside this repo: patches/README.md, sable-src/apps/r8/connectbot) | n/a |
| 0185 |  | 0185-davx5-translations-submodule.patch | (outside this repo: patches/README.md, sable-src/apps/r8/davx5) | n/a |
| 0186 |  | 0186-hub-mark-read-reaches-sable-mail.patch | (outside this repo: patches/README.md, sable-src/apps/r8/mail) | n/a |
| 0187 |  | 0187-browser-standalone-user-agent.patch | `android/browser/src/main/java/org/sableos/browser/ui/BrowserWeb.kt`, `android/browser/src/test/java/org/sableos/browser/UserAgentTest.kt` | 12/12 |
| 0188 |  | 0188-home-back-at-app-root-shows-active-frames.patch | (outside this repo: patches/README.md, sable-src/apps/r8/tenseven) | n/a |
| 0189 |  | 0189-home-hub-over-apps-and-frame-covers.patch | (outside this repo: patches/README.md, sable-src/apps/r8/tenseven) | n/a |
| 0190 |  | 0190-home-hub-keeps-items-and-status-icons.patch | (outside this repo: patches/README.md, sable-src/apps/r8/tenseven) | n/a |
| 0191 |  | 0191-home-needs-no-permission-setup.patch | (outside this repo: patches/README.md, sable-src/apps/r8/tenseven) | n/a |
| 0192 |  | 0192-home-live-frames-like-sable-1.patch | (outside this repo: patches/README.md, sable-src/apps/r8/tenseven) | n/a |
| 0193 |  | 0193-app-compatibility-redesign.patch | `titan2-platform/displaycompat/src/main/AndroidManifest.xml`, `titan2-platform/displaycompat/src/main/java/org/sableos/titan2/displaycompat/android/BootReceiver.kt` +34 more | 1169/1173, 4 replaced later |
| 0194 |  | 0194-app-compatibility-older-code-loading.patch | `titan2-platform/displaycompat/src/main/java/org/sableos/titan2/displaycompat/android/PresetSupport.kt`, `titan2-platform/displaycompat/src/main/java/org/sableos/titan2/displaycompat/android/SysProps.kt` +23 more | 143/143 |
| 0195 |  | 0195-app-compatibility-presets-tubi-google-tv-yt-music.patch | `titan2-platform/displaycompat/src/main/res/raw/app_compat_presets.tsv`, `titan2-platform/displaycompat/src/test/java/org/sableos/titan2/displaycompat/core/AppCompatTest.kt` | 8/8 |
| 0196 |  | 0196-home-no-permissions-page.patch | (outside this repo: patches/README.md, sable-src/apps/r8/tenseven) | n/a |
| 0197 |  | 0197-home-frames-keep-the-snapshot-buffer.patch | (outside this repo: patches/README.md, sable-src/apps/r8/tenseven) | n/a |
| 0198 |  | 0198-privacy-screen-band-a-third-of-the-screen.patch | (outside this repo: patches/README.md, sable-src/apps/r8/textreader) | n/a |
| 0199 |  | 0199-home-hub-and-command-fit-the-q25.patch | (outside this repo: patches/README.md, sable-src/apps/r8/tenseven) | n/a |
| 0200 |  | 0200-calendar-tenseven-layout.patch | `android/calendar/src/main/java/org/sableos/calendar/CalendarChrome.kt`, `android/calendar/src/main/java/org/sableos/calendar/CalendarText.kt` +21 more | 323/326, 3 replaced later |
| 0201 |  | 0201-sable-apps-between-the-bars-and-calendar-week-list.patch | `android/calendar/src/main/java/org/sableos/calendar/BusyMonth.kt`, `android/calendar/src/main/java/org/sableos/calendar/CalendarChrome.kt` +22 more | 135/135 |
| 0202 |  | 0202-privacy-screen-darkness-setting.patch | (outside this repo: sable-src/apps/r8/textreader) | n/a |
| 0203 |  | 0203-quiet-lock-layer-without-notifications.patch | (outside this repo: sable-src/apps/r8/tenseven) | n/a |
| 0204 |  | 0204-q25-type-audit-every-role-sable.patch | `android/weather/src/main/java/org/sableos/weather/MainActivity.kt` | 5/5 |
| 0205 |  | 0205-browser-pages-follow-the-sable-theme.patch | `android/browser/src/main/java/org/sableos/browser/MainActivity.kt`, `android/browser/src/main/java/org/sableos/browser/ui/BrowserScreen.kt` +1 more | 18/18 |
| 0206 |  | 0206-media-hub-list-on-the-q25-with-pictures.patch | `android/config/detekt.yml`, `android/media/src/main/java/org/sableos/media/FmController.kt` +35 more | 1387/1387 |
| 0207 |  | 0207-sable-games-console-lua-games-on-the-q25.patch | `android/games/build.gradle.kts`, `android/games/proguard-rules.pro` +56 more | 1/3027, 52 replaced later |
| 0208 |  | 0208-games-console-back-reaches-the-game.patch | `android/games/src/console/java/org/sableos/games/console/ConsoleApp.kt` | 0/10 |
| 0209 |  | 0209-tilt-racer-cars-with-wheels-glass-and-lights.patch | `android/games/src/console/assets/console/tilt_racer/main.lua` | 0/18, 16 replaced later |
| 0210 |  | 0210-games-console-string-format-keeps-decimals.patch | `android/games/src/console/java/org/sableos/games/console/LuaGame.kt`, `android/games/src/testConsole/java/org/sableos/games/console/LuaGameTest.kt` | 0/29, 1 replaced later |
| 0211 |  | 0211-games-console-pictures-and-f1-cars-in-tilt-racer.patch | `android/games/src/console/assets/console/tilt_racer/f1_blue.png`, `android/games/src/console/assets/console/tilt_racer/f1_green.png` +12 more | 0/155, 5 replaced later |
| 0212 |  | 0212-games-console-tilt-circuit-and-turned-pictures.patch | `android/games/src/console/assets/console/tilt_circuit/f1_blue.png`, `android/games/src/console/assets/console/tilt_circuit/f1_green.png` +31 more | 0/262, 38 replaced later |
| 0213 |  | 0213-games-console-ball-maze.patch | `android/games/src/console/assets/console/ball_maze/game.toml`, `android/games/src/console/assets/console/ball_maze/main.lua` +5 more | 0/399, 4 replaced later |
| 0214 |  | 0214-games-console-shapes-sky-roll-and-ramp-rush.patch | `android/games/src/console/assets/console/ramp_rush/arch.png`, `android/games/src/console/assets/console/ramp_rush/boulder.png` +37 more | 0/687, 7 replaced later |
| 0215 |  | 0215-games-console-portrait-calmer-tilt-easy-circuit.patch | `android/games/src/console/AndroidManifest.xml`, `android/games/src/console/assets/console/tilt_circuit/easy_0_0.png` +9 more | 0/153 |
| 0216 |  | 0216-games-console-sandbox-hardening.patch | `android/games/src/console/java/org/luaj/vm2/lib/MeteredPatterns.java`, `android/games/src/console/java/org/sableos/games/console/ConsolePlay.kt` +2 more | 0/253 |
| 0217 |  | 0217-extra-dim-off-by-default-with-pwm-backlight.patch | (outside this repo: sable-src/apps/r8/tenseven) | n/a |
| 0218 |  | 0218-key-mapper.patch | (outside this repo: sable-src/apps/r8/keymapper, sable-src/apps/r8/sable_flavor_common.py) | n/a |
| 0219 |  | 0219-sable-indian-games.patch | `android/games/build.gradle.kts`, `android/games/src/indiangames/res/drawable/ic_sable_game.xml` +19 more | 0/2512 |
| 0220 |  | 0220-extra-dim-on-again-without-pwm-backlight.patch | (outside this repo: sable-src/apps/r8/tenseven) | n/a |
| 0221 |  | 0221-games-moved-to-sablegames.patch | `android/games/build.gradle.kts`, `android/games/gradle.lockfile` +245 more | n/a |
| 0222 |  | 0222-sable-watch-reads-hub-calendar-weather.patch | `android/calendar/src/main/java/org/sableos/calendar/CalendarSnapshotProvider.kt`, `android/design/src/test/java/org/sableos/design/SableWatchCallerTest.kt` +2 more | 37/37 |
| 0223 |  | 0223-sable-phone-answers-the-call-button.patch | `titan2-platform/phone/src/main/AndroidManifest.xml`, `titan2-platform/phone/src/main/java/org/sableos/phone/android/PhoneActivity.kt` | 6/6 |
| 0224 |  | 0224-sable-phone-call-screen-and-dial-pad-fit.patch | `titan2-platform/phone/src/main/java/org/sableos/phone/android/CallBook.kt`, `titan2-platform/phone/src/main/java/org/sableos/phone/android/DialPadView.kt` +5 more | 119/119 |
| 0225 |  | 0225-sable-phone-answers-to-the-stock-incall-service-name.patch | `titan2-platform/phone/src/main/AndroidManifest.xml`, `titan2-platform/phone/src/main/java/com/android/incallui/InCallServiceImpl.kt` +1 more | 32/33, 1 replaced later |
| 0226 |  | 0226-sable-phone-rings-itself.patch | `titan2-platform/phone/src/main/AndroidManifest.xml`, `titan2-platform/phone/src/main/java/org/sableos/phone/android/PhoneInCallService.kt` +1 more | 70/70 |
| 0227 | 18 Nov 2026 | cosgrove: keep the API path (/v1) of a Custom server address | `android/assistant/src/main/java/org/sableos/assistant/core/Provider.kt`, `android/assistant/src/main/java/org/sableos/assistant/core/ServerAddress.kt` +1 more | 33/33 |
| 0228 | 18 Nov 2026 | sable: remove the home status dials and status line, point Setup at Tools | `android/sablestart-presentation-check/src/test/java/org/sableos/start/model/HomeLayoutTest.kt`, `android/sablestart-presentation-check/src/test/java/org/sableos/start/ui/StatusLabelLineTest.kt` +2 more | 2/5 |
| 0229 | 18 Nov 2026 | sable: Sable Setup leads with Profile, Theme and Sable apps rows | `titan2-platform/setup/src/main/AndroidManifest.xml`, `titan2-platform/setup/src/main/java/org/sableos/titan2/setup/android/Launcher.kt` +24 more | 296/316 |
| 0230 | 19 Nov 2026 | sable: log why Sable Keyboard shows or hides word suggestions | `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/CompactStripView.kt`, `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/SuggestionController.kt` | 14/14 |
| 0231 | 19 Nov 2026 | sable: the word-suggestion strip sits under the field, not over it | `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/SableImeService.kt`, `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/StripInsets.kt` | 9/9 |
| 0232 | 19 Nov 2026 | sable: keyboard suggestion chips follow the profile | `titan2-platform/keyboard/src/main/java/org/sableos/titan2/keyboard/android/CandidateBar.kt` | 16/16 |

## 2. Flavor patches: each became ordinary source in its app folder

### apps/launcher

| Patch function | First added | Files touched | Edits | Why (from the script) |
|---|---|---|---|---|
| `patch_build` | 2026-11-18 | `app/build.gradle.kts` | 2 |  |
| `patch_manifest` | 2026-11-18 | `SableLookReceiver.kt`, `app/src/main/AndroidManifest.xml`, `mipmap-anydpi-v26/ic_sable_home.xml` +1 | 4 |  |
| `patch_command` | 2026-11-18 | `SableStartActivity.kt` | 4 |  |
| `patch_colours` | 2026-11-18 | `SableSystemTokens.kt`, `SableTheme.kt`, `Settings2VisualTheme.kt` +3 | 6 | The approved BB10 tokens: blue #2E9BD6, black, raised #141414, dividers #262626, muted #A6A6A6. |
| `patch_frames` | 2026-11-18 | `app/src/main/AndroidManifest.xml`, `frames/FrameModels.kt`, `platform/ActiveFramesHost.kt` +6 | 10 | Active Frames on SableOS: the system task snapshot, never screen capture (owner, 2026-11-12). |
| `patch_system_menu` | 2026-11-18 | `SableSystemMenuActivity.kt`, `app/src/main/AndroidManifest.xml`, `src/SableSystemMenuActivity.kt` | 2 | The BlackBerry 7 style system menu on Alt+BlackBerry key (frameworks/base 0324; owner, |
| `patch_fit` | 2026-11-18 | `ui/WidgetsPage.kt` | 2 | Fit the Q25's 411 dp square screen (about 383 dp under the status bar): the widget picker's |
| `strip_manifest` | 2026-11-18 | `app/src/main/AndroidManifest.xml` | 0 |  |
| `strip_extras` | 2026-11-18 | `LauncherSettingsActivity.kt`, `frames/FrameWindow.kt`, `model/ExtraApps.kt` +6 | 19 |  |
| `strip_phone` | 2026-11-18 | `LauncherSettingsActivity.kt`, `OnboardingActivity.kt` | 2 |  |
| `strip_screen_capture` | 2026-11-18 | `SableStartActivity.kt`, `ui/ActiveFramesPage.kt`, `ui/FrameSettingsSection.kt` | 6 |  |
| `strip_first_run` | 2026-11-18 | `SableStartActivity.kt` | 1 | SableOS Setup already chose the home, its look and notification access; TenSeven's own setup |
| `patch_leftovers` | 2026-11-18 | - | 0 |  |
| `patch_hub_mail` | 2026-11-18 | `app/src/main/AndroidManifest.xml`, `conversations/ConversationHub.kt`, `conversations/SableMailFeed.kt` +3 | 5 | Hub shows Sable Mail's inboxes from its feed (org.sableos.mail.hub), not its notifications. |
| `patch_back_to_frames` | 2026-11-18 | `SableStartActivity.kt`, `frames/SableBackToFrames.kt`, `frames/SableBackToFramesTest.kt` +2 | 3 | Back at an app's root returns to Active Frames, as Home does (2.0 plan, BB10). |
| `patch_hub_peek` | 2026-11-18 | `SableHubPeekActivity.kt`, `app/src/main/AndroidManifest.xml`, `conversations/SableHubPeek.kt` +4 | 1 | Hub over any app (2.0 plan, BB10): a panel over the app in front, from holding the Recents key |
| `patch_frame_covers` | 2026-11-18 | `AppSummary.kt`, `SableCoverProvider.kt`, `SableFrameCovers.kt` +7 | 4 | The cover API (2.0 plan, BB10 Active Frame covers): apps say what their own frame shows through |
| `patch_no_status_dials` | 2026-11-18 | `LauncherSettingsActivity.kt`, `app/src/androidTest/java/io/github/aimindseye/tenseven/ui/ActiveFramesUiTest.kt`, `model/HomeLayout.kt` +3 | 14 | No status dials and no status line on either home (owner, 2026-11-18). The BB10 home keeps only the |
| `patch_extra_dim` | 2026-11-18 | `LauncherSettingsActivity.kt`, `SableStartActivity.kt`, `app/src/main/AndroidManifest.xml` +4 | 3 | Dimmer than the panel's lowest step (owner, 2026-11-12): with the brightness slider at the bottom the |
| `patch_home_access` | 2026-11-18 | `LauncherSettingsActivity.kt`, `OnboardingActivity.kt`, `SableStartActivity.kt` +7 | 13 | The home is part of SableOS, so it needs no permission setup (owner, 2026-11-12): SableHomeAccess |
| `patch_hub_keep` | 2026-11-18 | `ConversationHub.kt`, `ConversationListenerService.kt`, `ConversationState.kt` +11 | 20 | Hub after the JL10 review (owner, 2026-11-12): items kept until deleted, Unread and date sections, |
| `patch_q25_fit` | 2026-11-18 | `SableTheme.kt`, `ui/CommandScreen.kt`, `ui/HubCalendarPeek.kt` +3 | 12 | Fits the Hub, Command and the home bar to the Q25's 411 dp square screen (owner, 2026-11-12). |
| `patch_quiet_lock` | 2026-11-18 | `ui/LockNotificationScreen.kt` | 1 | With no notifications the home's lock layer is just the clock and date (owner, 2026-11-13). |
| `patch_recent_tab` | 2026-11-18 | `model/Bb7Home.kt`, `model/Bb7HomeTest.kt`, `model/SableRecents.kt` +4 | 9 | The BlackBerry 7 tray gets a Recent panel after Favorites (owner, 2026-11-16): the apps opened last, |
| `patch_profiles_shortcut` | 2026-11-18 | `ui/Bb10HomeChrome.kt`, `ui/Bb7HomeScreen.kt` | 2 | The sound profile shortcut is on both homes (owner, 2026-11-16): the speaker icon in the BB7 banner |
| `patch_profile_setting` | 2026-11-18 | `platform/StartStateRepository.kt` | 3 | The profile (BlackBerry 10 or 7) is a whole-OS setting, not the launcher's own preference: Sable |
| `patch_hub_default_action` | 2026-11-18 | `ui/HubPanels.kt` | 5 | Hub rows follow the BlackBerry rule that Enter runs the default action and Menu shows the rest |
| `patch_hub_reply_selector` | 2026-11-18 | `conversations/ConversationModels.kt`, `conversations/SableReplyChoice.kt`, `conversations/SableReplyChoiceTest.kt` +3 | 4 | A merged Hub conversation (one person across SMS, chat and email, which the pinned upstream already |
| `patch_bb7_highlight` | 2026-11-18 | `SableResponsiveComponents.kt` | 1 | BlackBerry 7 marks the focused item with a filled highlight, BlackBerry 10 with an outline |
| `patch_hub_inline_reply` | 2026-11-19 | `ui/HubPanels.kt` | 3 | The Hub's reply field is inline: opening a message's menu shows the reply field at once, with |
| `patch_home_splat` | 2026-11-19 | `ui/HomeAppGrid.kt` | 4 | Splat indicator on app icons (conformance matrix, owner decision 2026-11-18): an app with |
| `patch_hub_bottom_tabs` | 2026-11-19 | `ui/HubPage.kt` | 3 | BlackBerry 10 puts the Hub's tabs (All, Unread, Messages, ...) in the bottom action area, just |
| `patch_hub_swipe_menu` | 2026-11-19 | `ui/HubPanels.kt` | 2 | BlackBerry 10 opens an app's menu with a swipe down from the top (conformance matrix, owner |
| `patch_visible_names` | 2026-11-18 | - | 0 | Sable Launcher is the only name the user sees. The upstream project name stays in package ids, |
| `patch_peek_motion` | 2026-11-19 | `ui/HubPeek.kt` | 2 | The Hub peek follows the whole-OS profile's motion set (conformance matrix, BlackBerry 7.1 |
| `patch_hub_row_motion` | 2026-11-19 | - | 3 | Hub conversation rows follow the profile's motion set. A row that leaves the list (deleted, |
| `patch_bb7_full_menu` | 2026-11-19 | `ui/Bb7HomeScreen.kt` | 1 | BBOS 7 Menu key = one full menu (owner decision 2026-11-19, from Bold7): Switch Application first (it |
| `patch_bb7_short_menu` | 2026-11-19 | `SableStartActivity.kt`, `src/SableShortMenu.kt`, `src/SableShortMenuTest.kt` +3 | 3 | BBOS 7 short menu (owner decision 2026-11-19, from Bold7): a trackpad hold on the home screen opens a |
| `patch_home_menu` | 2026-11-19 | `ui/Bb10HomeChrome.kt`, `ui/Bb7HomeScreen.kt` | 4 | One Home menu (owner decision 2026-11-19, matrix: BBOS 7 and BBOS 10 homes). The BBOS 7 long-press menu |
| `patch_home_size` | 2026-11-19 | `model/LauncherSettings.kt`, `model/LauncherSettingsTest.kt`, `ui/Bb7HomeScreen.kt` | 3 | Larger icons and names on both homes (owner decision 2026-11-19, from the Bold7 and JL10 screens). |
| `patch_bb7_dial` | 2026-11-19 | `src/SableDial.kt`, `src/SableDialTest.kt`, `ui/Bb7HomeScreen.kt` +2 | 3 | BBOS 7 profile (owner decision 2026-11-19, from Bold7): typing a number of three or more digits on the |
| `main` | 2026-11-18 | - | 0 |  |

### apps/mail

| Patch function | First added | Files touched | Edits | Why (from the script) |
|---|---|---|---|---|
| `copy_icon_resources` | 2026-11-12 | `ic_sable_mail.xml` | 0 | Copy the generated launcher icon (sable-src/tools/gen_first_party_icons.py, row mail) into the app. |
| `apply_keyboard_model` | 2026-11-12 | `core/preference/api/src/commonMain/kotlin/net/thunderbird/core/preference/interaction/InteractionSettings.kt`, `drawable/sable_mail_list_item_foreground.xml`, `drawable/sable_mail_list_item_foreground_bb7.xml` +7 | 15 | Sable Mail keyboard model (MAIL_CALENDAR_PRODUCTIVITY_UX.md, shared keyboard contract). |
| `apply_one_screen_setup` | 2026-11-19 | `AccountSetupNavHost.kt`, `SableQuickOptions.kt`, `src/setup/SableQuickOptions.kt` | 4 | Account setup is one screen: after the server checks the Display options and Sync options steps fill |
| `main` | 2026-11-12 | `AccountAutoDiscoveryContentPreview.kt`, `AccountAutoDiscoveryScreenPreview.kt`, `AppCommonFeatureModule.kt` +33 | 65 |  |

### apps/connectbot

| Patch function | First added | Files touched | Edits | Why (from the script) |
|---|---|---|---|---|
| `patch_settings` | 2026-11-12 | `settings.gradle.kts` | 1 |  |
| `patch_build` | 2026-11-12 | - | 6 | The :translations module drives a Python/uv translation tool; the shipped strings are already in res/. |
| `patch_manifest` | 2026-11-12 | - | 3 |  |
| `patch_console` | 2026-11-12 | - | 1 |  |
| `patch_colours` | 2026-11-12 | - | 1 | The Sable colours, dark and light, in place of ConnectBot's and of Android's wallpaper colours. |
| `main` | 2026-11-12 | `build.gradle.kts`, `mipmap-anydpi-v26/ic_sable_terminal.xml`, `src/main/AndroidManifest.xml` +3 | 1 |  |

### apps/espeak

| Patch function | First added | Files touched | Edits | Why (from the script) |
|---|---|---|---|---|
| `main` | 2026-11-05 | `AndroidManifest.xml`, `build.gradle` | 2 |  |

### apps/keymapper

| Patch function | First added | Files touched | Edits | Why (from the script) |
|---|---|---|---|---|
| `main` | 2026-11-13 | `app/build.gradle.kts`, `settings.gradle.kts` | 2 |  |

### apps/textreader

| Patch function | First added | Files touched | Edits | Why (from the script) |
|---|---|---|---|---|
| `main` | 2026-11-06 | `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `app/src/main/java/org/vaachak/textreader/MainActivity.kt` +2 | 1 |  |
| `patch_build` | 2026-11-06 | - | 4 |  |
| `patch_manifest` | 2026-11-06 | - | 0 |  |
| `patch_main_activity` | 2026-11-06 | - | 0 |  |
| `write_shared_components` | 2026-11-06 | - | 0 |  |
| `write_text_reader_util` | 2026-11-06 | `app/src/main/java/org/vaachak/textreader/TextReaderUtil.kt` | 0 |  |
| `patch_tts_settings` | 2026-11-06 | `app/src/main/java/org/vaachak/textreader/TtsSettingsView.kt` | 0 |  |
| `patch_tests` | 2026-11-06 | `app/src/androidTest/java/org/vaachak/textreader/MainScreenTest.kt`, `app/src/test/java/org/vaachak/textreader/TextReaderUtilTest.kt` | 0 |  |
| `copy_design_sources` | 2026-11-06 | `SableCornerStyle.kt`, `SableGlobalAppearance.kt`, `SableResponsive.kt` +2 | 0 |  |
| `copy_launcher_icons` | 2026-11-06 | `mipmap-anydpi-v26/ic_sable_text_reader.xml` | 0 |  |
| `copy_speak_focus` | 2026-11-06 | - | 0 |  |

### apps/davx5

| Patch function | First added | Files touched | Edits | Why (from the script) |
|---|---|---|---|---|
| `apply_colours` | 2026-11-12 | `core/src/main/kotlin/at/bitfire/davdroid/ui/OseTheme.kt` | 0 | DAVx5's green and orange become the Sable colours; its screens otherwise stay upstream's. |
| `main` | 2026-11-12 | `app-ose/build.gradle.kts` | 0 |  |

### apps/icsx5

| Patch function | First added | Files touched | Edits | Why (from the script) |
|---|---|---|---|---|
| `apply_colours` | 2026-11-12 | `app/src/main/java/at/bitfire/icsdroid/ui/theme/Theme.kt` | 0 | ICSx5's blues become the Sable colours, dark and light; its screens otherwise stay upstream's. |
| `main` | 2026-11-12 | `app/build.gradle.kts` | 0 |  |

## 3. Framework patches (stay in zinwaq25-sableos)

| Project | Patches |
|---|---|
| `device/xelex/Q25` | 2 |
| `device/xelex/mt6789-common` | 3 |
| `frameworks/base` | 43 |
| `kernel/xelex/mt6789` | 10 |
| `lineage-sdk` | 3 |
| `packages/apps/Aperture` | 1 |
| `packages/apps/Contacts` | 7 |
| `packages/apps/Dialer` | 11 |
| `packages/apps/DocumentsUI` | 4 |
| `packages/apps/FMRadio` | 2 |
| `packages/apps/Glimpse` | 5 |
| `packages/apps/Jelly` | 4 |
| `packages/apps/Launcher3` | 3 |
| `packages/apps/LineageParts` | 3 |
| `packages/apps/Settings` | 49 |
| `packages/apps/SetupWizard` | 31 |
| `packages/overlays/Lineage` | 1 |

Per patch: `patches/framework/<project>/NNNN-*.patch` in zinwaq25-sableos; each file starts with a commit message saying what and why.

<details><summary>device/xelex/Q25 (2)</summary>

- 1501 SableOS: type $ on the Q25 currency key
- 1502 SableOS: Space, T and B scroll in any app on the Q25

</details>

<details><summary>device/xelex/mt6789-common (3)</summary>

- 1502 SableOS: let Settings drive the Q25 keyboard firmware switch
- 1503 SableOS: let init set the Q25 trackpad mode
- 1504 SableOS: let init set the Q25 double-tap to wake

</details>

<details><summary>frameworks/base (43)</summary>

- 0101 0101-SableOS-read-default-large-Quick-Settings-tiles-from.patch
- 0102 0102-SableOS-Quick-Settings-tiles-use-the-Sable-card-and-.patch
- 0103 SableOS: apply the pure black theme at boot and after setup
- 0301 SableOS: keyboard notification commands in the shade
- 0302 SableOS: Alt shade shortcuts for keyboards without Fn
- 0303 SableOS: Space toggles and Alt+Enter opens details on Quick Settings tiles
- 0304 SableOS: keyboard hint line and type-ahead on the lock screen
- 0305 SableOS: Sable lock screen clock and unlock text
- 0306 SableOS: keep the lock screen clock on the current time and zone
- 0307 SableOS: Sable locked glance, emergency shortcut and bouncer keyboard cues
- 0310 SableOS: three-column labeled Quick Settings grid, four-tile compact row
- 0311 SableOS: Sable Quick Settings header, Hub summary row and key hint
- 0312 SableOS: "/" searches the notifications in the shade
- 0313 SableOS: Alt+Shift+R cycles the Tall canvas of the app in front
- 0314 SableOS: lock screen Peek, one row per app
- 0315 SableOS: compact PIN pad and Lock screen size
- 0316 SableOS: compact Settings rows on short screens
- 0317 SableOS: next calendar event on the lock screen
- 0318 SableOS: lock screen Peek as an app column beside the newest notification
- 0319 SableOS: Alt+Space opens Sable Command from any app
- 0320 SableOS: Picture Password as a quick second unlock
- 0321 SableOS: built-in Picture Password pictures
- 0322 SableOS: ticker for new notifications on the lock screen
- 0323 SableOS: lock screen shows nothing extra when there are no notifications
- 0324 SableOS: Alt+BlackBerry key opens the system menu
- 0325 SableOS: optional wipe after 10 failed unlock tries
- 0326 SableOS: hold the Recents key for the Hub over any app
- 0327 SableOS: PIN pad leaves room for the navigation bar
- 0328 SableOS: older code loading for listed apps (Pluto TV)
- 0329 SableOS: Alt+Shift+P switches the Privacy Screen from any app
- 0330 SableOS: compact PIN pad reaches the MotionLayout it lives in
- 0331 SableOS: PIN pad keys keep their compact height
- 0332 SableOS: no keyboard fallback line on the PIN and password bouncer
- 0333 SableOS: PIN pad fits its real height, so the keys stay off the PIN field
- 0501 SableOS: notification light for every alerting channel
- 0502 SableOS: short toasts stay up for 3 seconds
- 0503 SableOS: screen transitions follow the BlackBerry profile
- 0505 SableOS: hold Menu opens Active Frames from any app
- 0506 SableOS: End goes home and Call opens Phone
- 0507 SableOS: dialogs follow the BlackBerry profile
- 0508 SableOS: BlackBerry 7 shade banner opens Manage Connections
- 0509 SableOS: BlackBerry 7 shade counters and no compact tile row
- 0550 SableOS: microG signature spoofing on user builds when the product allows it

</details>

<details><summary>kernel/xelex/mt6789 (10)</summary>

- 0201 SableOS: report the fuel gauge's learned battery health on the Q25
- 0202 SableOS: stop the Q25 keyboard driver logging every key and trackpad move
- 0901 SableOS: let the system set the Q25 keyboard backlight
- 0902 SableOS: no phantom Q25 keys after the screen wakes
- 0903 SableOS: double-tap to wake on the Q25 touch panel
- 0904 SableOS: no hiss on the Q25 headphone jack
- 0905 SableOS: no hidden num lock on the Q25 keyboard
- 0906 SableOS: dim the Q25 with the PWM backlight, as the stock brightness dtbo does
- 0907 SableOS: double-tap to wake every time on the Q25
- 0908 SableOS: catch a double tap that woke the Q25 from suspend

</details>

<details><summary>lineage-sdk (3)</summary>

- 0150 SableOS: pure black dark theme by default
- 0502 SableOS: default notification light color unless the user picks auto
- 0504 SableOS: battery light blinks while charging, solid green only at 100 %

</details>

<details><summary>packages/apps/Aperture (1)</summary>

- 0801 SableOS: let Sable Camera be the default still camera

</details>

<details><summary>packages/apps/Contacts (7)</summary>

- 0751 0751-SableOS-pure-contact-list-letter-and-number-model-fo.patch
- 0752 0752-SableOS-Contacts-list-with-A-Z-rail-letter-keys-and-.patch
- 0753 SableOS: pure route subtitle and privacy model for Contacts
- 0754 SableOS: route subtitle, Hub route chip and privacy line in Contacts
- 0755 SableOS: Sable dark palette, focus outline and type scale for Contacts
- 0756 SableOS: Contacts follows the system Dark theme
- 0757 SableOS: Contacts follows the BlackBerry profile (flat filled focus bar on BlackBerry 7)

</details>

<details><summary>packages/apps/Dialer (11)</summary>

- 0701 0701-SableOS-pure-contact-list-letter-and-number-model-fo.patch
- 0702 0702-SableOS-Phone-contacts-list-with-A-Z-rail-letter-key.patch
- 0703 0703-SableOS-single-key-mute-speaker-and-dialpad-on-the-in.patch
- 0704 SableOS: Space answers a ringing call on the in-call screen
- 0705 SableOS: pure dial-screen key and Recents privacy model for Phone
- 0706 SableOS: hardware digits on the dial screen and Recents privacy
- 0707 SableOS: Sable dark palette and focus outline for Phone
- 0708 SableOS: Sable header, Recents rows, dial pad keys and in-call pills
- 0709 SableOS: Phone follows the system Dark theme
- 0710 SableOS: speed dial on letter keys
- 0711 SableOS: Phone follows the BlackBerry profile (flat filled focus bar on BlackBerry 7)

</details>

<details><summary>packages/apps/DocumentsUI (4)</summary>

- 1001 1001-SableOS-pure-Files-keyboard-model.patch
- 1002 1002-SableOS-Files-keys-for-copy-move-rename-new-folder-a.patch
- 1003 1003-SableOS-Files-single-letter-keys-per-the-design.patch
- 1004 1004-SableOS-Files-letter-keys-and-the-hint-dock.patch

</details>

<details><summary>packages/apps/FMRadio (2)</summary>

- 1061 1061-SableOS-pure-FM-bridge-protocol-for-Sable-Media.patch
- 1062 1062-SableOS-Sable-Media-drives-FM-through-a-bridge-servi.patch

</details>

<details><summary>packages/apps/Glimpse (5)</summary>

- 0801 SableOS: Sable keyboard focus ring on gallery thumbnails
- 1021 1021-SableOS-pure-Photos-keyboard-model-and-album-type-to.patch
- 1022 1022-SableOS-Photos-keys-in-the-viewer-the-grid-and-the-a.patch
- 1023 1023-SableOS-Photos-keys-follow-the-design-s-S-add-Z-grid.patch
- 1024 1024-SableOS-Photos-caption-strips-grid-J-K-and-Z-zoom-mo.patch

</details>

<details><summary>packages/apps/Jelly (4)</summary>

- 1041 1041-SableOS-pure-Browser-keyboard-model.patch
- 1042 1042-SableOS-Browser-keys-for-the-address-bar-tabs-find-a.patch
- 1043 1043-SableOS-Browser-keys-follow-the-design-s-T-B-L-and-a.patch
- 1044 1044-SableOS-Browser-start-page-command-dock-reader-mode-.patch

</details>

<details><summary>packages/apps/Launcher3 (3)</summary>

- 0001 SableOS: keep Launcher3QuickStep for Recents only, not HOME
- 0002 SableOS: hide the Launcher3QuickStep home settings, label it Recents
- 0003 SableOS: Recents key and button open Sable Start's Active Frames

</details>

<details><summary>packages/apps/LineageParts (3)</summary>

- 0503 SableOS: show Automagic notification light color off by default
- 0504 SableOS: keep the battery light on when fully charged by default
- 0601 SableOS: no LineageOS contributors, statistics or Trust branding

</details>

<details><summary>packages/apps/Settings (49)</summary>

- 0101 0101-SableOS-Sable-appearance-authority-and-Display-Accen.patch
- 0102 0102-SableOS-SableOS-version-row-in-About-phone.patch
- 0103 SableOS: Sable app corner style in the appearance authority
- 0104 SableOS: seed the system palette from the Sable accent once
- 0201 0201-SableOS-battery-health-and-usage-model-with-host-tes.patch
- 0202 0202-SableOS-Battery-health-and-Charging-protection-in-Se.patch
- 0203 0203-SableOS-keyboard-inspection-for-the-battery-usage-ch.patch
- 0300 SableOS: Sable Attention and per-app Sable Hub entries
- 0601 SableOS: Applications privacy, Network Manager and App Security model
- 0602 SableOS: sensitive access under app names and the Apps app count
- 0603 SableOS: Network Manager in Settings > Apps > Network access
- 0604 SableOS: read-only App security & privacy page
- 0901 SableOS: Keyboard light info in Settings > Keyboard
- 1001 SableOS: Sable entries in Settings, Display and App info
- 1002 1002-SableOS-keyboard-type-ahead-on-the-Settings-homepage.patch
- 1101 1101-SableOS-Mobile-Manager-App-lock-Focus-mode-and-profi.patch
- 1102 1102-SableOS-Mobile-Manager-App-lock-Focus-mode-and-Priva.patch
- 1103 SableOS: Settings > Sable holds Sable Setup only
- 1201 SableOS: Settings-wide Sable focus ring
- 1202 SableOS: Keyboard & input homepage row, Sable Tools links and key hint
- 1203 SableOS: hide the LineageOS version row in About phone
- 1204 SableOS: App Control Center, Freezer and Student mode model
- 1205 SableOS: App Control Center, Freezer and Student mode; no App lock
- 1206 SableOS: turn on the cloned apps page for Dual Apps
- 1207 SableOS: Connected devices card hub
- 1208 SableOS: App blocker on Android's Restricted background mode
- 1209 SableOS: security changes seen on this phone and tracker signals on the App security page
- 1301 SableOS: battery voltage row and the Q25's learned health facts
- 1401 SableOS: Settings homepage row opens Sable Setup directly
- 1501 SableOS: Keyboard firmware screen and *#*#1122#*#* on the Q25
- 1601 SableOS: Text size choice in Settings > Display
- 1602 SableOS: Lock screen peek row in Settings > Security
- 1603 SableOS: size tiles for Display, Text and Lock screen
- 1604 SableOS: Sable settings page in one list
- 1605 SableOS: Trackpad Keys or Pointer in Settings > Keyboard
- 1606 SableOS: Picture Password row on the Sable page
- 1607 SableOS: Settings top level in BlackBerry 10 order
- 1608 SableOS: Deep sleeping apps, with Put unused apps to sleep
- 1609 SableOS: one blue accent and no accent row (2.0 black and white)
- 1610 SableOS: Double-tap to wake switch in Settings > Display
- 1611 SableOS: Settings top level in ten BlackBerry 10 rows
- 1612 SableOS: no Attention row on the Sable page
- 1613 SableOS: Picture Password row in Settings > Security
- 1614 SableOS: near-grey system palette (black and white 2.0)
- 1615 SableOS: Wipe after 10 failed tries switch in Settings > Security
- 1616 SableOS: Sable Audio, the Bluetooth codec picker
- 1617 SableOS: Key remapping in Settings > Keyboard
- 1618 SableOS: the Private Space note says Sable Launcher
- 1619 SableOS: Settings top level is one flat list on BlackBerry 7

</details>

<details><summary>packages/apps/SetupWizard (31)</summary>

- 0401 0401-SableOS-keyboard-first-focus-in-setup-steps.patch
- 0402 0402-SableOS-Sable-Keyboard-readiness-step-before-Wi-Fi-a.patch
- 0403 0403-SableOS-Esc-returns-to-the-previous-setup-step.patch
- 0404 0404-SableOS-keyboard-test-step-after-keyboard-readiness.patch
- 0405 SableOS: privacy summary step after Wi-Fi
- 0406 0406-SableOS-Sable-accent-on-the-Theme-step-Theme-after-P.patch
- 0407 0407-SableOS-Ready-to-start-text-on-the-final-setup-page.patch
- 0408 SableOS: drop the LineageOS features step
- 0409 SableOS: navy cards and a cyan focus ring in setup
- 0410 SableOS: Step n of N on every owner setup step
- 0411 SableOS: keyboard check on Welcome, keyboard steps only when needed
- 0412 SableOS: Restore hidden until validated (fail-closed)
- 0413 SableOS: Ready screen rows as the setup design shows them
- 0414 SableOS: keep Next on screen in short windows
- 0415 SableOS: text size choice on the Theme step
- 0416 SableOS: Sable apps step, Full, Essentials or Stock
- 0417 SableOS: Look step, BlackBerry 10 or BlackBerry 7 style
- 0418 SableOS: no accent or pure black choice on the Theme step (2.0)
- 0419 SableOS: keyboard-first black Ready screen, no swipe-up arrow
- 0420 SableOS: Use Sable Keyboard button in the Keyboard step
- 0421 SableOS: Sable apps step follows the setup tiers
- 0422 SableOS: per-app check boxes on the Sable apps step
- 0423 SableOS: apply the Sable apps choice before the step moves on
- 0424 SableOS: Sable apps step no longer lists games
- 0425 SableOS: reopen the Sable apps step from Sable Setup
- 0426 SableOS: Look step is the Profile step and no longer mentions dials
- 0427 SableOS: the Sable apps step can see every installed app
- 0428 SableOS: the Ready screen says Sable Launcher
- 0429 SableOS: keep the footer bar on screen in short windows
- 0430 SableOS: short-window bar pins under the expressive scroll frame
- 0431 SableOS: Mail is a Core app and stays on in Essentials

</details>

<details><summary>packages/overlays/Lineage (1)</summary>

- 0101 SableOS: Source Sans 3 families in the product font customization

</details>
