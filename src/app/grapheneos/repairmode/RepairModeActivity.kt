package app.grapheneos.repairmode

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class RepairModeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme)
        DynamicColors.applyToActivityIfAvailable(this)

        setContentView(R.layout.repair_mode)
        setTitle(R.string.app_label)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val isInRepairMode = isInRepairMode(this)

        val actionButton = findViewById<ExtendedFloatingActionButton>(R.id.action_button)
        val icon = findViewById<ImageView>(R.id.repair_mode_icon)
        val description = findViewById<TextView>(R.id.repair_mode_description)
        val moreInfo = findViewById<TextView>(R.id.repair_mode_more_info)

        if (isInRepairMode) {
            actionButton.setText(R.string.exit_repair_mode)
            icon.visibility = View.GONE
            description.setText(R.string.repair_mode_description)
            moreInfo.visibility = View.GONE
        } else {
            actionButton.setText(R.string.enter_repair_mode)
            icon.visibility = View.VISIBLE
            description.setText(R.string.repair_mode_exit_description)
            moreInfo.visibility = View.VISIBLE
        }

        actionButton.setOnClickListener { view ->
            actionButton.isEnabled = false

            if (isInRepairMode) {
                setRepairModeDisabled(this)
                return@setOnClickListener
            }
            if (!isEnoughFreeSpaceAvailable()) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.repair_mode_not_allowed_low_storage)
                    .setPositiveButton(R.string.okay, null)
                    .setOnDismissListener { actionButton.isEnabled = true }
                    .show()
                return@setOnClickListener
            }

            actionButton.visibility = View.GONE
            Snackbar.make(view, R.string.restart_enter_repair_mode_message, Snackbar.LENGTH_LONG)
                .show()
            Handler(Looper.getMainLooper()).postDelayed({ setRepairModeEnabled(this) }, 2000)
        }
    }

}
