package mobi.idealabs.ads.core.bean

sealed class IVTUserLevel(val level: Int)
object UndefinedUser : IVTUserLevel(-1)
object ViciousUser : IVTUserLevel(1)
object WorseUser : IVTUserLevel(2)
object SuperiorUser : IVTUserLevel(0)