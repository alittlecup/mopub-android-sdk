package mobi.idealabs.ads.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class AdsPlugin : Plugin<Project> {

    public override fun apply(project: Project) {
        println("==================")
        println("Inject Ads Points Start")
        var extension = project.extensions.findByType(LibraryExtension::class.java)
        extension?.registerTransform(
            AdsTransform(
                project
            )
        )
        println("==================")

    }
}
