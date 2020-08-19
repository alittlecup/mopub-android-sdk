package mobi.idealabs.ads.gradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import mobi.idealabs.ads.asm.MopubClassChecker
import mobi.idealabs.ads.asm.MopubMethodAdapter
import mobi.idealabs.ads.inject.AdLoaderAdapter
import mobi.idealabs.ads.inject.AdLoaderRewardedVideoAdapter
import mobi.idealabs.ads.inject.AdViewControllerAdapter
import mobi.idealabs.ads.inject.NativeAdAdapter
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class AdsTransform(val project: Project) : Transform() {

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        println("===== ASM Transform =====")
        println("${transformInvocation?.inputs}")
        println("${transformInvocation?.referencedInputs}")
        println("${transformInvocation?.outputProvider}")
        println("${transformInvocation?.isIncremental}")

        //当前是否是增量编译
        val isIncremental = transformInvocation?.isIncremental
        //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        val inputs = transformInvocation?.inputs
        //引用型输入，无需输出。
        val referencedInputs = transformInvocation?.referencedInputs

        //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        val outputProvider = transformInvocation?.outputProvider

        //遍历inputs Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        inputs?.forEach { input ->
            //遍历directoryInputs(文件夹中的class文件) directoryInputs代表着以源码方式参与项目编译的所有目录结构及其目录下的源码文件
            // 比如我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
            input.directoryInputs.forEach { directoryInput ->
                //文件夹中的class文件
                handDirectoryInput(directoryInput, outputProvider!!)
            }
            //遍历jar包中的class文件 jarInputs代表以jar包方式参与项目编译的所有本地jar包或远程jar包
            input.jarInputs.forEach { jarInput ->
                //处理jar包中的class文件
                handJarInput(jarInput, outputProvider!!)
            }
        }
    }

    override fun getName(): String {
        return AdsTransform::class.simpleName.toString()
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return hashSetOf(QualifiedContent.Scope.SUB_PROJECTS)
    }

    override fun isIncremental(): Boolean = true


    //遍历jarInputs 得到对应的class 交给ASM处理
    private fun handJarInput(jarInput: JarInput, outputProvider: TransformOutputProvider) {
        if (jarInput.file.absolutePath.endsWith(".jar")) {
            var jarName = jarInput.name
            val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length - 4)
            }
            val jarFile = JarFile(jarInput.file)
            val enumeration = jarFile.entries()
            val tmpFile = File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            //避免上次的缓存被重复插入
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            val jarOutputStream = JarOutputStream(FileOutputStream(tmpFile))
            //用于保存
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                val entryName = jarEntry.name
                val zipEntry = ZipEntry(entryName)
                val inputStream = jarFile.getInputStream(jarEntry)
                //需要插桩class 根据自己的需求来-------------
                if (isProcessClass(entryName)) {
                    //class文件处理
                    println("----------- jar class  <$entryName> -----------")
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(processClass(entryName, inputStream))
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            //结束
            jarOutputStream.close()
            jarFile.close()
            //获取output目录
            val dest = outputProvider.getContentLocation(
                jarName + md5Name,
                jarInput.contentTypes, jarInput.scopes, Format.JAR
            )
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }

    private fun buildTransformSrc(): String {
        val absolutePath = project.buildDir.absolutePath
        val path = project.path.removePrefix(":")
        return absolutePath.replace(
            path,
            "buildSrc"
        ) + "/intermediates/transforms/AdsTransform/class/"
    }

    //遍历directoryInputs  得到对应的class  交给ASM处理
    private fun handDirectoryInput(input: DirectoryInput, outputProvider: TransformOutputProvider) {
        val file = input.file
        if (file.isDirectory) {
            file.eachFileRecurse { item ->
                if (isProcessClass(item.name)) {
                    val code = processClass(item.name, FileInputStream(item))
                    val fos = FileOutputStream(file.parentFile.absolutePath + File.separator + name)
                    fos.write(code)
                    fos.close()
                }

            }
            val dest = outputProvider.getContentLocation(
                input.name,
                input.contentTypes,
                input.scopes,
                Format.DIRECTORY
            )
            FileUtils.copyDirectory(input.file, dest)
        }
    }

    private fun processClass(fileName: String, fileInputStream: InputStream): ByteArray {
        val soruceByte = fileInputStream.readBytes()
        val classReader = ClassReader(soruceByte)
        val classWriter = ClassWriter(classReader, 0)
        //创建类访问器   并交给它去处理
        val adapter = generateAdapter(fileName, classWriter)
        classReader.accept(adapter, ClassReader.SKIP_FRAMES)
        val byteArray = classWriter.toByteArray()
        val filePath = buildTransformSrc() + fileName

        val lastIndexPoint = filePath.lastIndexOf(".")
        val modifyFilePath = if (lastIndexPoint != -1) {
            filePath.replaceRange(lastIndexPoint, filePath.length, "Modify.class")
        } else {
            filePath
        }
        var fileOutputStream = FileUtils.openOutputStream(File(filePath))
        fileOutputStream.write(soruceByte)
        fileOutputStream.close()

        fileOutputStream = FileUtils.openOutputStream(File(modifyFilePath))
        fileOutputStream.write(byteArray)
        fileOutputStream.close()
        return byteArray
    }

    private fun generateAdapter(fileName: String, classWriter: ClassWriter) =
        if (fileName.contains("AdViewController")) AdViewControllerAdapter(
            classVisitor = classWriter,
            className = fileName.removeSuffix(".class")
        ) else if (fileName.contains("NativeAd")) NativeAdAdapter(
            classVisitor = classWriter,
            className = fileName.removeSuffix(".class")
        ) else if (fileName.contains("AdLoaderRewardedVideo")) AdLoaderRewardedVideoAdapter(
            classVisitor = classWriter,
            className = fileName.removeSuffix(".class")
        ) else if (fileName.contains("AdLoader")) AdLoaderAdapter(
            classVisitor = classWriter,
            className = fileName.removeSuffix(".class")
        ) else MopubMethodAdapter(
            api = Opcodes.ASM7,
            classVisitor = classWriter
        )

    private fun isProcessClass(name: String): Boolean {
        return MopubClassChecker.isModifyClass(name)

    }

    fun File.eachFileRecurse(function: (File) -> Unit) {
        check(this.isDirectory)
        val listFiles = this.listFiles()
        if (listFiles != null) {
            for (file in listFiles) {
                if (file.isDirectory) {
                    function(file)
                    file.eachFileRecurse(function)
                } else {
                    function(file)
                }
            }
        }
    }
}


