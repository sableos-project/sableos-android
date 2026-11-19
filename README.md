# SableOS Android apps

The SableOS application source, one folder per app under `apps/`. This is the source of truth: edit it
directly. It holds only SableOS code, not LineageOS or the Android tree.

| Folder | What it is |
| --- | --- |
| `apps/android` | Our own apps (Gradle project): Hub, Browser, Calendar, Tasks, Notes, Calculator, Weather, Media, Messages, Assistant, text reader, design library |
| `apps/titan2-platform` | Platform apps: keyboard, camera, setup, vault, profiles, spaces, compass, tools and others |
| `apps/reader` | Sable Reader |
| `apps/launcher` | Sable Launcher (home, Hub screens), patched TenSeven source |
| `apps/mail` | Sable Mail, patched Thunderbird source |
| `apps/keymapper`, `apps/davx5`, `apps/icsx5`, `apps/connectbot`, `apps/espeak`, `apps/textreader` | Patched third-party apps |

Device-specific values (Q25 screen, key maps, app tiers) will move into `devices/<name>/` as they are found.
