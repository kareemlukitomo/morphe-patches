package app.kareem.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    val COMPATIBILITY_INSTAGRAM =
        Compatibility(
            name = "Instagram",
            packageName = "com.instagram.android",
            apkFileType = ApkFileType.APKM,
            appIconColor = 0xFC483C,
            targets =
                listOf(
                    AppTarget(
                        version = "423.0.0.47.66",
                        description = "Instagram Stable version (all archs)",
                    ),
                    AppTarget(
                        version = "425.0.0.0.0",
                        description = "Instagram Alpha version (arm64-v8a only)",
                        isExperimental = true,
                    ),
                ),
        )
}
