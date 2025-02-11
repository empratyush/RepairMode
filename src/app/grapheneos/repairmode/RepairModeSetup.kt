package app.grapheneos.repairmode

import android.content.Context
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.os.image.DynamicSystemManager
import android.provider.Settings

private const val REBOOT_REASON = "Repair Mode"
private const val REPAIR_MODE_KEY = "repairmode.lock"
private const val STORAGE_LIMIT_IN_BYTES = 4294967296L //4GB

fun setRepairModeEnabled(context: Context) {
    val dsm = context.getSystemService(DynamicSystemManager::class.java)
    dsm.remove()
    dsm.startInstallation(REPAIR_MODE_KEY)
    dsm.createPartition("userdata", STORAGE_LIMIT_IN_BYTES, false)
    dsm.closePartition()
    dsm.finishInstallation()
    dsm.setEnable(true, false)
    putRepairModeEnabledSettings(true, context)
    reboot(context)
}

fun setRepairModeDisabled(context: Context) {
    val dsm = context.getSystemService(DynamicSystemManager::class.java)
    dsm.remove()
    putRepairModeEnabledSettings(false, context)
    reboot(context)
}


fun isInRepairMode(context: Context): Boolean {
    val dsm = context.getSystemService(DynamicSystemManager::class.java)
    return dsm.isInUse() && REPAIR_MODE_KEY == dsm.getActiveDsuSlot()
}

fun reboot(context: Context) {
    context.getSystemService(PowerManager::class.java).reboot(REBOOT_REASON)
}

private fun putRepairModeEnabledSettings(isEnabled: Boolean, context: Context) {
    Settings.Global.putInt(
        context.getContentResolver(),
        Settings.Global.REPAIR_MODE_ACTIVE,
        if (isEnabled) 1 else 0
    )
}

fun isEnoughFreeSpaceAvailable(): Boolean {
    val dataDirPath = Environment.getDataDirectoryPath()
    val freeSpaceInBytes = StatFs(dataDirPath).availableBytes
    return freeSpaceInBytes >= STORAGE_LIMIT_IN_BYTES
}

