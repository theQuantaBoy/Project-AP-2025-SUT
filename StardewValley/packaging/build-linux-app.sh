#!/usr/bin/env bash
# Builds a portable, self-contained Linux build of the game client (bundled
# JRE, no separate Java install required). Output: packaging/dist/StardewValley/
# as a ready-to-run folder, plus a matching .tar.gz next to it.
#
# Usage: ./packaging/build-linux-app.sh
#
# To run the result:      packaging/dist/StardewValley/StardewValley
# To get a taskbar icon:  packaging/dist/StardewValley/install-desktop-entry.sh
#
# Notes:
# - jpackage must be run with the same JDK you want bundled into the app.
#   Set JAVA_HOME to a JDK 21+ install if the default `java` on your PATH
#   doesn't have jpackage (`java -version` / `which jpackage` to check).
# - This project's core/build.gradle pulls in both the LWJGL3 backend (used)
#   and an old LWJGL2 backend (gdx-backend-lwjgl, apparently unused - probably
#   a leftover dependency) and its dependencies (jinput, lwjgl-platform 2.x).
#   Both declare classes under the `org.lwjgl` package, and having both on the
#   classpath at once trips a JVM "sealing violation" that only surfaces under
#   jpackage's stricter classpath handling (Gradle's own run task doesn't hit
#   it). This script filters those out, along with native jars for platforms
#   other than linux-x86_64 (gdx-platform's *-natives-desktop.jar bundles all
#   three OSes' natives in one jar and is fine to keep as-is).
# - The game reads/writes "music/" and "saves/" as paths relative to the
#   process's current working directory (see RadioPlayer.java, SQLiteUtil.java),
#   not relative to the app's install location. A raw jpackage launcher would
#   make save-file location depend on wherever the user happened to run it
#   from. So the top-level `StardewValley` entry point here is a small wrapper
#   that cds into its own folder first, making the app fully self-contained
#   and portable no matter where it's extracted to or launched from.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
APP_NAME="StardewValley"
APP_VERSION="1.0.0"
DESKTOP_ID="io.github.thequantaboy.stardewvalley"

WORK_DIR="$SCRIPT_DIR/.work"
INPUT_DIR="$WORK_DIR/jars"
DIST_DIR="$SCRIPT_DIR/dist"
APP_DIR="$DIST_DIR/$APP_NAME"

JPACKAGE_BIN="${JPACKAGE_BIN:-jpackage}"
if ! command -v "$JPACKAGE_BIN" >/dev/null 2>&1; then
  echo "jpackage not found. Set JAVA_HOME to a JDK 21+ (bundled JDK 14+) install, or set JPACKAGE_BIN to its full path." >&2
  exit 1
fi

echo "==> Building jars"
cd "$ROOT_DIR"
./gradlew :core:jar :lwjgl3:jar --console=plain

echo "==> Gathering runtime classpath"
rm -rf "$WORK_DIR"
mkdir -p "$INPUT_DIR"

CP_LIST_FILE="$WORK_DIR/classpath.txt"
cat > "$WORK_DIR/print-classpath.gradle" <<'EOF'
allprojects {
    tasks.register("printRuntimeClasspath") {
        doLast {
            if (project.configurations.findByName("runtimeClasspath")) {
                project.configurations.runtimeClasspath.files.each { println it.absolutePath }
            }
        }
    }
}
EOF
./gradlew -I "$WORK_DIR/print-classpath.gradle" :lwjgl3:printRuntimeClasspath --console=plain -q > "$CP_LIST_FILE"

while IFS= read -r jarpath; do
  [ -f "$jarpath" ] && cp -n "$jarpath" "$INPUT_DIR/"
done < "$CP_LIST_FILE"
cp "$ROOT_DIR/lwjgl3/build/libs/lwjgl3.jar" "$INPUT_DIR/"
cp "$ROOT_DIR/core/build/libs/core-1.0.jar" "$INPUT_DIR/"

echo "==> Filtering out non-linux-x86_64 native jars and the unused LWJGL2/jinput jars"
cd "$INPUT_DIR"
rm -f ./*natives-windows*.jar ./*natives-macos*.jar ./*natives-osx*.jar ./*-arm32.jar ./*-arm64.jar
rm -f jinput-2.0.5.jar jinput-platform-2.0.5-natives-linux.jar jutils-1.0.0.jar \
      lwjgl-2.9.3.jar lwjgl-platform-2.9.3-natives-linux.jar
cd "$ROOT_DIR"

echo "==> Running jpackage"
rm -rf "$DIST_DIR"
mkdir -p "$DIST_DIR"
"$JPACKAGE_BIN" \
  --type app-image \
  --input "$INPUT_DIR" \
  --main-jar lwjgl3.jar \
  --main-class ap.project.lwjgl3.Lwjgl3Launcher \
  --name "$APP_NAME" \
  --app-version "$APP_VERSION" \
  --icon "$ROOT_DIR/core/assets/icons/icon-256.png" \
  --dest "$DIST_DIR"

echo "==> Fixing up the working directory"
# NOTE: jpackage's native launcher binary (bin/$APP_NAME) has its own name
# baked in to find its matching lib/app/$APP_NAME.cfg - leave it untouched.
# Our wrapper lives at the top level (a different path, $APP_DIR/$APP_NAME
# vs $APP_DIR/bin/$APP_NAME) so there's no name clash despite sharing a name.
# NOTE: deliberately NOT bundling core/assets/music/*.ogg here - those tracks
# look like real, non-original songs (e.g. by the rapper Lucki) rather than
# licensed/original game music, so they don't belong in a public release.
# The game already handles a missing/empty music/ folder gracefully.
cp "$ROOT_DIR/core/assets/icons/icon-256.png" "$APP_DIR/icon.png"

cat > "$APP_DIR/$APP_NAME" <<EOF
#!/usr/bin/env bash
# Wrapper so saves/music always resolve next to this folder, regardless of
# where it was extracted to or what directory it's launched from.
cd "\$(cd "\$(dirname "\$(readlink -f "\$0")")" && pwd)"

# GLFW's native Wayland backend doesn't reliably report an app identity that
# desktop environments can match to a .desktop file/icon (a known GLFW/Wayland
# limitation - same class of issue Minecraft has had on Linux for years). GLFW
# picks its backend based on \$WAYLAND_DISPLAY at startup, so unsetting it here
# makes the app run through XWayland (X11 compatibility) instead, where the
# icon/taskbar matching in install-desktop-entry.sh actually works. Harmless
# for a fixed-resolution 2D game; remove this line if you'd rather run native
# Wayland and don't care about the taskbar icon.
unset WAYLAND_DISPLAY

exec "./bin/$APP_NAME" "\$@"
EOF
chmod +x "$APP_DIR/$APP_NAME"

echo "==> Adding optional desktop-integration scripts"
cat > "$APP_DIR/install-desktop-entry.sh" <<EOF
#!/usr/bin/env bash
# Optional: adds a taskbar/app-menu icon and entry for this app (per-user,
# no root needed). Not required to play - just nicer if you're keeping it
# installed. Needed on GNOME/Wayland in particular, which (unlike X11) only
# shows a game's real icon if there's a matching installed .desktop file.
set -euo pipefail
APP_DIR="\$(cd "\$(dirname "\$(readlink -f "\$0")")" && pwd)"
ICON_DIR="\$HOME/.local/share/icons/hicolor/256x256/apps"
APPS_DIR="\$HOME/.local/share/applications"
mkdir -p "\$ICON_DIR" "\$APPS_DIR"
cp "\$APP_DIR/icon.png" "\$ICON_DIR/$DESKTOP_ID.png"
cat > "\$APPS_DIR/$DESKTOP_ID.desktop" <<DESKTOP
[Desktop Entry]
Type=Application
Name=Stardew Valley
Comment=Farming/life-sim game, AP course project
Exec=\$APP_DIR/$APP_NAME
Icon=\$APP_DIR/icon.png
Terminal=false
Categories=Game;
StartupWMClass=$APP_NAME
DESKTOP
command -v update-desktop-database >/dev/null 2>&1 && update-desktop-database "\$APPS_DIR" || true
command -v gtk-update-icon-cache >/dev/null 2>&1 && gtk-update-icon-cache "\$HOME/.local/share/icons/hicolor" || true
echo "Installed. The game should now show its real icon in your app menu/taskbar."
EOF
chmod +x "$APP_DIR/install-desktop-entry.sh"

cat > "$APP_DIR/uninstall-desktop-entry.sh" <<EOF
#!/usr/bin/env bash
set -euo pipefail
rm -f "\$HOME/.local/share/icons/hicolor/256x256/apps/$DESKTOP_ID.png"
rm -f "\$HOME/.local/share/applications/$DESKTOP_ID.desktop"
command -v update-desktop-database >/dev/null 2>&1 && update-desktop-database "\$HOME/.local/share/applications" || true
echo "Removed the app-menu entry and icon. (This folder itself is untouched - delete it manually to fully uninstall.)"
EOF
chmod +x "$APP_DIR/uninstall-desktop-entry.sh"

echo "==> Creating tarball"
cd "$DIST_DIR"
tar -czf "$APP_NAME-linux-x86_64.tar.gz" "$APP_NAME"

echo
echo "Done."
echo "  Folder:  $APP_DIR/"
echo "  Tarball: $DIST_DIR/$APP_NAME-linux-x86_64.tar.gz"
echo "  Run:     $APP_DIR/$APP_NAME"
echo "  Icon:    $APP_DIR/install-desktop-entry.sh (optional)"
