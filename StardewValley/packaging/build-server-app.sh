#!/usr/bin/env bash
# Builds a portable, self-contained Linux build of the multiplayer server
# (bundled JRE, no separate Java install required). Output:
# packaging/dist/StardewValleyServer/ as a ready-to-run folder, plus a
# matching .tar.gz next to it.
#
# Usage: ./packaging/build-server-app.sh
# To run:  packaging/dist/StardewValleyServer/StardewValleyServer
#
# This is a headless console app (GameServer.java uses libGDX's
# HeadlessApplication and never touches Gdx.gl - confirmed via
# ServerMapLoader.java, which deliberately loads maps without real textures).
# So unlike the client, it needs none of LWJGL3/GLFW's graphics natives -
# just core's own runtime classpath.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
APP_NAME="StardewValleyServer"
APP_VERSION="1.0.0"

WORK_DIR="$SCRIPT_DIR/.work-server"
INPUT_DIR="$WORK_DIR/jars"
DIST_DIR="$SCRIPT_DIR/dist"
APP_DIR="$DIST_DIR/$APP_NAME"

JPACKAGE_BIN="${JPACKAGE_BIN:-jpackage}"
if ! command -v "$JPACKAGE_BIN" >/dev/null 2>&1; then
  echo "jpackage not found. Set JAVA_HOME to a JDK 21+ (bundled JDK 14+) install, or set JPACKAGE_BIN to its full path." >&2
  exit 1
fi

echo "==> Building jar"
cd "$ROOT_DIR"
./gradlew :core:jar --console=plain

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
./gradlew -I "$WORK_DIR/print-classpath.gradle" :core:printRuntimeClasspath --console=plain -q > "$CP_LIST_FILE"

while IFS= read -r jarpath; do
  [ -f "$jarpath" ] && cp -n "$jarpath" "$INPUT_DIR/"
done < "$CP_LIST_FILE"
cp "$ROOT_DIR/core/build/libs/core-1.0.jar" "$INPUT_DIR/"

echo "==> Filtering out non-linux-x86_64 native jars"
cd "$INPUT_DIR"
rm -f ./*natives-windows*.jar ./*natives-macos*.jar ./*natives-osx*.jar ./*-arm32.jar ./*-arm64.jar
cd "$ROOT_DIR"

echo "==> Running jpackage"
rm -rf "$DIST_DIR/$APP_NAME"
mkdir -p "$DIST_DIR"
"$JPACKAGE_BIN" \
  --type app-image \
  --input "$INPUT_DIR" \
  --main-jar core-1.0.jar \
  --main-class ap.project.network.server.GameServer \
  --name "$APP_NAME" \
  --app-version "$APP_VERSION" \
  --dest "$DIST_DIR"

echo "==> Fixing up the working directory (server writes saves/ next to itself)"
# NOTE: jpackage's native launcher binary (bin/$APP_NAME) has its own name
# baked in to find its matching lib/app/$APP_NAME.cfg - leave it untouched.
# Our wrapper lives at the top level (a different path) so there's no clash.
cat > "$APP_DIR/$APP_NAME" <<EOF
#!/usr/bin/env bash
# Wrapper so save data always lands next to this folder, regardless of where
# it was extracted to or what directory it's launched from. This is a
# console app - run it from a terminal to see server logs.
cd "\$(cd "\$(dirname "\$(readlink -f "\$0")")" && pwd)"
exec "./bin/$APP_NAME" "\$@"
EOF
chmod +x "$APP_DIR/$APP_NAME"

echo "==> Creating tarball"
cd "$DIST_DIR"
tar -czf "$APP_NAME-linux-x86_64.tar.gz" "$APP_NAME"

echo
echo "Done."
echo "  Folder:  $APP_DIR/"
echo "  Tarball: $DIST_DIR/$APP_NAME-linux-x86_64.tar.gz"
echo "  Run (from a terminal): $APP_DIR/$APP_NAME"
