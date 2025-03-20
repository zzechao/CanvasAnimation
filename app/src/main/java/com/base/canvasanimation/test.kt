package com.base.canvasanimation

/**
 * @author zzechao
 * @date 2025/3/20 14:56
 */
interface Device {
    fun boot()       // 开机
    fun shutdown()  // 关机
    fun diagnose()  // 诊断
}

class DefaultDevice : Device {
    override fun boot() = println("Default booting...")
    override fun shutdown() = println("Default shutting down...")
    override fun diagnose() = println("Running diagnostics...")
}

class CustomDevice(private val name: String) : Device by DefaultDevice() {
    // 覆盖 `boot()` 方法，使用自己的实现
    override fun boot() {
        println("CustomDevice '$name' is booting with fancy animation...")
    }

    // 其他方法（`shutdown()` 和 `diagnose()`）依然委托给 DefaultDevice
}


fun main() {
    val device = CustomDevice("MyDevice")
    device.boot()      // 输出自定义实现
    device.shutdown() // 输出 DefaultDevice 的实现
    device.diagnose() // 输出 DefaultDevice 的实现
}